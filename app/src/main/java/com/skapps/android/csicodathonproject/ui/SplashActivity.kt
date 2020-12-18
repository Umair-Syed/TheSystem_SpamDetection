package com.skapps.android.csicodathonproject.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.ui.home.MainActivity
import com.skapps.android.csicodathonproject.ui.login.DATA_STORE_KEY
import com.skapps.android.csicodathonproject.ui.login.LoginActivity
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val PREF_KEY_IS_BLOCKED = "isBlocked"
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection(KEY_COLLECTION_USERS)
        val user = FirebaseAuth.getInstance().currentUser

        // Create data store instance
        val dataStore: DataStore<Preferences> = this.createDataStore(
            name = DATA_STORE_KEY
        )
        val isBlockedKey = preferencesKey<Boolean>(PREF_KEY_IS_BLOCKED)

        if(user != null){
            usersCollection.document(user.uid).get().addOnSuccessListener {
                if(it.exists()){
                    // edit preference, for future use
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.edit { settings ->
                            settings[isBlockedKey] = it.getBoolean("blocked") ?: false
                        }
                    }
                }
            }

            //getting isBlocked from data store in different thread
            val isBlockedFlow: Flow<Boolean> = dataStore.data.map {
                it[isBlockedKey] ?: false
            }
            lifecycleScope.launch {
                isBlockedFlow.collect { isBlocked ->
                    if (!isBlocked){
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }else{
                        val builder = AlertDialog.Builder(this@SplashActivity)
                        builder.apply {
                            setTitle("Your account has been blocked by the admin.")
                            setNegativeButton("Close app"){ _, _ ->
                                finishAndRemoveTask()
                            }
                            setPositiveButton("Contact Admin") { _, _ ->
                                supportAdmin()
                            }
                            show()
                        }
                    }
                }
            }

        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun supportAdmin() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        val TO = arrayOf("admin@thesystem.com")
        val CC = arrayOf("")
        emailIntent.setPackage("com.google.android.gm")
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User unblock request")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No email client installed!", Toast.LENGTH_SHORT).show()
        }
    }
}