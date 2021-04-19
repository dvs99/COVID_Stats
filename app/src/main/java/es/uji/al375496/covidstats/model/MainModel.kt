package es.uji.al375496.covidstats.model

import android.content.Context
import com.android.volley.Response
import es.uji.al375496.covidstats.IMPOSSIBLE_STRING
import es.uji.al375496.covidstats.model.data.database.Country
import es.uji.al375496.covidstats.model.data.database.DatabaseAccess
import es.uji.al375496.covidstats.model.data.database.Region
import es.uji.al375496.covidstats.model.data.database.Subregion

class MainModel (context: Context)
{
    var currentSubregion: Subregion? = null
    var currentRegion: Region? = null
    var currentCountry: Country? = null
    var fromDate = ""
    var toDate = ""

    private var networkAccess = NetworkAccess.getInstance(context)
    private var databaseAccess = DatabaseAccess.getInstance(context)

    fun getCountries (listener: Response.Listener<List<Country>>, errorListener: Response.ErrorListener)
    {
        databaseAccess.getCountries(Response.Listener { countries->
            if (countries.isEmpty()){
                networkAccess.getCountries(Response.Listener {netCountries->
                    if (netCountries != null)
                        databaseAccess.insertCountries(netCountries)
                    listener.onResponse(netCountries)
                }, errorListener)
            }
            else
                listener.onResponse(countries)
        })
    }

    fun getRegions(listener: Response.Listener<List<Region>>, errorListener: Response.ErrorListener)
    {
        if (currentCountry != null)
        {
            databaseAccess.getRegions(Response.Listener { regions->
                if (regions.isEmpty()){
                    networkAccess.getRegions(currentCountry!!, Response.Listener {netRegions->
                        if (netRegions != null)
                        {
                            if (netRegions.isEmpty())
                                databaseAccess.insertRegions(listOf(Region(IMPOSSIBLE_STRING,IMPOSSIBLE_STRING,currentCountry!!.id)))
                            else
                                databaseAccess.insertRegions(netRegions)
                        }
                        listener.onResponse(netRegions)

                    }, errorListener)
                }
                else if (regions.size == 1 && regions[0].id == IMPOSSIBLE_STRING && regions[0].name == IMPOSSIBLE_STRING)
                    listener.onResponse(listOf())
                else
                    listener.onResponse(regions)
            }, currentCountry!!)
        }
    }

    fun getSubregions(listener: Response.Listener<List<Subregion>>, errorListener: Response.ErrorListener)
    {
        if (currentCountry != null && currentRegion != null)
        {
            databaseAccess.getSubregions(Response.Listener { subregions->
                if (subregions.isEmpty()){
                    networkAccess.getSubregions(currentCountry!!, currentRegion!!, Response.Listener {netSubregions->
                        if (netSubregions != null)
                        {
                            if (netSubregions.isEmpty())
                                databaseAccess.insertSubregions(listOf(Subregion(IMPOSSIBLE_STRING,IMPOSSIBLE_STRING,currentRegion!!.id,currentCountry!!.id)))
                            else
                                databaseAccess.insertSubregions(netSubregions)
                        }
                        listener.onResponse(netSubregions)
                    }, errorListener)
                }
                else if (subregions.size == 1 && subregions[0].id == IMPOSSIBLE_STRING && subregions[0].name == IMPOSSIBLE_STRING)
                    listener.onResponse(listOf())
                else
                    listener.onResponse(subregions)
            }, currentCountry!!, currentRegion!!)
        }
    }
}