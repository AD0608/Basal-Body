package com.basalbody.app.ui.intro.viewmodel

import androidx.lifecycle.viewModelScope
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.model.Resource
import com.basalbody.app.ui.intro.repository.IntroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class IntroViewModel @Inject constructor(
    private var introRepository: IntroRepository,
) : BaseViewModel() {

    //-------Init Api-------//
    /**Always set Initial state of flow is Show loading [false]*/
    private val _callInitApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callInitApiStateFlow: StateFlow<Resource<*>> = _callInitApiMutableStateFlow

    fun callInitApi() {
        viewModelScope.launch {
            introRepository.callInitApi().collect {
                _callInitApiMutableStateFlow.value = it
            }
        }
    }

}