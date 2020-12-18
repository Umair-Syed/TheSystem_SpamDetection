package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.data.models.Review
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS
import com.skapps.android.csicodathonproject.util.KEY_SUB_COLLECTION_REVIEWS
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Syed Umair on 18/12/2020.
 */
@Singleton
class ReviewsRepository @Inject constructor(){

    fun fetchReviews(product: Product): MutableLiveData<List<Review>> {

        val db = FirebaseFirestore.getInstance()
        val reviewsCollection = db.collection(KEY_COLLECTION_PRODUCTS).document(product.id)
            .collection(KEY_SUB_COLLECTION_REVIEWS)
        val listReviews = MutableLiveData<List<Review>>()

        reviewsCollection.addSnapshotListener { queryDocSnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (queryDocSnapshot != null) {
                val list = ArrayList<Review>()
                for (docSnapshot in queryDocSnapshot) {
                    val review = Review(
                        docSnapshot.getString("uid") ?: "",
                        docSnapshot.getString("name") ?: "",
                        docSnapshot.getString("pid") ?: "",
                        docSnapshot.getString("heading") ?: "",
                        docSnapshot.getString("description") ?: "",
                        docSnapshot.getDouble("rating") ?: 0.0
                    )
                    list.add(review)
                }
                listReviews.postValue(list)
            }
        }
        return listReviews
    }
}
