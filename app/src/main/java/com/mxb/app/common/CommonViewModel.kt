package com.mxb.app.common

import android.content.Context
import com.mxb.app.base.BaseViewModel
import com.mxb.app.network.ApiService
import javax.inject.Inject

class CommonViewModel @Inject constructor(
    private var mContext: Context,
    private var apiService: ApiService,
) : BaseViewModel()