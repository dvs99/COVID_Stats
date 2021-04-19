package es.uji.al375496.covidstats.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.uji.al375496.covidstats.IMPOSSIBLE_VALUE
import es.uji.al375496.covidstats.R
import es.uji.al375496.covidstats.model.data.DayData
import es.uji.al375496.covidstats.presenter.StatsPresenter

class DayAdapter (private val days: List<DayData>, private val presenter: StatsPresenter): RecyclerView.Adapter<DayViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_stats, parent,false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int)
    {
        with(days[position]) {
            holder.date.text = date
            holder.totalCases.text = if (totalCases != IMPOSSIBLE_VALUE) totalCases.toString() else "unknown"
            holder.newCases.text = if (newCases != IMPOSSIBLE_VALUE) newCases.toString() else "unknown"
            holder.view.setOnClickListener {
                presenter.itemSelected(this)
            }
        }
    }

    override fun getItemCount(): Int
    {
        return days.size
    }
}