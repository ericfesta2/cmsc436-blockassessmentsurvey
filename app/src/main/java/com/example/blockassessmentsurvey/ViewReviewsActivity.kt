package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewReviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_review)

        findViewById<FloatingActionButton>(R.id.addReviewButton).setOnClickListener() {
            val newReviewIntent = Intent(this, ReviewActivity::class.java)

            newReviewIntent.resolveActivity(packageManager)?.let {
                startActivity(newReviewIntent)
            }
        }
    }
}