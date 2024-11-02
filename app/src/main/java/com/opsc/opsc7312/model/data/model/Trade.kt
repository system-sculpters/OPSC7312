package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

data class Trade(
    var userid: String = "",            // User ID associated with the goal
    var symbol: String = "",
    var quantity: Int = 0,
) : Parcelable {
    // Constructor for creating Trade objects from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    // Method to write the Trade object into a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(symbol)
        parcel.writeInt(quantity)
    }

    // Method to describe the contents (not used in this context, returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator implementation to create Trade objects from Parcel
    companion object CREATOR : Parcelable.Creator<Trade> {
        override fun createFromParcel(parcel: Parcel): Trade {
            return Trade(parcel)
        }

        // Method to create an array of Trade objects of the specified size
        override fun newArray(size: Int): Array<Trade?> {
            return arrayOfNulls(size)
        }
    }
}
