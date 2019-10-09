package com.emedia.googleapis.google_place

import android.os.Parcel
import android.os.Parcelable


data class GoogleLocation(
    var address: String? = null,
    var latitude: Double? = null,
    var longtude: Double? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeValue(latitude)
        parcel.writeValue(longtude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoogleLocation> {
        override fun createFromParcel(parcel: Parcel): GoogleLocation {
            return GoogleLocation(parcel)
        }

        override fun newArray(size: Int): Array<GoogleLocation?> {
            return arrayOfNulls(size)
        }
    }


}