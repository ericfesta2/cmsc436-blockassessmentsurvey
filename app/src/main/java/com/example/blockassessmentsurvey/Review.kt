package com.example.blockassessmentsurvey

data class Review(
    val reviewer: String,
    val safety: Float,
    val air: Float,
    val cleanliness: Float,
    val parking: Float,
    val posted: String,
    val comments: String
)