package com.skapps.android.csicodathonproject.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Syed Umair on 18/12/2020.
 */
@Parcelize
data class Review(
    val uid: String,
    val pid: String,
    val heading: String,
    val description: String,
    val rating: Double
) : Parcelable