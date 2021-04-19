package es.uji.al375496.covidstats.model

import android.content.Context
import com.android.volley.Response
import es.uji.al375496.covidstats.model.data.*

class StatsModel (context: Context)
{
    private var networkAccess = NetworkAccess.getInstance(context)

    fun getStats (info: StatsQueryInfo, listener: Response.Listener<List<DayData>>, errorListener: Response.ErrorListener)
    {
        networkAccess.getStats(info, listener, errorListener)
    }
}