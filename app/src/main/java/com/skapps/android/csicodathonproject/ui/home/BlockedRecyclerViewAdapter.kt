package com.skapps.android.csicodathonproject.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skapps.android.csicodathonproject.data.models.SUser
import com.skapps.android.csicodathonproject.databinding.ListItemBlockedUserBinding

/**
 * Created by Syed Umair on 20/12/2020.
 */


class BlockedRecyclerViewAdapter(
    private val blockedList: ArrayList<SUser>,
    private val listener: ItemAdapterListener
) : RecyclerView.Adapter<BlockedRecyclerViewAdapter.BlockedViewHolder>() {


    interface ItemAdapterListener {
        fun onUnblockClicked(user: SUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedRecyclerViewAdapter.BlockedViewHolder {
        val binding = ListItemBlockedUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BlockedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockedViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return blockedList.size
    }

    inner class BlockedViewHolder(private val binding: ListItemBlockedUserBinding):
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.unblock.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = blockedList[position]
                    listener.onUnblockClicked(item)
                }
            }
        }


        fun bind(listIndex: Int) {
            val currentUser = blockedList[listIndex]
            binding.apply {
               name.text = currentUser.name
            }
        }
    }


}