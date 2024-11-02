package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

data class StockHistory(
    val date: String = "",         // The date of the stock data point
    val high: Float = 0F,         // The highest price during the trading period
    val low: Float = 0F,          // The lowest price during the trading period
    val open: Float = 0F,         // The opening price at the beginning of the trading period
    val close: Float = 0F,        // The closing price at the end of the trading period
    val adjClose: Float = 0F,     // The adjusted closing price (accounting for splits, dividends, etc.)
    val volume: Int = 0
): Parcelable {
    // Constructor for creating StockHistory objects from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt()
    )

    // Method to write the StockHistory object into a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeFloat(high)
        parcel.writeFloat(low)
        parcel.writeFloat(open)
        parcel.writeFloat(close)
        parcel.writeFloat(adjClose)
        parcel.writeInt(volume)
    }

    // Method to describe the contents (not used in this context, returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator implementation to create StockHistory objects from Parcel
    companion object CREATOR : Parcelable.Creator<StockHistory> {
        override fun createFromParcel(parcel: Parcel): StockHistory {
            return StockHistory(parcel)
        }

        // Method to create an array of StockHistory objects of the specified size
        override fun newArray(size: Int): Array<StockHistory?> {
            return arrayOfNulls(size)
        }
    }
}
