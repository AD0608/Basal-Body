package com.basalbody.app.ui.auth.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.model.Resource
import com.basalbody.app.ui.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private var splashRepository: AuthRepository,
) : BaseViewModel() {

    var isSelectEmail : Boolean = false
    var isTermsConditionCheck : Boolean = false
    var countDownTimer: CountDownTimer? = null

    //-------Init Api-------//
    /**Always set Initial state of flow is Show loading [false]*/
    private val _callInitApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callInitApiStateFlow: StateFlow<Resource<*>> = _callInitApiMutableStateFlow

    fun callInitApi() {
        viewModelScope.launch {
            splashRepository.callInitApi().collect {
                _callInitApiMutableStateFlow.value = it
            }
        }
    }

}