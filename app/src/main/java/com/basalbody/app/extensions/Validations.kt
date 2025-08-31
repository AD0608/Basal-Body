package com.basalbody.app.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.basalbody.app.utils.Constants.EMAIL_PATTERN
import com.basalbody.app.utils.Constants.PASSWORD_LIMIT
import com.basalbody.app.utils.Constants.REGEX_VALID_PASSWORD
import java.util.regex.Matcher
import java.util.regex.Pattern

/** Validate Sting us empty or "null"*/
fun String.isValidateString(): Boolean {
    return this.isNotEmpty() && this != "null"
}

fun String.emailValid(): Boolean {
    val pattern = Pattern.compile(EMAIL_PATTERN)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

/**Validate password*/
fun String.isValidPassword(): Boolean {
    val pattern: Pattern
    val passwordRegex = REGEX_VALID_PASSWORD
    pattern = Pattern.compile(passwordRegex)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

/**Validate phone number*/
fun String.isPhoneNumberValidate(): Boolean {
    return if (this.length == 15) {
        this.toString() == "000000000000000"
    } else this.length in 6..16
}

/**Validate password is validate*/
fun String.isPasswordValid(): Boolean {
    return this.length < PASSWORD_LIMIT
}

/**Validate password has white space*/
fun String.isPasswordHasWhiteSpace(): Boolean {
    return this.startsWith(" ") || this.endsWith(" ")
}

/** Remove White space from [edittext]*/
fun removeWhiteSpace(context: Context, editText: EditText) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (s.toString().startsWith(" ")) {
                editText.setText(s.toString().trim { it <= ' ' })
                editText.setSelection(editText.text.length)
            }
        }
    })

    editText.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            if (editText.text?.length!! >= 0) {
                var text = editText.text.toString()
                var isSpace = text.endsWith(" ")
                if (isSpace) {
//                    showSnackBar("Please remove white space end of the password.",
//                        CommonUtils.STATUS_FALSE,context)
                }
            }
        }
    }
}

/** Validate String has Only Upper case and lower case*/
fun String.isUpperAndLowerCase(): Boolean {
    val regex = Regex("^(?=.*[a-z])(?=.*[A-Z]).+\$")
    return regex.matches(this)
}

/** Validate String has only number*/
fun String.isNumber(): Boolean {
    val regex = Regex("^(?=.*[0-9]).+\$")
    return regex.matches(this)
}

/** Validate String has only special character*/
fun String.isSpecialCharacter(): Boolean {
    val regex = Regex("^(?=.*[\$&+,:;=\\\\?@#_|/'<>.^*()%!-]).+\$")
    return regex.matches(this)
}

/** Validate String has first character Upper case or not*/
fun String.isFirstLetterIsUpperCase(): Boolean {
    return this.first().isUpperCase()
}