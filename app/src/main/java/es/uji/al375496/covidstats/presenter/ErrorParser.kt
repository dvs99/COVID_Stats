package es.uji.al375496.covidstats.presenter

import com.android.volley.*

class ErrorParser
{
    companion object Parser
    {
        fun parseNetworkError(error : VolleyError): String
        {
            return when(error)
            {
                is NoConnectionError -> return "There is no internet connection"
                is NetworkError -> return "Connection failed"
                is ServerError -> return "Server error"
                is AuthFailureError -> return "Authentication error"
                is ParseError -> return "Parse error"
                is TimeoutError -> return "Connection timed out"
                else -> "Unknown error"
            }
        }
    }
}