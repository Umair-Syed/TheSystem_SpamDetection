package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skapps.android.csicodathonproject.data.models.SUser

/**
 * Created by Syed Umair on 20/12/2020.
 */

class AllUsersViewModel@ViewModelInject constructor(
    private val repository: AllUsersRepository
) : ViewModel() {

    private var usersList = MutableLiveData<List<SUser>>()

    fun getUsersList() : LiveData<List<SUser>> {
        usersList =  repository.fetchAllUsers()
        return usersList
    }
}