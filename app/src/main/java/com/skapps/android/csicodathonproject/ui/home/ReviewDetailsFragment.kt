package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentReviewDetailsBinding
import com.skapps.android.csicodathonproject.ui.dialog.WriteReviewDialog
import com.skapps.android.csicodathonproject.ui.login.DATA_STORE_KEY
import com.skapps.android.csicodathonproject.ui.login.PREF_KEY_IS_ADMIN
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS
import com.skapps.android.csicodathonproject.util.KEY_SUB_COLLECTION_REVIEWS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class ReviewDetailsFragment : Fragment(R.layout.fragment_review_details) {
    private lateinit var binding: FragmentReviewDetailsBinding
    private val args by navArgs<ReviewDetailsFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = com.skapps.android.csicodathonproject.databinding.FragmentReviewDetailsBinding.bind(view)

        if (activity != null) {
            (activity as MainActivity).supportActionBar?.title = "Review"
        }


        binding.apply {
            name.text = args.review.name
            title.text = args.review.heading
            description.text = args.review.description
            ratingBar.rating = args.review.rating.toFloat()
        }


        val dataStore: DataStore<Preferences> = requireActivity().createDataStore(
            name = DATA_STORE_KEY
        )
        val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
        val isAdminFlow: Flow<Boolean> = dataStore.data.map {
            it[isAdminKey] ?: false
        }

        val db = FirebaseFirestore.getInstance()
        val reviewsCollection = db.collection(KEY_COLLECTION_PRODUCTS).document(args.review.pid)
            .collection(KEY_SUB_COLLECTION_REVIEWS)
        val usersCollection = db.collection(KEY_COLLECTION_USERS)

        lifecycleScope.launch {
            isAdminFlow.collect {isAdmin ->
                if(isAdmin){
                    binding.reviewControlRl.visibility = View.VISIBLE

                    // update review later
                    binding.removeReviewBtn.setOnClickListener {
                        reviewsCollection.document(args.review.rid).delete().addOnSuccessListener {
                            Toast.makeText(requireContext(), "Review deleted", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), it.localizedMessage ?: "Couldn't delete", Toast.LENGTH_SHORT).show()
                        }
                    }

                    binding.blockUserBtn.setOnClickListener {
                        usersCollection.document(args.review.uid).
                                update("blocked", true).addOnSuccessListener {
                            Toast.makeText(requireContext(), "User blocked", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Something went wrong! Try again later.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    binding.reviewControlRl.visibility = View.GONE

                }
            }
        }


    }
}