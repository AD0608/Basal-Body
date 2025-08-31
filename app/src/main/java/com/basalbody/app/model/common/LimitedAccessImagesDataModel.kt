package com.basalbody.app.model.common

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LimitedAccessImagesDataModel(
    val uri: Uri,
    var isSelected: Boolean = false
) : Parcelable