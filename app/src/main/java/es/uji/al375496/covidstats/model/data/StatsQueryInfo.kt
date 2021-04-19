package es.uji.al375496.covidstats.model.data

import android.os.Parcel
import android.os.Parcelable
import es.uji.al375496.covidstats.model.data.database.Country

data class StatsQueryInfo(val location: Location, val country: Country, val fromDate: String, val toDate: String): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Location::class.java.classLoader)!!,
            parcel.readParcelable(Country::class.java.classLoader)!!,
            parcel.readString()!!,
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(location, flags)
        parcel.writeParcelable(country, flags)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StatsQueryInfo> {
        override fun createFromParcel(parcel: Parcel): StatsQueryInfo {
            return StatsQueryInfo(parcel)
        }

        override fun newArray(size: Int): Array<StatsQueryInfo?> {
            return arrayOfNulls(size)
        }
    }

}