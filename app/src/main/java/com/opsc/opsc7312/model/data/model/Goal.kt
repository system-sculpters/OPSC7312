package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

// Data class representing a Goal entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class Goal(
    var id: String = "",
    var userid: String = "",
    var name: String = "",
    var targetamount: Double = 0.00,
    var currentamount: Double = 0.00,
    var deadline: Long = 0L,
    var contrubitiontype: String = "",
    var contributionamount: Double = 0.00
) : Parcelable {
    // Implementing Parcelable for passing Category objects between components

    // Constructor for creating Category objects from Parcel (used during parceling)

    // This implementation of a parcelable data class was adapted from stackoverflow
    // https://stackoverflow.com/questions/49249234/what-is-parcelable-in-android
    // Rehan Khan
    // https://stackoverflow.com/users/16812867/rehan-khan
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userid)
        parcel.writeString(name)
        parcel.writeDouble(targetamount)
        parcel.writeDouble(currentamount)
        parcel.writeLong(deadline)
        parcel.writeString(contrubitiontype)
        parcel.writeDouble(contributionamount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Goal> {
        override fun createFromParcel(parcel: Parcel): Goal {
            return Goal(parcel)
        }

        override fun newArray(size: Int): Array<Goal?> {
            return arrayOfNulls(size)
        }
    }
}
