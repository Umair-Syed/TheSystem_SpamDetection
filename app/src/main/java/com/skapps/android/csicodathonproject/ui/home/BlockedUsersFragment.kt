package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.SUser
import com.skapps.android.csicodathonproject.data.viewmodels.BlockedUsersViewModel
import com.skapps.android.csicodathonproject.databinding.FragmentBlockedUsersBinding
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BlockedUsersFragment"

@AndroidEntryPoint
class BlockedUsersFragment : Fragment(R.layout.fragment_blocked_users), BlockedRecyclerViewAdapter.ItemAdapterListener {
    private lateinit var binding: FragmentBlockedUsersBinding

    private val viewModel by viewModels<BlockedUsersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBlockedUsersBinding.bind(view)

        binding.blockedUsersRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        viewModel.getBlockList().observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: blockList size == ${it.size}")
            binding.blockedUsersRecyclerView.adapter = BlockedRecyclerViewAdapter(
                it as ArrayList<SUser>,
                this
            )
            if (it.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }

        }
    }

    override fun onUnblockClicked(user: SUser) {
        val db = FirebaseFirestore.getInstance()
        val userCollectionRef = db.collection(KEY_COLLECTION_USERS)

        userCollectionRef.whereEqualTo("email",user.email).get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result != null){
                    for(doc in it.result!!){
                        userCollectionRef.document(doc.id).update("blocked", false)
                    }
                }
            }


    }
}

