package es.uji.al375496.covidstats.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.uji.al375496.covidstats.R

class DayViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val date: TextView = view.findViewById(R.id.dateTextView)
    val totalCases: TextView = view.findViewById(R.id.totalCasesTextView)
    val newCases: TextView = view.findViewById(R.id.newCasesTextView)
}