package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

// Data class representing a Transaction entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class Transaction(
    var id: String = "",
    var name: String = "",
    var amount: Double = 0.00,
    var date: Long = 0L,
    var userid: String = "",
    var isrecurring: Boolean = false,
    var type: String = "", // Use String to match the provided data
    var categoryId: String = "",
    val category: Category = Category()
) : Parcelable {
    // Implementing Parcelable for passing Transaction objects between components

    // Constructor for creating Category objects from Parcel (used during parceling)

    // This implementation of a parcelable data class was adapted from stackoverflow
    // https://stackoverflow.com/questions/49249234/what-is-parcelable-in-android
    // Rehan Khan
    // https://stackoverflow.com/users/16812867/rehan-khan
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble() , // Deserialize BigDecimal from String
        parcel.readLong(), // Deserialize the timestamp

        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(), // Deserialize Boolean
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Category::class.java.classLoader) ?: Category() // Deserialize Category object
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name) // Corrected: name should be written here
        parcel.writeString(amount.toString()) // Serialize BigDecimal to String
        parcel.writeLong(date) // Serialize timestamp
        parcel.writeString(userid)
        parcel.writeByte(if (isrecurring) 1 else 0) // Serialize Boolean
        parcel.writeString(type)
        parcel.writeString(categoryId) // Corrected: categoryId should be written here
        parcel.writeParcelable(category, flags) // Serialize Category object
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}