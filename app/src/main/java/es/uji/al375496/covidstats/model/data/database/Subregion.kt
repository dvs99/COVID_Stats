package es.uji.al375496.covidstats.model.data.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.uji.al375496.covidstats.model.data.Location

@Entity(tableName = "subregions",
        indices = [Index(value = ["region_id","country_id"])],
        foreignKeys = [ForeignKey(entity = Region::class, parentColumns = ["id", "country_id"], childColumns = ["region_id","country_id"])],
        primaryKeys = ["id", "region_id", "country_id"])
data class Subregion (override val name: String, override val id: String, val region_id: String, val country_id: String): Comparable<Subregion>, Location
{
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)

    override fun toString(): String
    {
        return name
    }

    override fun compareTo(other: Subregion): Int
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
        parcel.writeString(region_id)
        parcel.writeString(country_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subregion> {
        override fun createFromParcel(parcel: Parcel): Subregion {
            return Subregion(parcel)
        }

        override fun newArray(size: Int): Array<Subregion?> {
            return arrayOfNulls(size)
        }
    }
}
