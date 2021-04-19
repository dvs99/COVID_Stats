package es.uji.al375496.covidstats.view

import es.uji.al375496.covidstats.model.data.DayData

interface StatsView
{
    var isLoading: Boolean

    fun showError(message: String)
    fun displayDialog(data: DayData)
    fun displayStats(stats: List<DayData>)
}