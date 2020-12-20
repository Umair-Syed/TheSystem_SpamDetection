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
class BlockedUsersRepository @Inject constructor() {

    fun fetchBlockedUsers(): MutableLiveData<List<SUser>> {

        val db = FirebaseFirestore.getInstance()
        val usersCollectionRef = db.collection(KEY_COLLECTION_USERS)
        val blockedUsersList = MutableLiveData<List<SUser>>()

        val query = usersCollectionRef.whereEqualTo("blocked", true)

        query.addSnapshotListener { queryDocSnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (queryDocSnapshot != null) {
                val list = ArrayList<SUser>()
                for (docSnapshot in queryDocSnapshot) {
                    val blockedUser = SUser(
                        docSnapshot.getString("name") ?: "",
                        docSnapshot.getString("email") ?: "",
                        docSnapshot.getBoolean("admin") ?: false,
                        docSnapshot.getBoolean("blocked") ?: true
                    )
                    list.add(blockedUser)
                }
                blockedUsersList.postValue(list)
            }
        }
        return blockedUsersList
    }
}