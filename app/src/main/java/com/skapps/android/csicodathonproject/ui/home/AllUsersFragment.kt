package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.SUser
import com.skapps.android.csicodathonproject.data.viewmodels.AllUsersViewModel
import com.skapps.android.csicodathonproject.databinding.FragmentAllUsersBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllUsersFragment : Fragment(R.layout.fragment_all_users) {
    private lateinit var binding: FragmentAllUsersBinding

    private val viewModel by viewModels<AllUsersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAllUsersBinding.bind(view)

        binding.allUsersRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        viewModel.getUsersList().observe(viewLifecycleOwner) {
            binding.allUsersRecyclerView.adapter = AllUsersListAdapter(
                requireContext(),
                it as ArrayList<SUser>
            )
            if (it.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }

        }
    }
}