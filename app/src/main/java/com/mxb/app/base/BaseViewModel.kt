package com.mxb.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mxb.app.utils.ValidationStatus
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sessionGson: Gson

    private val _validateState = MutableLiveData(ValidationStatus.UNKNOWN)
    val validationState: LiveData<ValidationStatus> = _validateState


    fun setValidationValue(validationStatus: ValidationStatus) {
        _validateState.value = validationStatus
    }


}
