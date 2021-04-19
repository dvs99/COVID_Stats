package es.uji.al375496.covidstats.model

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import es.uji.al375496.covidstats.IMPOSSIBLE_VALUE
import es.uji.al375496.covidstats.model.data.*
import es.uji.al375496.covidstats.model.data.database.Country
import es.uji.al375496.covidstats.model.data.database.Region
import es.uji.al375496.covidstats.model.data.database.Subregion
import org.json.JSONException
import org.json.JSONObject

class NetworkAccess private constructor(context: Context)
{
    companion object: SingletonHolder<NetworkAccess,Context>(::NetworkAccess)

    //network constants
    object NetConstants {
        const val BASE_URL = "https://api.covid19tracking.narrativa.com/api"
        const val COUNTRIES = "countries"
        const val COUNTRY = "country"
        const val REGIONS = "regions"
        const val REGION = "region"
        const val SUBREGIONS = "sub_regions"
        const val SUBREGION = "sub_region"
        const val NAME = "name"
        const val ID = "id"
        const val DATE_FROM = "date_from"
        const val DATE_TO = "date_to"
        const val DATES = "dates"
        const val DATE = "date"
        const val CONFIRMED = "today_confirmed"
        const val CONFIRMED_NEW = "today_new_confirmed"
        const val DEATHS = "today_deaths"
        const val DEATHS_NEW = "today_new_deaths"
        const val RECOVERED = "today_recovered"
        const val RECOVERED_NEW = "today_new_recovered"
        const val STATS_TIMEOUT = 7000
    }

    private val queue: RequestQueue = Volley.newRequestQueue(context)

    fun getCountries(listener: Response.Listener<List<Country>>, errorListener: Response.ErrorListener)
    {
        val url = "${NetConstants.BASE_URL}/${NetConstants.COUNTRIES}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response -> listener.onResponse(parseCountriesJson(response))}, //send the parsed list if there is no error
            {error ->  errorListener.onErrorResponse(error) })
        queue.add(request)
    }

    //turns the JSON into a list of countries
    private fun parseCountriesJson(response: JSONObject): List<Country>?
    {
        val countries = ArrayList<Country>()
        try
        {
            val countryJsonArray = response.getJSONArray(NetConstants.COUNTRIES)
            for (i in 0 until countryJsonArray.length()){
                val countryJsonObject : JSONObject = countryJsonArray[i] as JSONObject
                val country = Country(countryJsonObject.getString(NetConstants.NAME), countryJsonObject.getString(NetConstants.ID))
                countries.add(country)
            }
        }
        catch (e: JSONException)
        {
            return null
        }
        countries.sort()
        return countries
    }

    fun getRegions(country: Country, listener: Response.Listener<List<Region>>, errorListener: Response.ErrorListener)
    {
        val url = "${NetConstants.BASE_URL}/${NetConstants.COUNTRIES}/${country.id}/${NetConstants.REGIONS}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response -> listener.onResponse(parseRegionsJson(country, response))}, //send the parsed list if there is no error
            {error ->  errorListener.onErrorResponse(error) })
        queue.add(request)
    }

    private fun parseRegionsJson(country: Country, response: JSONObject): List<Region>?
    {
        val regions = ArrayList<Region>()
        try
        {
            val regionJsonArray = response.getJSONArray(NetConstants.COUNTRIES).getJSONObject(0).getJSONArray(country.id)
            for (i in 0 until regionJsonArray.length()){
                val regionJsonObject : JSONObject = regionJsonArray[i] as JSONObject
                val region = Region(regionJsonObject.getString(NetConstants.NAME), regionJsonObject.getString(NetConstants.ID), country.id)
                regions.add(region)
            }
        }
        catch (e: JSONException)
        {
            return null
        }
        regions.sort()
        return regions
    }

    fun getSubregions(country: Country, region: Region, listener: Response.Listener<List<Subregion>>, errorListener: Response.ErrorListener)
    {
        val url = "${NetConstants.BASE_URL}/${NetConstants.COUNTRIES}/${country.id}/${NetConstants.REGIONS}/${region.id}/${NetConstants.SUBREGIONS}"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                {response -> listener.onResponse(parseSubregionsJson(country, region, response))}, //send the parsed list if there is no error
                {error ->  errorListener.onErrorResponse(error) })
        queue.add(request)
    }

    private fun parseSubregionsJson(country: Country, region: Region, response: JSONObject): List<Subregion>?
    {
        val subregions = ArrayList<Subregion>()
        try
        {
            val regionJsonArray = response.getJSONArray(NetConstants.COUNTRIES).getJSONObject(0).getJSONObject(country.id).getJSONArray(region.id)
            for (i in 0 until regionJsonArray.length()){
                val regionJsonObject : JSONObject = regionJsonArray[i] as JSONObject
                val subregion = Subregion(regionJsonObject.getString(NetConstants.NAME), regionJsonObject.getString(NetConstants.ID), region.id, country.id)
                subregions.add(subregion)
            }
        }
        catch (e: JSONException)
        {
            return null
        }
        subregions.sort()
        return subregions
    }

    fun getStats(info: StatsQueryInfo, listener: Response.Listener<List<DayData>>, errorListener: Response.ErrorListener)
    {
        var url = ""
        when (info.location) {
            is Country ->   url = "${NetConstants.BASE_URL}/${NetConstants.COUNTRY}/${info.location.id}?${NetConstants.DATE_FROM}=${info.fromDate}&${NetConstants.DATE_TO}=${info.toDate}"
            is Region ->    url = "${NetConstants.BASE_URL}/${NetConstants.COUNTRY}/${info.country.id}/${NetConstants.REGION}/${info.location.id}?${NetConstants.DATE_FROM}=${info.fromDate}&${NetConstants.DATE_TO}=${info.toDate}"
            is Subregion -> url = "${NetConstants.BASE_URL}/${NetConstants.COUNTRY}/${info.country.id}/${NetConstants.REGION}/${info.location.region_id}/${NetConstants.SUBREGION}/${info.location.id}?${NetConstants.DATE_FROM}=${info.fromDate}&${NetConstants.DATE_TO}=${info.toDate}"
        }
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                { //send the parsed list if there is no error
                    response ->
                    when (info.location) {
                        is Country -> listener.onResponse(parseCountryStats(info.location, response))
                        is Region -> listener.onResponse(parseRegionStats(info.country, response))
                        is Subregion -> listener.onResponse(parseSubregionStats(info.country, response))
                    }
                },
                {error ->  errorListener.onErrorResponse(error) })
        request.retryPolicy = DefaultRetryPolicy(
                NetConstants.STATS_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(request)
    }

    private fun parseCountryStats(country: Country, response: JSONObject): List<DayData>?
    {
        val stats = ArrayList<DayData>()
        try
        {
            val datesJsonObj = response.getJSONObject(NetConstants.DATES)
            for (date in datesJsonObj.keys()){
                val dataObject = datesJsonObj.getJSONObject(date).getJSONObject(NetConstants.COUNTRIES).getJSONObject(country.name)
                if (dataObject.getString(NetConstants.DATE) == date)
                {
                val dayData = DayData(date, dataObject.optInt(NetConstants.CONFIRMED, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.CONFIRMED_NEW, IMPOSSIBLE_VALUE),
                        dataObject.optInt(NetConstants.DEATHS, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.DEATHS_NEW, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.RECOVERED, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants. RECOVERED_NEW, IMPOSSIBLE_VALUE))
                stats.add(dayData)
                }
            }
        }
        catch (e: JSONException)
        {
            return null
        }
        return stats
    }

    private fun parseRegionStats(country: Country, response: JSONObject): List<DayData>?
    {
        val stats = ArrayList<DayData>()
        try
        {
            val datesJsonObj = response.getJSONObject(NetConstants.DATES)
            for (date in datesJsonObj.keys()){
                val dataObject = datesJsonObj.getJSONObject(date).getJSONObject(NetConstants.COUNTRIES).getJSONObject(country.name).getJSONArray(NetConstants.REGIONS).getJSONObject(0)
                if (dataObject.getString(NetConstants.DATE) == date)
                {
                    val dayData = DayData(date, dataObject.optInt(NetConstants.CONFIRMED, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.CONFIRMED_NEW, IMPOSSIBLE_VALUE),
                            dataObject.optInt(NetConstants.DEATHS, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.DEATHS_NEW, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.RECOVERED, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants. RECOVERED_NEW, IMPOSSIBLE_VALUE))
                    stats.add(dayData)
                }
            }
        }
        catch (e: JSONException)
        {
            return null
        }
        return stats
    }

    private fun parseSubregionStats(country: Country, response: JSONObject): List<DayData>?
    {
        val stats = ArrayList<DayData>()
        try
        {
            val datesJsonObj = response.getJSONObject(NetConstants.DATES)
            for (date in datesJsonObj.keys()){
                val dataObject = datesJsonObj.getJSONObject(date).getJSONObject(NetConstants.COUNTRIES).getJSONObject(country.name).getJSONArray(NetConstants.REGIONS).getJSONObject(0).getJSONArray(NetConstants.SUBREGIONS).getJSONObject(0)
                if (dataObject.getString(NetConstants.DATE) == date)
                {
                    val dayData = DayData(date, dataObject.optInt(NetConstants.CONFIRMED, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.CONFIRMED_NEW, IMPOSSIBLE_VALUE),
                            dataObject.optInt(NetConstants.DEATHS, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.DEATHS_NEW, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants.RECOVERED, IMPOSSIBLE_VALUE), dataObject.optInt(NetConstants. RECOVERED_NEW, IMPOSSIBLE_VALUE))
                    stats.add(dayData)
                }
            }
        }
        catch (e: JSONException)
        {
            return null
        }
        return stats
    }
}
