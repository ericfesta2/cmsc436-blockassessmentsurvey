package com.example.blockassessmentsurvey

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ReviewList(private val context: Activity, private var reviews: List<Review>) : ArrayAdapter<Review>(context,
    R.layout.review_list, reviews) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.review_list, null, true)

        val posterView = listViewItem.findViewById<TextView>(R.id.authorName)
        val commentView = listViewItem.findViewById<TextView>(R.id.commentText)
        val postedView = listViewItem.findViewById<TextView>(R.id.timestampText)

        val review = reviews[position]
        posterView.text = review.reviewer
        commentView.text = review.comments
        postedView.text = review.posted

        return listViewItem
    }
}