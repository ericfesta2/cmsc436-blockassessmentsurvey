package com.example.blockassessmentsurvey

enum class QuestionType {
    RATING_BAR,
    MULTI_CHOICE
}

val ratingDimensions = listOf(
        RatingDimension(
                "Safety",
                "I feel safe from crime and other hazards in my neighborhood.",
                "d_safety",
                QuestionType.RATING_BAR
        ),
        RatingDimension(
                "Open Space",
                "I have convenient access to a recreational area or park.",
                "d_open_space",
                QuestionType.RATING_BAR
                /*QuestionType.MULTI_CHOICE,
                listOf(
                        "Short walk",
                        "Long walk/run",
                        "Short car/bike trip",
                        "Long car/bus ride"
                )*/
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