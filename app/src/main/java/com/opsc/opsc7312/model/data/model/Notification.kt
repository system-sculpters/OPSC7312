package com.opsc.opsc7312.model.data.model

import android.os.Parcel
import android.os.Parcelable

data class Notification(
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var status: Boolean = false,
    var createdAt: Long = 0L
): Parcelable {
    // Constructor for creating Notification objects from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    )

    // Method to write the Notification object into a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeByte(if (status) 1 else 0)  // Converts Boolean to Byte
        parcel.writeLong(createdAt)
    }

    // Method to describe the contents (not used in this context, returns 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable.Creator implementation to create Notification objects from Parcel
    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        // Method to create an array of Notification objects of the specified size
        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }
}
