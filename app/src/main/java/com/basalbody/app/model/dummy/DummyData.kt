package com.basalbody.app.model.dummy

import com.basalbody.app.R

object DummyData {

    data class IntroData(
        val imageRes: Int,
        val title: String,
        val description: String
    )

    fun getIntroData(): ArrayList<IntroData> {
        return arrayListOf<IntroData>().apply {
            add(
                IntroData(
                    R.drawable.intro_1,
                    "Welcome to\nBasal Body",
                    "Discover a new connection with\nyour body through daily\ntemperature tracking."
                )
            )
            add(
                IntroData(
                    R.drawable.intro_2,
                    "Track Effortlessly,\nLearn Deeply",
                    "Record your BBT each morning and\nwe'll turn your data into clear health\ninsights."
                )
            )
            add(
                IntroData(
                    R.drawable.intro_3,
                    "Personalized Cycle\nInsights",
                    "Get forecasts, fertility\npredictions, and health patterns\ntailored to you."
                )
            )
        }
    }

}