package com.basalbody.app.ui.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.model.Resource
import com.basalbody.app.ui.home.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private var homeRepository: HomeRepository,
) : BaseViewModel() {

    //-------Logout Api-------//
    /**Always set Initial state of flow is Show loading [false]*/
    private val _callLogoutApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callLogoutApiStateFlow: StateFlow<Resource<*>> = _callLogoutApiMutableStateFlow
    fun callLogoutApi() {
        viewModelScope.launch {
            homeRepository.callLogoutApi().collect {
                _callLogoutApiMutableStateFlow.value = it
            }
        }
    }

    //-------Get User Profile Api-------//
    private val _callGetUserProfileApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callGetUserProfileApiStateFlow: StateFlow<Resource<*>> =
        _callGetUserProfileApiMutableStateFlow

    fun callGetUserProfileApi() {
        viewModelScope.launch {
            homeRepository.callGetUserProfileApi().collect {
                _callGetUserProfileApiMutableStateFlow.value = it
            }
        }
    }

}