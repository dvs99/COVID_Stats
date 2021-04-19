package es.uji.al375496.covidstats.model.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyDAO {
    @Insert
    fun insertCountries(countries: List<Country>)

    @Insert
    fun insertRegions(regions: List<Region>)

    @Insert
    fun insertSubregions(subregions: List<Subregion>)

    @Query
    ("SELECT * FROM countries ORDER BY name")
    fun getCountries(): List<Country>

    @Query
    ("SELECT * FROM regions WHERE country_id =:countryId ORDER BY name")
    fun getRegions(countryId: String): List<Region>

    @Query
    ("SELECT * FROM subregions WHERE country_id =:countryId AND region_id=:regionId ORDER BY name")
    fun getSubregions(regionId: String, countryId: String): List<Subregion>
}