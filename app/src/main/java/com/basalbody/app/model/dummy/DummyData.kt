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
                    "Welcome to Basal Body",
                    "Discover a new connection with your body through daily temperature tracking."
                )
            )
            add(
                IntroData(
                    R.drawable.intro_2,
                    "Track Effortlessly, Learn Deeply",
                    "Record your BBT each morning and we'll turn your data into clear health insights."
                )
            )
            add(
                IntroData(
                    R.drawable.intro_3,
                    "Personalized Cycle Insights",
                    "Get forecasts, fertility predictions, and health patterns tailored to you."
                )
            )
        }
    }

}