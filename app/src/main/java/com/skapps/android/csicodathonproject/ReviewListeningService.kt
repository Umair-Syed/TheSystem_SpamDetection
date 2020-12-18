package com.skapps.android.csicodathonproject

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.ui.CHANNEL_REVIEW_LISTENER

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

        db.collectionGroup("reviews").addSnapshotListener { docSnapshots, error ->
            if( error != null){
                stopSelf()
            }

            val spamDetails = MutableLiveData<List<String>>()
            spamDetails.observe(this){
                notificationBuilder = notificationForReview(it[0], it[1], it[2])
                notificationManager.notify(SERVICE_NOTIFICATION_ID, notificationBuilder.build())
                spamDetails.removeObservers(this)
            }



            if (docSnapshots != null) {
                for(docSnap in docSnapshots.documentChanges){

                    val pid = docSnap.document.getString("pid")
                    val uid = docSnap.document.getString("uid")

                    if (pid != null) {
                        val productReviews = db.collection("products")
                            .document(pid).collection("reviews")

                        productReviews.whereEqualTo("uid",uid).get()
                            .addOnSuccessListener {
                                if(it.size() > 1){
                                    // means multiple reviews of same user(uid) on product (pid)
                                    spamDetails.postValue(listOf(
                                        docSnap.document.getString("name")?:"",
                                        docSnap.document.getString("description")?:"",
                                        docSnap.document.id))

                                    for(doc in it){
                                        // marking all spams by this user
                                        productReviews.document(doc.id).update("spam", true)
                                        // potential spam detected
                                    }
                                }
                            }

                    }
                }
            }
        }

        return START_STICKY
    }

    private fun notificationForReview(name:String, reviewDes: String, rid: String): NotificationCompat.Builder{

        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("rid", rid)
        val actionIntent = PendingIntent.getBroadcast(
            this,
            0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_REVIEW_LISTENER)
            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
            .setContentTitle("Potential Spam Detected")
            .setContentText("\"$reviewDes\" by $name")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .addAction(R.mipmap.ic_launcher, "Check", actionIntent)
    }

}