package es.uji.al375496.covidstats.model.data.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.uji.al375496.covidstats.model.data.Location

@Entity(tableName = "countries")
data class Country (override val name: String, @PrimaryKey override val id: String): Comparable<Country>, Location
{
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!)

    override fun toString(): String
    {
        return name
    }

    override fun compareTo(other: Country): Int
    {
        return when {
            name == other.name -> 0
            name < other.name -> -1
            else -> 1
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Country> {
        override fun createFromParcel(parcel: Parcel): Country {
            return Country(parcel)
        }

        override fun newArray(size: Int): Array<Country?> {
            return arrayOfNulls(size)
        }
    }
}
