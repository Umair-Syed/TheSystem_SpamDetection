package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Review
import com.skapps.android.csicodathonproject.data.viewmodels.SpamsViewModel
import com.skapps.android.csicodathonproject.databinding.FragmentSpamsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpamsFragment : Fragment(R.layout.fragment_spams), ReviewsListAdapter.ItemAdapterListener {
    private lateinit var binding: FragmentSpamsBinding
    private val viewModel by viewModels<SpamsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSpamsBinding.bind(view)


        binding.spamsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        viewModel.getSpams().observe(viewLifecycleOwner) {
            binding.spamsRecyclerView.adapter = ReviewsListAdapter(requireContext(), it as ArrayList<Review>,this)
            if(it.isEmpty()){
                binding.emptyView.visibility = View.VISIBLE
            }else{
                binding.emptyView.visibility = View.GONE
            }

        }
    }

    override fun onItemClicked(review: Review) {
        val action = SpamsFragmentDirections.actionSpamsFragmentToReviewDetailsFragment(review)
        findNavController().navigate(action)
    }

}