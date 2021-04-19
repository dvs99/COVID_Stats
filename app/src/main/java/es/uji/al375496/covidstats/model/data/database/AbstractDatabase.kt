package es.uji.al375496.covidstats.model.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = [Country::class, Region::class, Subregion::class], version = 1, exportSchema = false)
abstract class AbstractDatabase: RoomDatabase()
{
    abstract fun getDAO(): MyDAO
}