package com.skapps.android.csicodathonproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by Syed Umair on 18/12/2020.
 */

private const val TAG = "NotificationReceiver"
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: called____ ")


    }

}