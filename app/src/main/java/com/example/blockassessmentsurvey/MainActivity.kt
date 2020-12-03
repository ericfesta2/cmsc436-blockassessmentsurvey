package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

private lateinit var mMap: GoogleMap

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val mMap = findViewById<MapView>(R.id.mapView)
        //val mRadius = findViewById<SeekBar>(R.id.radiusSeek)
        //val mSearch = findViewById<SearchView>(R.id.blockSearch)
        //val milesTextView = findViewById<TextView>(R.id.radiusTextView)
        //val mReviewButton = findViewById<Button>(R.id.reviewLocationButton)
        val mSeeReviewsButton = findViewById<Button>(R.id.seeReviewsButton)
        val mSpinner = findViewById<Spinner>(R.id.spinner)
        val mCityName = findViewById<EditText>(R.id.editTextCityName)
        val mStreetName = findViewById<EditText>(R.id.editTextStreetName)

        var states = arrayOf("NJ", "NY", "MD")

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        mSpinner!!.setAdapter(aa)

        /*val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()*/




        val apiKey = getString(R.string.api_key)

        /*mReviewButton.setOnClickListener() {
            val intent = Intent(this, ReviewActivity::class.java)
            //TODO:: Include currently selected block as an extra
            startActivity(intent)
        }*/

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        val placesClient = Places.createClient(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?

        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment!!.setOnPlaceSelectedListener( object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.name + ", " + place.id)
                // TODO: Set variable to make this selected place that will get passed as an intent

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $status")
            }
        })


        mSeeReviewsButton.setOnClickListener() {
            val intent = Intent(this, ViewReviewsActivity::class.java)
            //TODO: Include the currently selected block as an extr
            intent.putExtra("State", mSpinner.selectedItem.toString())
            intent.putExtra("City", mCityName.text.toString())
            intent.putExtra("Street", mStreetName.text.toString())
            startActivity(intent)
        }

        //mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("TAG", "SETTING MAP")
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }
}