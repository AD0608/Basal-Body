package com.basalbody.app.utils.otp.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

object Defaults {
    val activeBackground: Color = Color.White
    val passiveBackground: Color = Color.White
    val errorColor: Color = Color.Red
    val textStyle: TextStyle = TextStyle(color = Color(0xFF000000), fontSize = 14.sp)
    val digits: Int = 6
}

inline fun Color?.takeOrElse(block: () -> Color): Color = this ?: block()