package com.skapps.android.csicodathonproject.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.SUser
import com.skapps.android.csicodathonproject.databinding.ListItemAllUsersBinding

/**
 * Created by Syed Umair on 20/12/2020.
 */

class AllUsersListAdapter(
    private val context: Context,
    private val usersList: ArrayList<SUser>
) : RecyclerView.Adapter<AllUsersListAdapter.AllUsersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUsersListAdapter.AllUsersViewHolder {
        val binding = ListItemAllUsersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllUsersViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class AllUsersViewHolder(private val binding: ListItemAllUsersBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listIndex: Int) {
            val currentUser = usersList[listIndex]
            binding.apply {
                name.text = currentUser.name
                if(currentUser.active){
                    active.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.indicator_online) )
                }else{
                    active.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.indicator_offline) )
                }
            }
        }
    }


}