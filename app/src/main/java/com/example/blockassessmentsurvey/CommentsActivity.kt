package com.example.blockassessmentsurvey

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class CommentsActivity : AppCompatActivity() {

    val reviewList = mutableListOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comments_page)

        val state = intent.getStringExtra("State")
        val city = intent.getStringExtra("City")
        val street = intent.getStringExtra("Street")

        val path = state + "/" + city + "/" + street

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(path)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.i("View Reviews", dataSnapshot.toString())
                val reviews = dataSnapshot.value as Map<*, Map<String, *>>
                val reviews1 = reviews.values
                for (review in reviews1) {
                    try {
                        Log.i("View Comments", review.get("comments").toString())
                        val r = Review(review["reviewer"] as String, 0f, 0f, 0f, 0f, review["posted"] as String, review["comments"] as String)
                        reviewList.add(r)
                        update()
                    } catch (e: Exception) {
                        Log.i("View Comments", e.toString())

                    } finally {

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    fun update() {
        val reviewAdapter = ReviewList(this, reviewList)
        findViewById<ListView>(R.id.reviewsList).adapter = reviewAdapter
    }
}