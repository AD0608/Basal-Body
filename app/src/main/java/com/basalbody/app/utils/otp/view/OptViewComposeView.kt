package com.basalbody.app.utils.otp.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.basalbody.app.utils.otp.compose.OtpView
import com.basalbody.app.utils.otp.style.Defaults

/**
 *Created by Paras Chauhan on 29,November,2023
 *Excellent WebWorld
 *paras.chauhan.eww@gmail.com
 */

class OptViewComposeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    private val textState = mutableStateOf<String?>("")
    private val fontSizeState = mutableStateOf<Float?>(Defaults.textStyle.fontSize.value)
    private val textColorState = mutableStateOf<Color?>(Defaults.textStyle.color)
    private val fontFamilyState = mutableStateOf(Defaults.textStyle.fontFamily)
    private val fontWeightState = mutableStateOf(Defaults.textStyle.fontWeight)
    private val activeColorState = mutableStateOf<Color?>(Defaults.activeBackground)
    private val passiveColorState = mutableStateOf<Color?>(Defaults.passiveBackground)
    private val digitsState = mutableStateOf(Defaults.digits)
    private val errorEnabledState = mutableStateOf(false)
    private val autoFocusEnabledState = mutableStateOf(true)
    private val passwordState = mutableStateOf(false)
    private val symbolState = mutableStateOf('-')
    private val keyboardTypeState =
        mutableStateOf(KeyboardOptions(keyboardType = KeyboardType.Number))

    var text: String?
        get() = textState.value
        set(value) {
            textState.value = value
        }
    var fontSize: Float?
        get() = fontSizeState.value
        set(value) {
            fontSizeState.value = value
        }

    var textColor: Color?
        get() = textColorState.value
        set(value) {
            textColorState.value = value
        }

    var fontFamily: FontFamily?
        get() = fontFamilyState.value
        set(value) {
            fontFamilyState.value = value
        }

    var fontWeight: FontWeight?
        get() = fontWeightState.value
        set(value) {
            fontWeightState.value = value
        }

    var activeColor: Color?
        get() = activeColorState.value
        set(value) {
            activeColorState.value = value
        }

    var passiveColor: Color?
        get() = passiveColorState.value
        set(value) {
            passiveColorState.value = value
        }

    var digits: Int
        get() = digitsState.value
        set(value) {
            digitsState.value = value
        }

    var errorEnabled: Boolean
        get() = errorEnabledState.value
        set(value) {
            errorEnabledState.value = value
        }

    var autoFocusEnabled: Boolean
        get() = autoFocusEnabledState.value
        set(value) {
            autoFocusEnabledState.value = value
        }

    var password: Boolean
        get() = passwordState.value
        set(value) {
            passwordState.value = value
        }

    var symbol: Char
        get() = symbolState.value
        set(value) {
            symbolState.value = value
        }

    var keyboardType: KeyboardOptions
        get() = keyboardTypeState.value
        set(value) {
            keyboardTypeState.value = value
        }


    private var textChangeListener: OtpView.ChangeListener? = null
    fun setTextChangeListener(dataSelectListener: OtpView.ChangeListener?) {
        this.textChangeListener = dataSelectListener
    }

    @Suppress("RedundantVisibilityModifier")
    protected override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    @Composable
    override fun Content() {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        Box {
            var otpText by remember { mutableStateOf("") }

            OtpView(
                value = textState.value.orEmpty(),
                digits = digitsState.value,
                password = passwordState.value,
                symbol = symbolState.value,
                errorEnabled = errorEnabledState.value,
                autoFocusEnabled = autoFocusEnabledState.value,
                textColor = textColorState.value,
                fontSize = (fontSizeState.value ?: Defaults.textStyle.fontSize.value).toInt().sp,
                fontWeight = fontWeightState.value,
                fontFamily = fontFamilyState.value,
                activeColor = activeColorState.value,
                passiveColor = passiveColorState.value,
                keyboardOptions = keyboardTypeState.value,
                onTextChange = { value, completed ->
                    textState.value = value

                    textChangeListener?.onTextChange(value, completed)
                },
            )
        }

    }
}