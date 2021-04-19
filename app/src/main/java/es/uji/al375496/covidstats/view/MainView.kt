package es.uji.al375496.covidstats.view

import android.view.View
import es.uji.al375496.covidstats.model.data.database.Country
import es.uji.al375496.covidstats.model.data.Location
import es.uji.al375496.covidstats.model.data.database.Region
import es.uji.al375496.covidstats.model.data.database.Subregion

interface MainView
{
    var enabledCountryButton: Boolean
    var enabledRegionButton: Boolean
    var enabledSubregionButton: Boolean
    var isLoading: Boolean

    fun onGoButton(view: View)

    fun showError(message: String)
    fun showCountries(countries: List<Country>)
    fun showRegions(regions: List<Region>)
    fun hideRegions()
    fun showSubregions(subregions: List<Subregion>)
    fun hideSubregions()
    fun launchStatsActivity(location: Location, country: Country, fromDate: String, toDate: String)
    fun showRetry()
    fun clearRegion()
    fun clearCountry()
}