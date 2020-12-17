package com.skapps.android.csicodathonproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentLoginFirstBinding
import kotlinx.coroutines.launch

const val PREF_KEY_IS_ADMIN = "isAdmin"
const val DATA_STORE_KEY = "settings"
class LoginFirstFragment : Fragment(R.layout.fragment_login_first) {
    private lateinit var binding: FragmentLoginFirstBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginFirstBinding.bind(view)

        // Create data store instance
        val dataStore: DataStore<Preferences> = requireActivity().createDataStore(
            name = DATA_STORE_KEY
        )

        binding.loginButton.setOnClickListener {
            val action =
                LoginFirstFragmentDirections.actionLoginFirstFragmentToLoginSecondFragment()
            findNavController().navigate(action)

            // edit preference
            val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
            lifecycleScope.launch {
                dataStore.edit { settings ->
                    settings[isAdminKey] = false
                }
            }
        }

        binding.loginAdmin.setOnClickListener {
            val action =
                LoginFirstFragmentDirections.actionLoginFirstFragmentToLoginSecondFragment()
            findNavController().navigate(action)

            // edit preference
            val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
            lifecycleScope.launch {
                dataStore.edit { settings ->
                    settings[isAdminKey] = true
                }
            }
        }

    }
}