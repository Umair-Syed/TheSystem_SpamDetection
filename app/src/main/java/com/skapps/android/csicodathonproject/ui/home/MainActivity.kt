package com.skapps.android.csicodathonproject.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.skapps.android.csicodathonproject.R
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
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val usersCollectionRef = db.collection(KEY_COLLECTION_USERS)
    private var user = FirebaseAuth.getInstance().currentUser

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var navController: NavController? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        setUpNavigationDrawer()

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
    }


    private fun setUpNavigationDrawer() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        mAppBarConfiguration = AppBarConfiguration
                .Builder(R.id.homeFragment, R.id.allUsersFragment) //destinations
                .setOpenableLayout(binding.drawerLayout)
                .build()

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        if(navController != null){
            NavigationUI.setupActionBarWithNavController(
                this,
                navController!!,
                mAppBarConfiguration!!
            )
            NavigationUI.setupWithNavController(navigationView, navController!!)

        }
        navigationView.setNavigationItemSelectedListener(this)

        val header = navigationView.getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.navName)
        val email = header.findViewById<TextView>(R.id.navEmail)
        val nameEmail = arrayOfNulls<String>(2)

        //snapshot Listener, to show email and name in NavDrawer Header
        usersCollectionRef.document(user!!.uid)
            .addSnapshotListener(this, object : EventListener<DocumentSnapshot> {
                override fun onEvent(
                    documentSnapshot: DocumentSnapshot?,
                    e: FirebaseFirestoreException?
                ) {
                    if (e != null) {
                        Log.d(TAG, e.toString())
                        return
                    }
                    if (documentSnapshot?.exists()!!) {
                        nameEmail[0] = documentSnapshot.getString(KEY_USER_DOC_NAME) //name
                        nameEmail[1] = documentSnapshot.getString(KEY_USER_DOC_EMAIL) //email
                        name.text = nameEmail[0]
                        email.text = nameEmail[1]
                    }
                }
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = NavigationUI.onNavDestinationSelected(
            item,
            navController!!
        )

        if (!handled) {
            when (item.itemId) {
                R.id.all_users -> {
                    TODO("need to implement")
                }
                R.id.blocked_users -> {
                    TODO("need to implement")
                }
                R.id.spams -> {
                    TODO("need to implement")
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return handled
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.activity_main_drawer, menu)
//        val blockedUsersItem = menu?.findItem(R.id.blocked_users)
//        val spamItem = menu?.findItem(R.id.spams)
//
//        val dataStore: DataStore<Preferences> = createDataStore(
//            name = DATA_STORE_KEY
//        )
//        val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
//        val isAdminFlow: Flow<Boolean> = dataStore.data.map {
//            it[isAdminKey] ?: false
//        }
//
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
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