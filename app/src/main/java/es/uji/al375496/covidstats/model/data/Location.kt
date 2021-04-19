package es.uji.al375496.covidstats.model.data

import android.os.Parcelable

interface Location: Parcelable {
    val name: String
    val id: String
}