package com.skapps.android.csicodathonproject.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.SUser
import com.skapps.android.csicodathonproject.databinding.FragmentLoginFourthBinding
import com.skapps.android.csicodathonproject.ui.home.MainActivity
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val TAG = "LoginFourthFragment"
class LoginFourthFragment : Fragment(R.layout.fragment_login_fourth) {
    private lateinit var binding: FragmentLoginFourthBinding

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollectionRef = db.collection(KEY_COLLECTION_USERS)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginFourthBinding.bind(view)

        val dataStore: DataStore<Preferences> = requireActivity().createDataStore(
            name = DATA_STORE_KEY
        )
        val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
        val isAdminFlow: Flow<Boolean> = dataStore.data.map {
            it[isAdminKey] ?: false
        }




        binding.submitBtn.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty() && isEmailValid(email)){
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    isAdminFlow.collect {isAdmin ->
                        Log.d(TAG, "onViewCreated: called $isAdmin")
                        // writing to fire store
                        if(user?.uid != null)
                        userCollectionRef.document(user.uid).set(SUser(name, email, isAdmin, false)).addOnSuccessListener {
                            userProfileChangeRequest {
                                displayName = name
                                user.updateProfile(this.build()).addOnSuccessListener {
                                    binding.progressBar.visibility = View.GONE
                                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                                    requireActivity().finish()
                                }
                            }
                        }.addOnFailureListener { e ->
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(activity, e.localizedMessage ?: "Something went wrong!", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, "onViewCreated: ${e.message}")
                        }
                    }
                }
            } else if (!isEmailValid(email)) {
                binding.email.error = "Invalid email!"
            } else {
                Toast.makeText(activity, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}