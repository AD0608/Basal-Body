package com.basalbody.app.utils.otp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.basalbody.app.utils.otp.style.ColorStyle
import com.basalbody.app.utils.otp.style.Defaults
import kotlinx.coroutines.delay

/**
 *Created by Paras Chauhan on 29,November,2023
 *Excellent WebWorld
 *paras.chauhan.eww@gmail.com
 */
@Composable
fun InnerOtpView(
    modifier: Modifier = Modifier,
    value: String,
    digits: Int = 6,
    password: Boolean,
    symbol: Char = '*',
    enabled: Boolean = true,
    errorEnabled: Boolean = false,
    autoFocusEnabled: Boolean = false,
    colorStyle: ColorStyle = ColorStyle.Default,
    textStyle: TextStyle = Defaults.textStyle,
    keyboardOptions: KeyboardOptions,
    onFocusChanged: (Boolean) -> Unit = {},
    onTextChange: (String, Boolean) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val boxSpacing = 10.dp
    val boxWidth = (screenWidth - (boxSpacing * (digits - 1))) / digits

    BasicTextField(
        modifier = modifier
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChanged(it.isFocused)
            }
            .focusRequester(focusRequester),
        value = value,
        singleLine = true,
        onValueChange = {
            if (it.length <= digits) {
                if ((keyboardOptions.keyboardType == KeyboardType.Number && it.isDigitsOnly()) ||
                    keyboardOptions.keyboardType == KeyboardType.Text
                ) {
                    onTextChange.invoke(it, it.length == digits)
                    onFocusChanged(true)
                }
            }
        },
        cursorBrush = SolidColor(textStyle.color),
        enabled = enabled,
        textStyle = TextStyle(textAlign = TextAlign.Center),
        keyboardOptions = keyboardOptions,
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(boxSpacing)
            ) {
                val textLength = value.length
                repeat(digits) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)         // equal width for each digit
                            .aspectRatio(1f)    // make it a square
                            .background(
                                if (index < value.length) colorStyle.active
                                else if (index == value.length && isFocused) colorStyle.active
                                else colorStyle.passive,
                                shape = CircleShape
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (errorEnabled) colorStyle.error else Color(0xff716569),
                                shape = CircleShape
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (index < textLength) {
                                if (password) symbol.toString() else value[index].toString()
                            } else "",
                            style = textStyle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                        if (index == textLength && isFocused) {
                            var alpha by remember { mutableStateOf(1f) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(750L)
                                    alpha = 1f - alpha
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(textStyle.fontSize.value.dp * 1.2f) // proportional to text size
                                    .alpha(alpha)
                                    .background(textStyle.color.copy(alpha = 0.6f))
                            )
                        }
                    }
                }
            }
        },
    )
    LaunchedEffect(autoFocusEnabled) {
        if (autoFocusEnabled) focusRequester.requestFocus()
    }
}
