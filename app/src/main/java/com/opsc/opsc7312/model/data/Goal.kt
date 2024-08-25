package com.opsc.opsc7312.model.data

import android.os.Parcel
import android.os.Parcelable

data class Goal(
    var id: String = "",
    var name: String = "",
    var totalamount: Double = 0.00,
    var currentamount: Double = 0.00,
    var date: Long = 0L,
    var contrubitiontype: String = "",
    var contributionamount: Double = 0.00
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.writeString(name)
        parcel.writeDouble(totalamount)
        parcel.writeDouble(currentamount)
        parcel.writeLong(date)
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
