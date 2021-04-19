package es.uji.al375496.covidstats

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import es.uji.al375496.covidstats.model.MainModel
import es.uji.al375496.covidstats.model.data.Location
import es.uji.al375496.covidstats.model.data.StatsQueryInfo
import es.uji.al375496.covidstats.model.data.database.Country
import es.uji.al375496.covidstats.model.data.database.Region
import es.uji.al375496.covidstats.model.data.database.Subregion
import es.uji.al375496.covidstats.presenter.MainPresenter
import es.uji.al375496.covidstats.view.MainView
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity(), MainView
{
    private lateinit var loadingTextView: TextView
    private lateinit var loadingBar: ProgressBar

    private lateinit var countryTextView: TextView
    private lateinit var countryAutoCompleteTextView: AutoCompleteTextView
    private lateinit var countryButton: Button
    private lateinit var regionTextView: TextView
    private lateinit var regionAutoCompleteTextView: AutoCompleteTextView
    private lateinit var regionButton: Button
    private lateinit var subregionTextView: TextView
    private lateinit var subregionAutoCompleteTextView: AutoCompleteTextView
    private lateinit var subregionButton: Button
    private lateinit var fromTextView: TextView
    private lateinit var fromDate: EditText
    private lateinit var toTextView: TextView
    private lateinit var toDate: EditText

    private lateinit var retryButton: Button

    private lateinit var presenter: MainPresenter

    private var justCreated  = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        justCreated = true
        setContentView(R.layout.activity_main)

        loadingTextView = findViewById(R.id.loadingTextView)
        loadingBar = findViewById(R.id.loadingProgressBar)

        countryTextView = findViewById(R.id.countyTextView)
        countryAutoCompleteTextView = findViewById(R.id.countryAutoCompleteTextView)
        countryButton = findViewById(R.id.countryGoButton)
        regionTextView = findViewById(R.id.regionTextView)
        regionAutoCompleteTextView = findViewById(R.id.regionAutoCompleteTextView)
        regionButton = findViewById(R.id.regionGoButton)
        subregionTextView = findViewById(R.id.subregionTextView)
        subregionAutoCompleteTextView = findViewById(R.id.subregionAutoCompleteTextView)
        subregionButton = findViewById(R.id.subregionGoButton)

        fromTextView = findViewById(R.id.fromTextView)
        fromDate = findViewById(R.id.fromEditTextDate)
        toTextView = findViewById(R.id.toTextView)
        toDate = findViewById(R.id.toEditTextDate)

        retryButton = findViewById(R.id.retryButton)

        //config the date editText calendar popups
        val calendar = Calendar.getInstance()
        val f = DecimalFormat("00")
        fromDate.setOnClickListener {
            val year: Int
            val month: Int
            val day: Int
            if (fromDate.text.toString() != "")
            {
                year = fromDate.text.subSequence(0,4).toString().toInt()
                month = fromDate.text.subSequence(5,7).toString().toInt() -1
                day = fromDate.text.subSequence(8,10).toString().toInt()
            }
            else
            {
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
            }
            val picker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{ _, setYear, setMonth, SetDay ->
                val dateString = "${f.format(setYear)}-${f.format(setMonth+1)}-${f.format(SetDay)}"
                fromDate.setText(dateString)
                presenter.onFromDateSet(dateString)
            }, year, month, day)
            picker.show()
        }
        toDate.setOnClickListener {
            val year: Int
            val month: Int
            val day: Int
            if (toDate.text.toString() != "")
            {
                year = toDate.text.subSequence(0,4).toString().toInt()
                month = toDate.text.subSequence(5,7).toString().toInt() -1
                day = toDate.text.subSequence(8,10).toString().toInt()
            }
            else
            {
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
            }
            val picker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{ _, setYear, setMonth, SetDay ->
                val dateString = "${f.format(setYear)}-${f.format(setMonth+1)}-${f.format(SetDay)}"
                toDate.setText(dateString)
                presenter.onToDateSet(dateString)
            }, year, month, day)
            picker.show()
        }

        val model = MainModel(applicationContext)
        presenter= MainPresenter(this, model)
    }

    override fun onResume() {
        super.onResume()
        if (justCreated)
        {
            //clears date fields after recreating and resuming the app so that the behaviour is consistent with the other fields
            fromDate.text.clear()
            toDate.text.clear()
            justCreated = false
        }
    }

    override var enabledCountryButton: Boolean
        get() = countryButton.isEnabled
        set(value) {countryButton.isEnabled = value}

    override var enabledRegionButton: Boolean
        get() = regionButton.isEnabled
        set(value) {regionButton.isEnabled = value}

    override var enabledSubregionButton: Boolean
        get() = subregionButton.isEnabled
        set(value) {subregionButton.isEnabled = value}

    override var isLoading: Boolean //sets the visibility of the necessary elements
        get() = loadingBar.visibility == View.VISIBLE
        set(value) {
            if (value)
            {   //disable everything when loading and show loading
                loadingBar.visibility = View.VISIBLE
                loadingTextView.visibility = View.VISIBLE

                countryTextView.isEnabled = false
                regionTextView.isEnabled = false
                subregionTextView.isEnabled = false
                countryAutoCompleteTextView.isEnabled = false
                regionAutoCompleteTextView.isEnabled = false
                subregionAutoCompleteTextView.isEnabled = false
                fromTextView.isEnabled = false
                fromDate.isEnabled = false
                toTextView.isEnabled = false
                toDate.isEnabled = false

                enabledCountryButton = false
                enabledRegionButton = false
                enabledSubregionButton = false
            }
            else
            {   //hide loading, enable and show dates. Countries, regions and subregions are auto-enabled when populated with lists.
                loadingBar.visibility = View.GONE
                loadingTextView.visibility = View.GONE

                fromDate.visibility = View.VISIBLE
                fromTextView.visibility = View.VISIBLE
                toDate.visibility = View.VISIBLE
                toTextView.visibility = View.VISIBLE

                fromDate.isEnabled = true
                fromTextView.isEnabled = true
                toDate.isEnabled = true
                toTextView.isEnabled = true
            }
    }

    override fun onGoButton(view: View)
    {
        when (view.id)
        {
            R.id.countryGoButton -> presenter.onCountryButton()
            R.id.regionGoButton -> presenter.onRegionButton()
            R.id.subregionGoButton -> presenter.onSubregionButton()
        }
    }

    override fun showError(message: String)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showCountries(countries: List<Country>)
    {
        //display the UI and enable the countries
        countryTextView.visibility = View.VISIBLE
        countryAutoCompleteTextView.visibility = View.VISIBLE
        countryButton.visibility = View.VISIBLE

        countryTextView.isEnabled = true
        countryAutoCompleteTextView.isEnabled = true

        //add the list of countries to the autocompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countries)
        countryAutoCompleteTextView.apply{
            setAdapter(adapter)
            setText("")
            addTextChangedListener(object: TextWatcher
            {
                override fun afterTextChanged(s: Editable?) {
                    val str = s.toString()
                    countries.binarySearch { it.name.compareTo(str) }.let {
                        if(it >= 0){
                            presenter.onCountrySelected(countries[it])}
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    presenter.onCountryDeselected()
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isEmpty())
                    showDropDown()
            }
        }
    }

    override fun showRegions(regions: List<Region>)
    {
        //enable countries
        countryTextView.isEnabled = true
        countryAutoCompleteTextView.isEnabled = true

        if (regions.isNotEmpty()) //do not enable and populate the regions if they do not exist
        {
            //enable regions
            regionTextView.isEnabled = true
            regionAutoCompleteTextView.isEnabled = true
            regionTextView.visibility = View.VISIBLE
            regionAutoCompleteTextView.visibility = View.VISIBLE
            regionButton.visibility = View.VISIBLE

            //add the list of regions to the autocompleteTextView
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regions)
            regionAutoCompleteTextView.apply{
                setAdapter(adapter)
                setText("")
                addTextChangedListener(object: TextWatcher
                {
                    override fun afterTextChanged(s: Editable?) {
                        val str = s.toString()
                        regions.binarySearch { it.name.compareTo(str) }.let {
                            if(it >= 0)
                                presenter.onRegionSelected(regions[it])}
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        presenter.onRegionDeselected()
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && text.isEmpty())
                        showDropDown()
                }
            }
        }
    }

    override fun hideRegions()
    {
        regionTextView.visibility = View.GONE
        regionAutoCompleteTextView.visibility = View.GONE
        regionButton.visibility = View.GONE

        regionTextView.isEnabled = false
        regionAutoCompleteTextView.isEnabled = false
        regionAutoCompleteTextView.setText("")
    }

    override fun showSubregions(subregions: List<Subregion>)
    {
        //enable countries and regions
        countryTextView.isEnabled = true
        regionTextView.isEnabled = true
        countryAutoCompleteTextView.isEnabled = true
        regionAutoCompleteTextView.isEnabled = true

        regionTextView.visibility = View.VISIBLE
        regionAutoCompleteTextView.visibility = View.VISIBLE
        regionButton.visibility = View.VISIBLE

        if (subregions.isNotEmpty()) //do not enable and populate the subregions if they do not exist
        {
            //enable subregions
            subregionTextView.isEnabled = true
            subregionAutoCompleteTextView.isEnabled = true

            subregionTextView.visibility = View.VISIBLE
            subregionAutoCompleteTextView.visibility = View.VISIBLE
            subregionButton.visibility = View.VISIBLE

           //add the list of countries to the autocompleteTextView
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subregions)
            subregionAutoCompleteTextView.apply{
                setAdapter(adapter)
                setText("")
                addTextChangedListener(object: TextWatcher
                {
                    override fun afterTextChanged(s: Editable?) {
                        val str = s.toString()
                        subregions.binarySearch { it.name.compareTo(str) }.let {
                            if(it >= 0)
                                presenter.onSubregionSelected(subregions[it])}
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        presenter.onSubregionDeselected()
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && text.isEmpty()) showDropDown()
                }
            }
        }
    }

    override fun hideSubregions() {
        subregionTextView.isEnabled = false
        subregionAutoCompleteTextView.isEnabled = false
        subregionAutoCompleteTextView.setText("")

        subregionTextView.visibility = View.GONE
        subregionAutoCompleteTextView.visibility = View.GONE
        subregionButton.visibility = View.GONE
    }

    override fun launchStatsActivity(location: Location, country: Country, fromDate: String, toDate: String) {
        val intent = Intent(this, StatsActivity::class.java).apply {
            putExtra(STATS_QUERY_INFO, StatsQueryInfo(location, country, fromDate, toDate))
        }
        startActivity(intent)
    }

    override fun showRetry() {
        retryButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }
        retryButton.visibility = View.VISIBLE
        fromDate.visibility = View.GONE
        toDate.visibility = View.GONE
        toTextView.visibility = View.GONE
        fromTextView.visibility = View.GONE
    }

    override fun clearRegion() {
        regionAutoCompleteTextView.text.clear()

        countryTextView.isEnabled = true
        regionTextView.isEnabled = true
        countryAutoCompleteTextView.isEnabled = true
        regionAutoCompleteTextView.isEnabled = true

        regionTextView.visibility = View.VISIBLE
        regionAutoCompleteTextView.visibility = View.VISIBLE
        regionButton.visibility = View.VISIBLE
        countryButton.visibility = View.VISIBLE
        countryAutoCompleteTextView.visibility = View.VISIBLE
        countryTextView.visibility = View.VISIBLE
    }

    override fun clearCountry() {
        countryAutoCompleteTextView.text.clear()
        countryTextView.isEnabled = true
        countryAutoCompleteTextView.isEnabled = true

        countryButton.visibility = View.VISIBLE
        countryAutoCompleteTextView.visibility = View.VISIBLE
        countryTextView.visibility = View.VISIBLE
    }
}