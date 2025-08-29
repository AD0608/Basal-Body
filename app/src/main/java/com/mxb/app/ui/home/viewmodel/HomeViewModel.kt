package com.mxb.app.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.mxb.app.base.BaseViewModel
import com.mxb.app.model.Resource
import com.mxb.app.ui.home.repository.HomeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private var homeRepository: HomeRepository,
) : BaseViewModel() {

    //-------Init Api-------//
    /**Always set Initial state of flow is Show loading [false]*/
    private val _callInitApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callInitApiStateFlow: StateFlow<Resource<*>> = _callInitApiMutableStateFlow
    fun callInitApi() {
        viewModelScope.launch {
            homeRepository.callInitApi().collect {
                _callInitApiMutableStateFlow.value = it
            }
        }
    }

}