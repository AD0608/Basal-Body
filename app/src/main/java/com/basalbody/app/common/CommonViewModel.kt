package com.basalbody.app.common

import android.content.Context
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.network.ApiService
import javax.inject.Inject

class CommonViewModel @Inject constructor(
    private var mContext: Context,
    private var apiService: ApiService,
) : BaseViewModel()