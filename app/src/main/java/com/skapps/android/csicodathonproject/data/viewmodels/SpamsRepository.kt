package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.data.models.Review
import com.skapps.android.csicodathonproject.util.KEY_SUB_COLLECTION_REVIEWS
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Syed Umair on 19/12/2020.
 */

@Singleton
class SpamsRepository @Inject constructor(){

    fun fetchSpams(): MutableLiveData<List<Review>> {

        val db = FirebaseFirestore.getInstance()

        val reviewsCollectionGroup = db.collectionGroup(KEY_SUB_COLLECTION_REVIEWS)
        val listReviewsSpams = MutableLiveData<List<Review>>()

        val query = reviewsCollectionGroup.whereEqualTo("spam", true)

        query.addSnapshotListener { queryDocSnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (queryDocSnapshot != null) {
                val list = ArrayList<Review>()
                for (docSnapshot in queryDocSnapshot) {
                    val review = Review(
                        docSnapshot.id,
                        docSnapshot.getString("uid") ?: "",
                        docSnapshot.getString("name") ?: "",
                        docSnapshot.getString("pid") ?: "",
                        docSnapshot.getString("heading") ?: "",
                        docSnapshot.getString("description") ?: "",
                        docSnapshot.getDouble("rating") ?: 0.0,
                        docSnapshot.getBoolean("spam") ?: true
                    )
                    list.add(review)
                }
                listReviewsSpams.postValue(list)
            }
        }
        return listReviewsSpams
    }
}