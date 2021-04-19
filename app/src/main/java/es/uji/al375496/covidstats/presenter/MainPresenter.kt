package es.uji.al375496.covidstats.presenter

import com.android.volley.Response
import es.uji.al375496.covidstats.model.MainModel
import es.uji.al375496.covidstats.model.data.database.Country
import es.uji.al375496.covidstats.model.data.database.Region
import es.uji.al375496.covidstats.model.data.database.Subregion
import es.uji.al375496.covidstats.view.MainView


class MainPresenter (private val view: MainView, private val model: MainModel)
{
    init
    {
        //fetch the countries from the internet
        view.isLoading = true
        model.getCountries(Response.Listener { countries ->
            if (countries == null) {
                view.showError("Possible bad JSON received from the server")
                view.isLoading = false
            } else {
                view.showCountries(countries)
                view.isLoading = false}
        }, Response.ErrorListener { error ->
            view.showError("Network error: ${ErrorParser.parseNetworkError(error)}")
            view.isLoading = false
            view.showRetry()
        })
    }

    fun onCountrySelected(country: Country)
    {
        model.currentCountry = country
        //fetch the regions from the internet
        view.isLoading = true
        model.getRegions(Response.Listener { regions ->
            if (regions == null) {
                view.showError("Possible bad JSON received from the server")
                view.isLoading = false
                view.clearCountry()
                onCountryDeselected()
            } else {
                view.showRegions(regions)
                view.isLoading = false
                onRegionDeselected()}
        }, Response.ErrorListener { error ->
            view.showError("Network error: ${ErrorParser.parseNetworkError(error)}")
            view.isLoading = false
            view.clearCountry()
            onCountryDeselected()
        })
        checkEnableGoButtons()
    }

    fun onCountryDeselected()
    {
        model.currentCountry = null
        view.hideRegions()
        view.hideSubregions()
        checkEnableGoButtons()
    }

    fun onRegionSelected(region: Region)
    {
        model.currentRegion = region
        //fetch the subregions from the internet
        view.isLoading = true
        model.getSubregions(Response.Listener { subregions ->
            if (subregions == null) {
                view.showError("Possible bad JSON received from the server")
                view.isLoading = false
                view.clearRegion()
                onRegionDeselected()
            } else {
                view.showSubregions(subregions)
                view.isLoading = false
                onSubregionDeselected()}
        }, Response.ErrorListener { error ->
            view.showError("Network error: ${ErrorParser.parseNetworkError(error)}")
            view.isLoading = false
            view.clearRegion()
            onRegionDeselected()
        })
        checkEnableGoButtons()
    }

    fun onRegionDeselected()
    {
        model.currentRegion = null
        view.hideSubregions()
        checkEnableGoButtons()
    }

    fun onSubregionSelected(subregion: Subregion)
    {
        model.currentSubregion = subregion
        checkEnableGoButtons()
    }

    fun onSubregionDeselected()
    {
        model.currentSubregion = null
        checkEnableGoButtons()
    }

    fun onFromDateSet(date: String)
    {
        model.fromDate = date
        checkEnableGoButtons()
    }

    fun onToDateSet(date: String)
    {
        model.toDate = date
        checkEnableGoButtons()
    }

    fun onCountryButton()
    {
        if (model.currentCountry != null) {
            view.launchStatsActivity(model.currentCountry!!, model.currentCountry!!,model.fromDate, model.toDate)
        }
    }

    fun onRegionButton()
    {
        if (model.currentRegion != null && model.currentCountry != null) {
            view.launchStatsActivity(model.currentRegion!!, model.currentCountry!!, model.fromDate, model.toDate)
        }
    }

    fun onSubregionButton()
    {
        if (model.currentSubregion != null && model.currentCountry != null) {
            view.launchStatsActivity(model.currentSubregion!!, model.currentCountry!!, model.fromDate, model.toDate)
        }
    }

    private fun checkEnableGoButtons()
    {
        if (model.fromDate != "" && model.toDate != "" && !view.isLoading){
            view.enabledCountryButton = (model.currentCountry != null)
            view.enabledRegionButton = (model.currentRegion != null)
            view.enabledSubregionButton = (model.currentSubregion != null)
        }
    }
}