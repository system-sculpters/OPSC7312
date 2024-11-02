package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

data class Investment(
    val id: String = "",
    val purchasePrice: Double = 0.0,
    val userid: String = "",
    val totalInvested: Double = 0.0,
    val purchaseDate: Long = 0L,
    val symbol: String = "",
    val currentValue: Double = 0.0,
    val quantity: Int = 0,
    val stockData: Stock? = Stock()
) : Parcelable {
    // Constructor for creating Investment objects from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readParcelable(Stock::class.java.classLoader)
    )

    // Method to write the Investment object into a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeDouble(purchasePrice)
        parcel.writeString(userid)
        parcel.writeDouble(totalInvested)
        parcel.writeLong(purchaseDate)
        parcel.writeString(symbol)
        parcel.writeDouble(currentValue)
        parcel.writeInt(quantity)
        parcel.writeParcelable(stockData, flags)
    }

    // Method to describe the contents (not used in this context, returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator implementation to create Investment objects from Parcel
    companion object CREATOR : Parcelable.Creator<Investment> {
        override fun createFromParcel(parcel: Parcel): Investment {
            return Investment(parcel)
        }

        // Method to create an array of Investment objects of the specified size
        override fun newArray(size: Int): Array<Investment?> {
            return arrayOfNulls(size)
        }
    }
}
