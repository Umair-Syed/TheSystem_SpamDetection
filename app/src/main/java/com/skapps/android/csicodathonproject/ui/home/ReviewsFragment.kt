package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.data.models.Review
import com.skapps.android.csicodathonproject.data.viewmodels.HomeViewModel
import com.skapps.android.csicodathonproject.data.viewmodels.ReviewsViewModel
import com.skapps.android.csicodathonproject.databinding.FragmentReviewsBinding
import com.skapps.android.csicodathonproject.ui.dialog.WriteReviewDialog
import com.skapps.android.csicodathonproject.ui.login.DATA_STORE_KEY
import com.skapps.android.csicodathonproject.ui.login.PREF_KEY_IS_ADMIN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewsFragment : Fragment(R.layout.fragment_reviews), ReviewsListAdapter.ItemAdapterListener {
    private lateinit var binding: FragmentReviewsBinding
    private val args by navArgs<ReviewsFragmentArgs>()
    private val viewModel by viewModels<ReviewsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReviewsBinding.bind(view)

        if (activity != null) {
            (activity as MainActivity).supportActionBar?.title = "Reviews"
        }

        val dataStore: DataStore<Preferences> = requireActivity().createDataStore(
            name = DATA_STORE_KEY
        )
        val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
        val isAdminFlow: Flow<Boolean> = dataStore.data.map {
            it[isAdminKey] ?: false
        }

        lifecycleScope.launch {
            isAdminFlow.collect {isAdmin ->
                if(!isAdmin){
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

        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        viewModel.getReviews(args.product).observe(viewLifecycleOwner) {
            binding.reviewsRecyclerView.adapter = ReviewsListAdapter(requireContext(), it as ArrayList<Review>,this)
            if(it.isEmpty()){
                binding.emptyView.visibility = View.VISIBLE
            }else{
                binding.emptyView.visibility = View.GONE
            }

        }
    }

    override fun onItemClicked(review: Review) {
        val action = ReviewsFragmentDirections.actionReviewsFragmentToReviewDetailsFragment(review)
        findNavController().navigate(action)
    }

}