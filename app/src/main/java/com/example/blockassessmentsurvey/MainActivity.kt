package com.example.blockassessmentsurvey

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.gms.maps.MapView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mMap = findViewById<MapView>(R.id.mapView)
        val mRadius = findViewById<SeekBar>(R.id.radiusSeek)
        val mSearch = findViewById<SearchView>(R.id.blockSearch)
        val milesTextView = findViewById<TextView>(R.id.radiusTextView)
        //val mReviewButton = findViewById<Button>(R.id.reviewLocationButton)
        val mSeeReviewsButton = findViewById<Button>(R.id.seeReviewsButton)

        /*mReviewButton.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            //TODO:: Include currently selected block as an extra
            startActivity(intent)
        }*/

        mSeeReviewsButton.setOnClickListener {
            val intent = Intent(this, ViewReviewsActivity::class.java)
            //TODO: Include the currently selected block as an extra
            startActivity(intent)
        }
    }
}