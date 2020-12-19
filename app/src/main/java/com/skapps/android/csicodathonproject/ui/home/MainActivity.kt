package com.skapps.android.csicodathonproject.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.ReviewListeningService
import com.skapps.android.csicodathonproject.databinding.ActivityMainBinding
import com.skapps.android.csicodathonproject.ui.login.DATA_STORE_KEY
import com.skapps.android.csicodathonproject.ui.login.LoginActivity
import com.skapps.android.csicodathonproject.ui.login.PREF_KEY_IS_ADMIN
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS
import com.skapps.android.csicodathonproject.util.KEY_USER_DOC_EMAIL
import com.skapps.android.csicodathonproject.util.KEY_USER_DOC_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val usersCollectionRef = db.collection(KEY_COLLECTION_USERS)
    private var user = FirebaseAuth.getInstance().currentUser
    private var isAdmin = false

    private lateinit var listener : NavController.OnDestinationChangedListener
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        // get isAdmin from data store
        val dataStore: DataStore<Preferences> = createDataStore(
            name = DATA_STORE_KEY
        )
        val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
        val isAdminFlow: Flow<Boolean> = dataStore.data.map {
            it[isAdminKey] ?: false
        }

        lifecycleScope.launch {
            isAdminFlow.collect {
                isAdmin = it
                setUpNavigationDrawer()
            }
        }

        setSupportActionBar(toolbar)

        val intent = Intent(this, LoginActivity::class.java)
        binding.logout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setTitle("Are you sure you want to sign out?")
                setNegativeButton("YES"){ _, _ ->
                    if(user != null){
                        FirebaseAuth.getInstance().signOut()
                        startActivity(intent)
                        finish()
                    }
                }
                setPositiveButton("NO") {
                        dialog, _ -> dialog.cancel()
                }
                show()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            isAdminFlow.collect { isAdmin ->
                if(isAdmin){
                    val serviceIntent = Intent(this@MainActivity, ReviewListeningService::class.java)
                    this@MainActivity.startService(serviceIntent)
                }
            }
        }
    }


    private fun setUpNavigationDrawer() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        navigationView.setupWithNavController(navController)

        mAppBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        setupActionBarWithNavController(navController, mAppBarConfiguration)


        val header = navigationView.getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.navName)
        val email = header.findViewById<TextView>(R.id.navEmail)
        val nameEmail = arrayOfNulls<String>(2)

        // show email and name in NavDrawer Header
        usersCollectionRef.document(user!!.uid).get()
            .addOnSuccessListener {documentSnapshot->
                if (documentSnapshot?.exists()!!) {
                    nameEmail[0] = documentSnapshot.getString(KEY_USER_DOC_NAME) //name
                    nameEmail[1] = documentSnapshot.getString(KEY_USER_DOC_EMAIL) //email
                    name.text = nameEmail[0]
                    email.text = nameEmail[1]
                }
            }

        // hide items if not admin
        val menu = navigationView.menu
        val blockedUsersItem = menu.findItem(R.id.blockedUsersFragment)
        val spamItem = menu.findItem(R.id.spamsFragment)
        Log.d(TAG, "setUpNavigationDrawer: isAdmin == $isAdmin")
        if(isAdmin){
            blockedUsersItem?.isVisible = true
            spamItem?.isVisible = true
        }else{
            blockedUsersItem?.isVisible = false
            spamItem?.isVisible = false
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp())
    }

    override fun onBackPressed() {
        // closes drawer when back button is clicked
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        // double tap back button to exit app
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}