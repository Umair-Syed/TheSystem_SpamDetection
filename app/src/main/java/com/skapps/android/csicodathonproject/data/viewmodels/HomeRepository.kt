package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Syed Umair on 18/12/2020.
 */
@Singleton
class HomeRepository @Inject constructor() {

    fun fetchProducts(): MutableLiveData<List<Product>> {

        val db = FirebaseFirestore.getInstance()
        val productsCollectionRef = db.collection(KEY_COLLECTION_PRODUCTS)
        val listProduct = MutableLiveData<List<Product>>()

        productsCollectionRef.addSnapshotListener { queryDocSnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (queryDocSnapshot != null) {
                val list = ArrayList<Product>()
                for (docSnapshot in queryDocSnapshot) {
                    val product = Product(
                        docSnapshot.id,
                        docSnapshot.getString("uid") ?: "",
                        docSnapshot.getString("name") ?: "",
                        docSnapshot.getString("description") ?: "",
                        docSnapshot.getString("imageUrl") ?: "",
                        docSnapshot.getString("price") ?: "0",
                        docSnapshot.getDouble("rating") ?: 0.0,
                        docSnapshot.getLong("ratingCount") ?: 0
                    )
                    list.add(product)
                }
                listProduct.postValue(list)
            }
        }
        return listProduct
    }
}