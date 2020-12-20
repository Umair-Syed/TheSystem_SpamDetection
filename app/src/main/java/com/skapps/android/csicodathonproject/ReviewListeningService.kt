package com.skapps.android.csicodathonproject

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.machinelearning.TextClassificationClient
import com.skapps.android.csicodathonproject.ui.CHANNEL_REVIEW_LISTENER
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS
import com.skapps.android.csicodathonproject.util.KEY_SUB_COLLECTION_REVIEWS
import kotlinx.coroutines.launch

/**
 * Created by Syed Umair on 18/12/2020.
 */

private const val TAG = "ReviewListeningService"
const val SERVICE_NOTIFICATION_ID = 2

class ReviewListeningService(): LifecycleService() {

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat

    private val db = FirebaseFirestore.getInstance()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        notificationManager = NotificationManagerCompat.from(this)

        db.collectionGroup(KEY_SUB_COLLECTION_REVIEWS).addSnapshotListener { docSnapshots, error ->
            if( error != null){
                stopSelf()
            }

            val spamDetails = MutableLiveData<List<String>>()
            spamDetails.observe(this){
                // logically detected a spam
                val name = it[0]
                val heading = it[1]
                val description = it[2]
                val rid = it[3]

                lifecycleScope.launch {

                    val isSpam = useClassificationModel(description)

                    if(isSpam){
                        Log.d(TAG, "onStartCommand: isSpam = $isSpam -- review == $heading")
                        notificationBuilder = notificationForReview(name, heading, rid,
                            "Alert! Spam detected") // both logically(multiple reviews by same user) and using ML
                        notificationManager.notify(SERVICE_NOTIFICATION_ID, notificationBuilder.build())

                    }else{
                        notificationBuilder = notificationForReview(name, heading, rid,
                            "Potential Spam detected!") // only logically(multiple reviews by same user)
                        notificationManager.notify(SERVICE_NOTIFICATION_ID, notificationBuilder.build())
                    }
                }
                spamDetails.removeObservers(this@ReviewListeningService)
            }


            if (docSnapshots != null) {
                for(docSnap in docSnapshots.documentChanges){

                    val pid = docSnap.document.getString("pid")
                    val uid = docSnap.document.getString("uid")

                    if (pid != null) {
                        val productReviews = db.collection(KEY_COLLECTION_PRODUCTS)
                            .document(pid).collection(KEY_SUB_COLLECTION_REVIEWS)

                        productReviews.whereEqualTo("uid",uid).get()
                            .addOnSuccessListener {
                                if(it.size() > 1){
                                    // means multiple reviews of same user(uid) on product (pid)
                                    spamDetails.postValue(listOf(
                                        docSnap.document.getString("name")?:"",
                                        docSnap.document.getString("heading")?:"",
                                        docSnap.document.getString("description")?:"",
                                        docSnap.document.id))

                                    for(doc in it){
                                        // marking all spams by this user
                                        productReviews.document(doc.id).update("spam", true)
                                        // potential spam detected
                                    }
                                }else{
                                    // Logically not spam. Now trying using classification model
                                    lifecycleScope.launch {
                                        val isSpam = useClassificationModel(
                                            docSnap.document.getString("description")?:"")
                                        if (isSpam) {
                                            Log.d(TAG, "onStartCommand: isSpam = $isSpam -- review == ${docSnap.document.getString("heading")}")

                                            productReviews.document(docSnap.document.id).update("spam", true)
                                            notificationBuilder = notificationForReview(
                                                docSnap.document.getString("name")?:"",
                                                docSnap.document.getString("heading")?:"",
                                                docSnap.document.id,
                                                "Potential Spam detected!"
                                            )
                                            notificationManager.notify(
                                                SERVICE_NOTIFICATION_ID,
                                                notificationBuilder.build()
                                            )
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }

        return START_STICKY
    }

    private fun notificationForReview(name:String, reviewHeading: String, rid: String, notTitle: String): NotificationCompat.Builder{

        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("rid", rid)
        val actionIntent = PendingIntent.getBroadcast(
            this,
            0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_REVIEW_LISTENER)
            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
            .setContentTitle(notTitle)
            .setContentText("\"$reviewHeading\" by $name")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .addAction(R.mipmap.ic_launcher, "Check", actionIntent)
    }

    private suspend fun useClassificationModel(description: String): Boolean{
        // using classification
        val classification = TextClassificationClient(this@ReviewListeningService)
        classification.load()
        val resultList = classification.classify(description)
        classification.unload()
        return resultList[0] < resultList[1] // 0 => truthful   1=>spam
    }

}