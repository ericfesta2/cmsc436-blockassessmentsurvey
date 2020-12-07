package com.example.blockassessmentsurvey

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUseCurrentLocationButton: TextView
    private lateinit var mStateSpinner: Spinner
    private lateinit var mCityName: EditText
    private lateinit var mStreetName: EditText
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private lateinit var mGeocoder: Geocoder

    // Keep the references to Toasts so that they can be hidden when leaving the activity
    private lateinit var mToasts: MutableList<Toast>

    // True when the user has granted the app at least one location permission.
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
        // FusedLocationProviderClient is similar to LocationService from the labs,
        // but it automatically combines GPS/network location data
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Geocoder enables obtaining street addresses from latitude/longitude (if possible)
        mGeocoder = Geocoder(this)
        mToasts = mutableListOf()

        val mSeeReviewsButton = findViewById<Button>(R.id.seeReviewsButton)

        mUseCurrentLocationButton.setOnClickListener {
            // When the user presses the "Use My Current Location" link, update the UI with either
            // their location data (if possible) or a dialog requesting location permissions.
            updateUIWithLocationData()
        }

        // Populate the Spinner with state names.
        // https://developer.android.com/guide/topics/ui/controls/spinner
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        // Set layout to use when the list of choices appear.
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner.
        mStateSpinner.adapter = aa

        mSeeReviewsButton.setOnClickListener {
            // Upon pressing the "See Reviews for Selected Location" button,
            // send the user-inputted (or autofilled) street, city, and state values to the View Reviews activity,
            // which will look up that location in Firebase to aggregate existing user reviews for it.
            val street = mStreetName.text.toString()
            val city = mCityName.text.toString()
            val state = mStateSpinner.selectedItem.toString()

            if (street.isEmpty() || city.isEmpty() || state.isEmpty()) {
                makeToast(this, getString(R.string.loc_empty), Toast.LENGTH_LONG)
            } else {
                // If the address is valid and non-empty, go to ViewReviewsActivity with the address data.
                // ViewReviewsActivity shows the reviews for the specified location (if any)
                // and prompts the user to add a review or view comments about that location.
                val intent = Intent(this, ViewReviewsActivity::class.java)
                intent.putExtra("State", mStateSpinner.selectedItem.toString())
                intent.putExtra("City", mCityName.text.toString())
                intent.putExtra("Street", mStreetName.text.toString())

                intent.resolveActivity(packageManager)?.let {
                    startActivity(intent)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Remove any background location updates when the user is about to leave MainActivity.
        mFusedLocationClient.removeLocationUpdates(LocationCallback())
        cancelToasts()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Partially adapted from Lab 11 - Location
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                var hasPermission = true

                // grantResults should have the results of asking for both coarse and fine location.
                // If either one is granted, the permission is considered granted.
                for (r in grantResults) {
                    hasPermission = hasPermission || r == 1
                }

                hasLocationPermission = hasPermission

                updateUIWithLocationData()
            }
        }
    }

    // Menu inflation and item selection methods adapted from https://developer.android.com/guide/topics/ui/menuss
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Add the Log Out button to the top bar.
        // This same logic is present in almost every other activity as well.
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out_item -> {
                // Log the user out and redirect them to the login page when they press Log Out.
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
        // Refreshes whether or not the user has granted the app location permissions.
        // This may change any time throughout the user's interaction with the app, so this is called in various parts of MainActivity for extra verification.
        // Adapted from Lab 11 - Location and Android Studio autocomplete
        hasLocationPermission = !(
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
    }

    /** Android Studio marks mFusedLocationClient.lastLocation and mFusedLocationClient.requestLocationUpdates()
     *  in red as it does not see a location permission check near them;
     *  however, the hasLocationPermission variable in conjunction with checkPermissions is equivalent to that,
     *  so the MissingPermission lint is suppressed to remove the linting error.
     */
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(callback: (() -> Unit)) {
        // Try to obtain the user's current location so that it can autofill the address fields.
        checkPermissions()

        if (hasLocationPermission) {
            // Adapted from https://developer.android.com/training/location/retrieve-current.html
            // Android Studio marks the line below as red, as it does not see an explicit permission request around it,
            // but the hasLocationPermission variable in conjunction with checkPermissions is equivalent to that.
            mFusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            // Get the location if possible.
                            // The Android emulator will always return the coordinates of Google headquarters (Amphitheatre Parkway, Mountain View, CA),
                            // but on a real device this will get the user's actual location.
                            mLocation = location
                            callback()
                        } else {
                            // The location response could be null due to a variety of reasons, like a new device never having requested the location before.
                            // If this is the case, the app will submit a location request to force mFusedLocationClient to refresh its location.
                            // Adapted from https://developer.android.com/training/location/request-updates#kotlin
                            val locationRequest = LocationRequest.create()
                            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            locationRequest.interval = 1000
                            // One request should be enough to obtain the location; more than that at a repeated interval could be superfluous and battery-draining.
                            locationRequest.numUpdates = 1

                            val locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    // Once the FusedLocationProviderClient has received location data upon the app's request,
                                    // update the UI accordingly if there is non-null location data.
                                    for (altLocation in locationResult.locations) {
                                        if (altLocation != null) {
                                            mLocation = altLocation
                                            updateUIWithLocationData()
                                        }
                                    }

                                    if (mLocation == null) {
                                        // If the user's location still cannot be obtained, give up and prompt the user to input it manually.
                                        makeToast(this@MainActivity, getString(R.string.loc_not_found), Toast.LENGTH_LONG)
                                    }
                                }
                            }

                            // Begin requesting location updates explicitly based on the above parameters.
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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
                                    makeToast(this, getString(R.string.loc_us_only), Toast.LENGTH_LONG)
                                } else {
                                    // Spinner setSelection adapted from https://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically
                                    makeToast(this, getString(R.string.loc_success), Toast.LENGTH_LONG)
                                    mUseCurrentLocationButton.text = getString(R.string.loc_in_use)
                                    // Set the location icon to the filled-in one to denote location being used.
                                    // Source: https://stackoverflow.com/questions/4502605/how-to-programmatically-set-drawableleft-on-android-button
                                    mUseCurrentLocationButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_baseline_location_on_24), null, null, null)
                                    // Italic transformation adapted from https://stackoverflow.com/questions/6200533/how-to-set-textview-textstyle-such-as-bold-italic
                                    mUseCurrentLocationButton.setTypeface(mUseCurrentLocationButton.typeface, Typeface.ITALIC)
                                    mStateSpinner.setSelection(stateInd)
                                    mStreetName.setText(locItem.thoroughfare)
                                    mCityName.setText(locItem.locality)
                                }
                            }
                        } else {
                            makeToast(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG)
                        }
                    } catch (e: IOException) {
                        Log.e("LOC_ERR", e.toString())
                        makeToast(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG)
                    }

                } else {
                    makeToast(this, getString(R.string.loc_not_found), Toast.LENGTH_LONG)
                }
            }
        } else {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        // This dialog shows when the user presses the "Use My Current Location" button but
        // has not granted the app location permissions.
        // Adapted from the midterm assessment
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

    private fun makeToast(context: Context, text: CharSequence, duration: Int) {
        // Generate and show Toasts as normal, but add their references to a list
        // so they can be hidden when going to another activity.
        val toast = Toast.makeText(context, text, duration)
        mToasts.add(toast)
        toast.show()
    }

    private fun cancelToasts() {
        // Hide any Toasts still active so they don't interfere with the next activity.
        for (t in mToasts) {
            t.cancel()
        }

        mToasts.clear()
    }
}