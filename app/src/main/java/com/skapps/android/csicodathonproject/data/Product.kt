package com.skapps.android.csicodathonproject.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Syed Umair on 18/12/2020.
 */
@Parcelize
data class Product(
    val uid: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double
) : Parcelable