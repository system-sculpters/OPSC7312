package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

data class Stock(
    val symbol: String = "",
    val name: String = "",
    val logoUrl: String = "",
    val currentPrice: Double = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val openPrice: Double = 0.0,
    val previousClosePrice: Double = 0.0
): Parcelable {
    // Implementing Parcelable for passing Stock objects between components

    // Constructor for creating Stock objects from Parcel (used during parceling)

    // This implementation of a parcelable data class was adapted from StackOverflow
    // https://stackoverflow.com/questions/49249234/what-is-parcelable-in-android
    // Rehan Khan
    // https://stackoverflow.com/users/16812867/rehan-khan
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    // Method to write the Stock object into a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(symbol)
        parcel.writeString(name)
        parcel.writeString(logoUrl)
        parcel.writeDouble(currentPrice)
        parcel.writeDouble(highPrice)
        parcel.writeDouble(lowPrice)
        parcel.writeDouble(openPrice)
        parcel.writeDouble(previousClosePrice)
    }

    // Method to describe the contents (not used in this context, returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator implementation to create Stock objects from Parcel
    companion object CREATOR : Parcelable.Creator<Stock> {
        override fun createFromParcel(parcel: Parcel): Stock {
            return Stock(parcel)
        }

        // Method to create an array of Stock objects of the specified size
        override fun newArray(size: Int): Array<Stock?> {
            return arrayOfNulls(size)
        }
    }
}
