package com.skapps.android.csicodathonproject.data.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.data.models.SUser
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Syed Umair on 20/12/2020.
 */

@Singleton
class AllUsersRepository @Inject constructor() {

    fun fetchAllUsers(): MutableLiveData<List<SUser>> {

        val db = FirebaseFirestore.getInstance()
        val usersCollectionRef = db.collection(KEY_COLLECTION_USERS)
        val usersList = MutableLiveData<List<SUser>>()

        usersCollectionRef.addSnapshotListener { queryDocSnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (queryDocSnapshot != null) {
                val list = ArrayList<SUser>()
                for (docSnapshot in queryDocSnapshot) {
                    val user = SUser(
                        docSnapshot.getString("name") ?: "",
                        docSnapshot.getString("email") ?: "",
                        docSnapshot.getBoolean("admin") ?: false,
                        docSnapshot.getBoolean("blocked") ?: true,
                        docSnapshot.getBoolean("active") ?: true

                    )
                    list.add(user)
                }
                usersList.postValue(list)
            }
        }
        return usersList
    }
}