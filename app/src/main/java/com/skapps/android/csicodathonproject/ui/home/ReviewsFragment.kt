package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentReviewsBinding
import com.skapps.android.csicodathonproject.ui.dialog.WriteReviewDialog
import com.skapps.android.csicodathonproject.ui.login.DATA_STORE_KEY
import com.skapps.android.csicodathonproject.ui.login.PREF_KEY_IS_ADMIN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ReviewsFragment : Fragment(R.layout.fragment_reviews) {
    private lateinit var binding: FragmentReviewsBinding
    private val args by navArgs<ReviewsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReviewsBinding.bind(view)


        val dataStore: DataStore<Preferences> = requireActivity().createDataStore(
            name = DATA_STORE_KEY
        )
        val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
        val isAdminFlow: Flow<Boolean> = dataStore.data.map {
            it[isAdminKey] ?: false
        }

        lifecycleScope.launch {
            isAdminFlow.collect {isAdmin ->
                if(isAdmin){
                    binding.writeReviewBtn.visibility = View.VISIBLE

                    binding.writeReviewBtn.setOnClickListener {
                        val dialog = WriteReviewDialog(args.product)
                        dialog.show(requireActivity().supportFragmentManager, "WriteReviewDialog")
                    }
                }else{
                    binding.writeReviewBtn.visibility = View.GONE
                }
            }
        }


    }

}