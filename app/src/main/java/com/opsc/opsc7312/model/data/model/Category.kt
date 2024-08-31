package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable
import com.opsc.opsc7312.AppConstants

// Data class representing a Category entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class Category(
    var id: String = "",
    var name: String = "",
    var color: String = "",
    var icon: String = "",
    var transactiontype: String = AppConstants.TRANSACTIONTYPE.INCOME.name,
    var userid: String = "",
    val isCreateButton: Boolean = false
): Parcelable {
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
        parcel.readString() ?: "",
        parcel.readString() ?:AppConstants.TRANSACTIONTYPE.INCOME.name,
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte() // Reads the boolean value
    )

    // Method to write the Category object into a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(color)
        parcel.writeString(icon)
        parcel.writeString(transactiontype)
        parcel.writeString(userid)
        parcel.writeByte(if (isCreateButton) 1 else 0) // Writes the boolean value
    }

    // Method to describe the contents (not used in this context, returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator implementation to create Category objects from Parcel
    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        // Method to create an array of Category objects of the specified size
        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}