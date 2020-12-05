package com.example.blockassessmentsurvey

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mMap: GoogleMap
    private lateinit var mUseCurrentLocationButton: TextView
    private lateinit var mStateSpinner: Spinner
    private lateinit var mCityName: EditText
    private lateinit var mStreetName: EditText
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private lateinit var mGeocoder: Geocoder
    private var hasLocationPermission = false

    companion object {
        const val LOCATION_REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        // If the user is not logged in, return to the login screen since they must be logged in at this point
        // Adapted from https://firebase.google.com/docs/auth/android/manage-users#kotlin+ktx
        if (mAuth.currentUser == null) {
            toLoginActivity()
        }

        mUseCurrentLocationButton = findViewById(R.id.useCurrentLocationBtn)
        mStateSpinner = findViewById(R.id.spinner)
        mCityName = findViewById(R.id.editTextCityName)
        mStreetName = findViewById(R.id.editTextStreetName)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mGeocoder = Geocoder(this)
        val mSeeReviewsButton = findViewById<Button>(R.id.seeReviewsButton)

        mUseCurrentLocationButton.setOnClickListener {
            updateUIWithLocationData()
        }

        //val mMap = findViewById<MapView>(R.id.mapView)
        //val mRadius = findViewById<SeekBar>(R.id.radiusSeek)
        //val mSearch = findViewById<SearchView>(R.id.blockSearch)
        //val milesTextView = findViewById<TextView>(R.id.radiusTextView)
        //val mReviewButton = findViewById<Button>(R.id.reviewLocationButton)

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        mStateSpinner!!.adapter = aa

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
            Places.initialize(applicationContext, apiKey);
        }

        //val placesClient = Places.createClient(this)

        /*val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?*/

        /*autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener( object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.name + ", " + place.id)
                // TODO: Set variable to make this selected place that will get passed as an intent

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $status")
            }
        })*/


        mSeeReviewsButton.setOnClickListener {
            val street = mStreetName.text.toString()
            val city = mCityName.text.toString()
            val state = mStateSpinner.selectedItem.toString()

            if (street.isEmpty() || city.isEmpty() || state.isEmpty()) {
                Toast.makeText(this, getString(R.string.loc_empty), Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, ViewReviewsActivity::class.java)
                intent.putExtra("State", mStateSpinner.selectedItem.toString())
                intent.putExtra("City", mCityName.text.toString())
                intent.putExtra("Street", mStreetName.text.toString())

                intent.resolveActivity(packageManager)?.let {
                    startActivity(intent)
                }
            }
        }

        //mapFragment.getMapAsync(this)
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationClient.removeLocationUpdates(LocationCallback())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Partially adapted from Lab 11 - Location
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                var hasPermission = true

                // grantResults should have the results of asking for both coarse and fine location.
                // If either one is granted, the permission is considered granted
                for (r in grantResults) {
                    hasPermission = hasPermission || r == 1
                }

                hasLocationPermission = hasPermission

                updateUIWithLocationData()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out_item -> {
                mAuth.signOut()
                toLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toLoginActivity() {
        val loginIntent = Intent(this, LoginRegisterActivity::class.java)

        intent.resolveActivity(packageManager)?.let {
            startActivity(loginIntent)
        }
    }

    private fun checkPermissions() {
        hasLocationPermission = !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    }

    private fun getCurrentLocation(callback: (() -> Unit)) {
        if (hasLocationPermission) {
            // Adapted from https://developer.android.com/training/location/retrieve-current.html
            mFusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            mLocation = location
                            callback()
                        } else {
                            Toast.makeText(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG)
                                    .show()
                        }
                    }
        }
    }

    private fun updateUIWithLocationData() {
        checkPermissions()

        if (hasLocationPermission) {
            getCurrentLocation {
                if (mLocation != null) {
                    // Use the Geocoder library to try to determine the user's address from the latitude and longitude.
                    try {
                        val locLst = mGeocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)

                        if (locLst.isNotEmpty()) {
                            val locItem = locLst[0]

                            if (locItem != null
                                    && locItem.thoroughfare != null && locItem.locality != null
                                    && locItem.adminArea != null) {
                                // If a location is found, pre-fill the form fields with the address info
                                // Get the name of the state
                                val stateInd = states.indexOf(locItem.adminArea)

                                if (stateInd == -1) {
                                    // Currently US locations only
                                    Toast.makeText(this, getString(R.string.loc_us_only), Toast.LENGTH_LONG).show()
                                } else {
                                    // Spinner setSelection adapted from https://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically
                                    Toast.makeText(this, getString(R.string.loc_success), Toast.LENGTH_LONG).show()
                                    mStateSpinner.setSelection(stateInd)
                                    mStreetName.setText(locItem.thoroughfare)
                                    mCityName.setText(locItem.locality)
                                }
                            }
                        } else {
                            Toast.makeText(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
                .setTitle("Welcome to Block Assessment Survey")
                .setMessage(R.string.need_cur_loc)
                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_REQUEST_CODE
                    )
                }
                .show()
    }
}