package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skapps.android.csicodathonproject.data.models.SUser

/**
 * Created by Syed Umair on 20/12/2020.
 */

class BlockedUsersViewModel@ViewModelInject constructor(
    private val repository: BlockedUsersRepository
) : ViewModel() {

    private var blockedUsersList = MutableLiveData<List<SUser>>()

    fun getBlockList() : LiveData<List<SUser>> {
        blockedUsersList =  repository.fetchBlockedUsers()
        return blockedUsersList
    }
}