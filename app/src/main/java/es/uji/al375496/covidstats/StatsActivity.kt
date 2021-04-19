package es.uji.al375496.covidstats

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uji.al375496.covidstats.model.StatsModel
import es.uji.al375496.covidstats.model.data.DayData
import es.uji.al375496.covidstats.model.data.StatsQueryInfo
import es.uji.al375496.covidstats.presenter.StatsPresenter
import es.uji.al375496.covidstats.view.DayAdapter
import es.uji.al375496.covidstats.view.StatsView

class  StatsActivity : AppCompatActivity(), StatsView
{
    private lateinit var dayView: RecyclerView
    private lateinit var loadingBar: ProgressBar
    private lateinit var loadingText: TextView

    private lateinit var presenter: StatsPresenter

    private val dayList: MutableList<DayData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        loadingBar = findViewById(R.id.loadingProgressBar)
        loadingText = findViewById(R.id.loadingTextView)
        dayView = findViewById(R.id.recyclerView)

        val nullableInfo: StatsQueryInfo? = intent.getParcelableExtra(STATS_QUERY_INFO)
        if (nullableInfo != null)
        {
            val statsQueryInfo: StatsQueryInfo = nullableInfo
            title = statsQueryInfo.location.name

            val model = StatsModel(applicationContext)
            presenter = StatsPresenter(this, model, statsQueryInfo)

            //set up the recyclerview
            dayView.adapter = DayAdapter(dayList, presenter)
            dayView.layoutManager = LinearLayoutManager(this).apply{
                reverseLayout = true
                stackFromEnd = true
            }

            isLoading = true

        }
        else finish()
    }

    override var isLoading: Boolean
        get() = (loadingBar.visibility == View.VISIBLE)
        set(value) {
            if (value){
            loadingBar.visibility = View.VISIBLE
            dayView.visibility = View.INVISIBLE
            }
            else {
                loadingBar.visibility = View.INVISIBLE
                loadingText.visibility = View.INVISIBLE
                dayView.visibility = View.VISIBLE
            }
        }

    override fun showError(message: String)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun displayDialog(data: DayData)
    {
        val builder = AlertDialog.Builder(this)

        val dialog = builder.apply {
            setMessage("\nTotal cases:\t\t\t\t\t\t\t\t\t${if (data.totalCases != IMPOSSIBLE_VALUE) data.totalCases.toString() else "unknown"}\n" +
                    "New cases:\t\t\t\t\t\t\t\t\t${if (data.newCases != IMPOSSIBLE_VALUE) data.newCases.toString() else "unknown"}\n\n" +
                    "Total recoveries:\t\t\t\t\t${if (data.totalRecoveries != IMPOSSIBLE_VALUE) data.totalRecoveries.toString() else "unknown"}\n" +
                    "New recoveries:\t\t\t\t\t${if (data.newRecoveries != IMPOSSIBLE_VALUE) data.newRecoveries.toString() else "unknown"}\n\n" +
                    "Total deaths:\t\t\t\t\t\t\t\t${if (data.totalDeaths != IMPOSSIBLE_VALUE) data.totalDeaths.toString() else "unknown"}\n" +
                    "New deaths:\t\t\t\t\t\t\t\t\t${if (data.newDeaths != IMPOSSIBLE_VALUE) data.newDeaths.toString() else "unknown"}\n")

            setTitle(data.date)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss()}
            create()
        }

        dialog.show()
    }

    override fun displayStats(stats: List<DayData>)
    {
        dayList.clear()
        dayList.addAll(stats)
        dayView.adapter?.notifyDataSetChanged()
    }
}