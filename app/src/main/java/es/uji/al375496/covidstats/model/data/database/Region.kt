package es.uji.al375496.covidstats.model.data.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.uji.al375496.covidstats.model.data.Location

@Entity(tableName = "regions",
        indices = [Index(value = ["country_id"])],
        foreignKeys = [ForeignKey(entity = Country::class, parentColumns = ["id"], childColumns = ["country_id"])],
        primaryKeys = ["id", "country_id"])
data class Region (override val name: String, override val id: String, val country_id: String): Comparable<Region>, Location
{
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)

    override fun toString(): String
    {
        return name
    }

    override fun compareTo(other: Region): Int
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
        parcel.writeString(country_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Region> {
        override fun createFromParcel(parcel: Parcel): Region {
            return Region(parcel)
        }

        override fun newArray(size: Int): Array<Region?> {
            return arrayOfNulls(size)
        }
    }
}