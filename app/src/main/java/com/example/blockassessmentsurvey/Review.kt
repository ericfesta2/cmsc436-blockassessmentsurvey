package com.example.blockassessmentsurvey

// The Review object, representing one full user review, is pushed to Firebase
data class Review(
    val reviewer: String,
    val safety: Float,
    val air: Float,
    val cleanliness: Float,
    val parking: Float,
    val posted: String,
    val comments: String
)