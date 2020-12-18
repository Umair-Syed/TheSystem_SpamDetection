package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.data.models.Review

/**
 * Created by Syed Umair on 18/12/2020.
 */
class ReviewsViewModel @ViewModelInject constructor(
    private val repository: ReviewsRepository
) : ViewModel() {

    private var reviewsList = MutableLiveData<List<Review>>()

    fun getReviews(product: Product) : LiveData<List<Review>> {
        reviewsList =  repository.fetchReviews(product)
        return reviewsList
    }
}