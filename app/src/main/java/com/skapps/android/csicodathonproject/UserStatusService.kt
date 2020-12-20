package com.skapps.android.csicodathonproject

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS

/**
 * Created by Syed Umair on 20/12/2020.
 */

private const val TAG = "UserStatusService"
class UserStatusService(): Service() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d(TAG, "onStartCommand: called... currentuser == $currentUser")

        if(currentUser != null){
            db.collection(KEY_COLLECTION_USERS).document(currentUser.uid)
                .update("active", false).addOnSuccessListener {
                    stopSelf()
                }.addOnFailureListener {
                    stopSelf()
                }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


}