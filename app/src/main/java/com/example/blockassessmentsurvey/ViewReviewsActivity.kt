package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ViewReviewsActivity : AppCompatActivity() {
    private lateinit var mContentLayout: LinearLayout
    private lateinit var mLayoutInflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)
        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")

        mContentLayout = findViewById(R.id.mainContentLayout)
        mLayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val mBlockNameText = findViewById<TextView>(R.id.blockName)
        mBlockNameText.text = street

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: DatabaseReference = database.getReference("message")

        myRef.setValue("Hello, World!")

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener() {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        findViewById<FloatingActionButton>(R.id.viewCommentsBtn).setOnClickListener() {
            val intent = Intent(this, CommentsActivity::class.java)
            // TODO: putExtra block name
            intent.putExtra("State", state)
            intent.putExtra("City", city)
            intent.putExtra("Street", street)

            intent.resolveActivity(packageManager)?.let {
                startActivity(intent)
            }
        }

        // TODO: Get rating aggregates from Firebase
        for (dimension in ratingDimensions) {
            val view = mLayoutInflater.inflate(R.layout.rating_dimension_ratingbar_view, null)

            view.findViewById<TextView>(R.id.heading).text = dimension.heading
            view.findViewById<TextView>(R.id.desc).text = dimension.desc

            mContentLayout.addView(view)
        }
    }
}