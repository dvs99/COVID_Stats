package es.uji.al375496.covidstats.presenter

import com.android.volley.*
import es.uji.al375496.covidstats.model.StatsModel
import es.uji.al375496.covidstats.model.data.DayData
import es.uji.al375496.covidstats.model.data.StatsQueryInfo
import es.uji.al375496.covidstats.view.StatsView


class StatsPresenter (private val view: StatsView, model: StatsModel, info: StatsQueryInfo)
{
    init
    {
        //fetch the data from the internet
        view.isLoading = true
        model.getStats(info, Response.Listener { data ->
            when{
                data == null ->     view.showError("Possible bad JSON received from the server")
                data.isEmpty() ->   view.showError("No data:\nCheck date range")
                else-> {
                    view.displayStats(data)
                    view.isLoading = false }
                }
        }, Response.ErrorListener { error ->
            view.showError("Network error: ${ErrorParser.parseNetworkError(error)}")
        })

    }

    fun itemSelected(dayData: DayData)
    {
        view.displayDialog(dayData)
    }
}