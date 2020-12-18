package com.skapps.android.csicodathonproject.data.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

/**
 * Created by Syed Umair on 18/12/2020.
 */
@Parcelize
data class Review(
    @Exclude
    @get:Exclude
    val rid: String,
    val uid: String,
    val name: String,
    val pid: String,
    val heading: String,
    val description: String,
    val rating: Double,
    val spam: Boolean
) : Parcelable