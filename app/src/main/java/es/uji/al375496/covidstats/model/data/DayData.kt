package es.uji.al375496.covidstats.model.data

data class DayData (val date: String, val totalCases: Int, val newCases: Int, val totalDeaths: Int, val newDeaths: Int, val totalRecoveries: Int, val newRecoveries: Int) {
}