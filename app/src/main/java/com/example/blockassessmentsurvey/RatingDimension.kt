package com.example.blockassessmentsurvey

data class RatingDimension(
        val heading: String,
        val desc: String,
        val id: String,
        val questionType: QuestionType,
        val choices: List<String>? = null
)