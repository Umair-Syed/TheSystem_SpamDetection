package com.skapps.android.csicodathonproject.data.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

/**
 * Created by Syed Umair on 18/12/2020.
 */
@Parcelize
data class Product(
    @Exclude
    @get:Exclude
    var id: String,
    val uid: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double,
    val ratingCount: Long
) : Parcelable