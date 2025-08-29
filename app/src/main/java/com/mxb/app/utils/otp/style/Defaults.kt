package com.mxb.app.utils.otp.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

object Defaults {
    val activeBackground: Color = Color.White
    val passiveBackground: Color = Color(0xFFD0DAE2)
    val errorColor: Color = Color.Red
    val textStyle: TextStyle = TextStyle(color = Color(0xFF010D15), fontSize = 14.sp)
    val digits: Int = 6
}

inline fun Color?.takeOrElse(block: () -> Color): Color = this ?: block()