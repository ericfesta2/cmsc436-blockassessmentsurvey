package com.example.blockassessmentsurvey

// The Review object, representing one full user review, is pushed to Firebase
data class Review(
    val reviewer: String = "",
    val reviews: Map<String, Float> = mapOf(),
    val posted: String = "",
    val comments: String = ""
)