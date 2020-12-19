package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skapps.android.csicodathonproject.data.models.Review

/**
 * Created by Syed Umair on 19/12/2020.
 */

class SpamsViewModel @ViewModelInject constructor(
    private val repository: SpamsRepository
) : ViewModel() {

    private var reviewsList = MutableLiveData<List<Review>>()

    fun getSpams() : LiveData<List<Review>> {
        reviewsList =  repository.fetchSpams()
        return reviewsList
    }
}