package com.example.blockassessmentsurvey

enum class QuestionType {
    RATING_BAR
}

val ratingDimensions = listOf(
        RatingDimension(
                "Safety",
                "I feel safe from crime and other hazards in my neighborhood.",
                "d_safety",
                QuestionType.RATING_BAR
        ),
        RatingDimension(
                "Air Quality",
                "How clean is the air near your block?",
                "d_air_quality",
                QuestionType.RATING_BAR
        ),
        RatingDimension(
                "Cleanliness",
                "How clean is the environment around you (e.g. absence of litter)?",
                "d_cleanliness",
                QuestionType.RATING_BAR
        ),
        RatingDimension(
                "Parking Spaces",
                "How accessible is parking in your neighborhood?",
                "d_parking_spaces",
                QuestionType.RATING_BAR
        )
)