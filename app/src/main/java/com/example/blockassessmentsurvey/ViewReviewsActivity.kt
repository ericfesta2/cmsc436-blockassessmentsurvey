package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.lang.Exception

class ViewReviewsActivity : AppCompatActivity() {
    private lateinit var mContentLayout: LinearLayout
    private lateinit var mLayoutInflater: LayoutInflater

    var safety: Float = 0f
    var air: Float = 0f
    var cleanliness: Float = 0f
    var parking: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)
        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")
        title = "Block Reviews"

        mContentLayout = findViewById(R.id.mainContentLayout)
        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val mBlockNameText = findViewById<TextView>(R.id.blockName)
        mBlockNameText.text = street

        val path = "$state/$city/$street"

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(path)

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener() {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        findViewById<FloatingActionButton>(R.id.viewCommentsBtn).setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            // TODO: putExtra block name
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                safety = 0f
                air= 0f
                cleanliness = 0f
                parking = 0f

                Log.i("View Reviews", dataSnapshot.toString())
                val reviews = dataSnapshot.value as Map<*, Map<String, *>>
                val reviews1 = reviews.values
                for (review in reviews1) {
                    try {
                        Log.i("View Reviews", review.get("safety").toString())
                        safety = safety.plus(review["safety"] as Long)
                        air = air.plus(review["air"] as Long)
                        cleanliness = cleanliness.plus(review["cleanliness"] as Long)
                        parking = parking.plus(review["parking"] as Long)
                    } catch (e: Exception) {
                        Log.i("View Reviews", e.toString())
                    } finally {

                    }
                }

                Log.i("Safety", safety.toString())
                safety /= reviews1.count()
                air /= reviews1.count()
                cleanliness /= reviews1.count()
                parking /= reviews1.count()
                getStars()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    // TODO: Get rating aggregates from Firebase
    fun getStars() {
        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_view, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            var stars = when (dimension.id) {
                "d_safety" -> safety
                "d_air_quality" -> air
                "d_cleanliness" -> cleanliness
                "d_parking_spaces" -> parking
                else -> 0f
            }

            view.findViewById<RatingBar>(R.id.ratingBar).rating = stars

            mContentLayout.addView(view)
        }
    }
}