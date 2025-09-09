package com.basalbody.app.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.R
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.ui.common.CommonViewModel
import com.basalbody.app.databinding.BottomSheetDialogLimitedAccessImagesPickerBinding
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.visibleIfOrGone
import com.basalbody.app.model.common.LimitedAccessImagesDataModel
import kotlin.reflect.KClass

class LimitedAccessImagesPickerBottomSheetDialog :
    BaseBottomSheetDialogFragment<CommonViewModel, BottomSheetDialogLimitedAccessImagesPickerBinding>(
        BottomSheetDialogLimitedAccessImagesPickerBinding::inflate,
        isCancel,
        isPreventBackButton,
        isDraggable
    ) {
    override val modelClass: KClass<CommonViewModel>
        get() = CommonViewModel::class

    private var contentResolver: ContentResolver? = null
    var maxPickItem: Int = Constants.DEFAULT_ONE
    var onImageSelection: ((ArrayList<Uri>) -> Unit)? = null

    //-------Images Uri List-------//
    private var limitedAccessImagesUriList: ArrayList<LimitedAccessImagesDataModel> = arrayListOf()
    private val userSelectedImagesAdapter: LimitedAccessImagesAdapter by lazy {
        LimitedAccessImagesAdapter(
            limitedAccessImagesUriList,
            maxPickItem = maxPickItem,
            ::onClickCategoryItem
        )
    }

    private fun onClickCategoryItem(position: Int, dataBean: LimitedAccessImagesDataModel) {
        if (maxPickItem == Constants.DEFAULT_ONE) {
            onImageSelection?.invoke(arrayListOf(dataBean.uri))
            dismiss()
        } else {
            val numberOfSelectedImages = limitedAccessImagesUriList.count { it.isSelected }
            val canSelectMoreImages = numberOfSelectedImages < maxPickItem

            if (dataBean.isSelected || canSelectMoreImages) {
                dataBean.isSelected = !dataBean.isSelected
                userSelectedImagesAdapter.notifyItemChanged(position)
                binding.linearLayoutAdd.visibleIfOrGone(limitedAccessImagesUriList.any { it.isSelected })
            } else {
                showSnackBar(
                    message = resources.getString(R.string.message_select_up_to_3_items),
                    isStatus = Constants.DEFAULT_ONE,
                    context = requireActivity()
                )
            }
        }
    }

    override fun initControls() {
        //-------Add layout should be gone by default-------//
        binding.linearLayoutAdd.gone()
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogThemePicker)
//        setDialogMargin()
        binding.recyclerViewLimitedAccessImages.addItemDecoration(
            ItemDecorationAlbumColumns(
                resources.getDimensionPixelSize(R.dimen.dp_5),
                3
            )
        ) //set item spacing
        limitedAccessImagesUriList = getUserSelectedImages()
        binding.recyclerViewLimitedAccessImages.adapter = userSelectedImagesAdapter
    }

    override fun setOnClickListener() {
        binding.textViewAdd onSafeClick {
            val selectedUris = limitedAccessImagesUriList
                .filter { it.isSelected }
                .map { it.uri }
                .toCollection(ArrayList())

            onImageSelection?.invoke(selectedUris)
            dismiss()
        }
    }

    private fun getUserSelectedImages(): ArrayList<LimitedAccessImagesDataModel> {
        val limitedAccessImagesUriList = ArrayList<LimitedAccessImagesDataModel>()

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor: Cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                limitedAccessImagesUriList.add(LimitedAccessImagesDataModel(uri = contentUri))
            }
        }
        return limitedAccessImagesUriList
    }

    companion object {
        var isCancel = false
        var isDraggable = false
        var isPreventBackButton = false
        fun newInstance(
            activity: FragmentActivity,
            rootView: ViewGroup,
            isCancel: Boolean = true,
            isDraggable: Boolean = true,
            isPreventBackButton: Boolean = false,
            maxPickItem: Int = Constants.DEFAULT_ONE,
            contentResolver: ContentResolver,
        ) = LimitedAccessImagesPickerBottomSheetDialog().apply {
            this.mActivity = activity
            this.rootView = rootView
            this.isPreventBackButton = isPreventBackButton
            this.isCancel = isCancel
            this.isDraggable = isDraggable
            this.maxPickItem = maxPickItem
            this.contentResolver = contentResolver
        }
    }
}