package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skapps.android.csicodathonproject.data.Product

/**
 * Created by Syed Umair on 18/12/2020.
 */

class HomeViewModel@ViewModelInject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private var productsList = MutableLiveData<List<Product>>()

    fun getProducts() :LiveData<List<Product>>{
        productsList =  repository.fetchProducts()
        return productsList
    }
}