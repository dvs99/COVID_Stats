package es.uji.al375496.covidstats.model.data.database

import android.content.Context
import androidx.room.Room
import com.android.volley.Response
import es.uji.al375496.covidstats.model.SingletonHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseAccess private constructor(context: Context)
{
    companion object: SingletonHolder<DatabaseAccess, Context>(::DatabaseAccess)

    private val dao: MyDAO

    init
    {
        val db = Room.databaseBuilder(context, AbstractDatabase::class.java,"database").build()
        dao = db.getDAO()
    }

    fun getCountries(listener: Response.Listener<List<Country>>)
    {
        GlobalScope.launch(Dispatchers.Main) {
            val countries = withContext(Dispatchers.IO) {
                dao.getCountries()
            }
            listener.onResponse(countries)
        }
    }

    fun getRegions(listener: Response.Listener<List<Region>>, country: Country)
    {
        GlobalScope.launch(Dispatchers.Main) {
            val regions = withContext(Dispatchers.IO) {
                dao.getRegions(country.id)
            }
            listener.onResponse(regions)
        }
    }

    fun getSubregions(listener: Response.Listener<List<Subregion>>, country: Country, region: Region)
    {
        GlobalScope.launch(Dispatchers.Main) {
            val subregions = withContext(Dispatchers.IO) {
                dao.getSubregions(region.id, country.id)
            }
            listener.onResponse(subregions)
        }
    }

    fun insertCountries(countries: List<Country>)
    {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertCountries(countries)
            }
        }
    }

    fun insertRegions(regions: List<Region>)
    {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertRegions(regions)
            }
        }
    }

    fun insertSubregions(subregions: List<Subregion>)
    {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertSubregions(subregions)
            }
        }
    }
}
