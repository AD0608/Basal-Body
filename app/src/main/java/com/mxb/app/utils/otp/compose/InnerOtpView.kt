package com.mxb.app.utils.otp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.mxb.app.utils.otp.style.ColorStyle
import com.mxb.app.utils.otp.style.Defaults
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val boxSize = textStyle.fontSize.value * 2 + 4
    val maxDigit = (screenWidth / boxSize) - 1
    val currentDigitCount = minOf(digits, maxDigit.toInt())

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
            if (it.length <= currentDigitCount) {
                if ((keyboardOptions.keyboardType == KeyboardType.Number && it.isDigitsOnly()) ||
                    keyboardOptions.keyboardType == KeyboardType.Text
                ) {
                    onTextChange.invoke(it, it.length == currentDigitCount)
                    onFocusChanged(true)
                }
            }
        },
        cursorBrush = SolidColor(textStyle.color),
        enabled = enabled,
        textStyle = TextStyle(textAlign = TextAlign.Center),
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val textLength = value.length
                repeat(currentDigitCount) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .sizeIn(minWidth = boxSize.dp, minHeight = boxSize.dp)
                            .background(
                                if (index < textLength && value[index]
                                        .toString()
                                        .isNotEmpty()
                                ) colorStyle.active else if (index == textLength && isFocused) colorStyle.active else colorStyle.passive,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (errorEnabled) colorStyle.error
                                else (if (index < textLength && value[index]
                                        .toString()
                                        .isNotEmpty()
                                ) textStyle.color else if (index == textLength && isFocused) textStyle.color else colorStyle.passive),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(1.dp)
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (index < textLength) {
                                if (password) {
                                    symbol.toString()
                                } else {
                                    value[index].toString()
                                }
                            } else if (index == textLength && isFocused) "" else "_",
                            modifier = Modifier.align(
                                Alignment.Center
                            ),
                            style = textStyle
                        )
                        if (index == textLength && isFocused) {
                            var alpha by remember { mutableStateOf(1f) }
                            LaunchedEffect(key1 = Unit) {
                                coroutineScope {
                                    launch {
                                        while (true) {
                                            delay(750L)
                                            alpha = 1f - alpha
                                        }
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(((boxSize / 2) - 2).dp)
                                    .alpha(alpha)
                                    .background(textStyle.color.copy(alpha = 0.4f))
                            )
                        }
                    }
                    if (index != currentDigitCount - 1) Spacer(modifier = Modifier.width(10.dp))
                }
            }
        },
    )
    LaunchedEffect(autoFocusEnabled) {
        if (autoFocusEnabled) focusRequester.requestFocus()
    }
}