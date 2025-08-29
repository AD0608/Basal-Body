package com.mxb.app.base

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mxb.app.R
import com.mxb.app.utils.blur.BlurView
import com.mxb.app.extensions.blurRadius
import com.mxb.app.extensions.gone
import com.mxb.app.extensions.hideKeyboard
import com.mxb.app.utils.CommonUtils
import javax.inject.Inject
import kotlin.reflect.KClass

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseBottomSheetDialogFragment<V : BaseViewModel, B : ViewBinding>(
    private val inflate: Inflate<B>,
    var isCancel: Boolean,
    var isDraggable: Boolean,
    var isPreventBackButton: Boolean
) : BottomSheetDialogFragment(), View.OnClickListener {

    protected abstract val modelClass: KClass<V>

    @Inject
    lateinit var viewModel: V
    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected lateinit var mActivity: FragmentActivity
    protected lateinit var rootView: ViewGroup
    private var frameLayout: BlurView? = null

    protected abstract fun initControls()
    protected abstract fun setOnClickListener()
    open fun addObserver() = Unit
    protected open fun removeObserver() = Unit
    override fun onClick(view: View?) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.isDraggable = isDraggable
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.setCanceledOnTouchOutside(isCancel)
            dialog.setCancelable(isCancel)
        }

        dialog.setOnShowListener {
            (view?.parent as? ViewGroup)?.background = Color.TRANSPARENT.toDrawable()
            view?.hideKeyboard()
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControls()
        setDialogMargin()
        setOnClickListener()
        addObserver()
        val constraintMain = view.findViewById<View>(R.id.constraintMain)
        val initialBottomPadding = constraintMain?.paddingBottom ?: 0

        dialog?.window?.let {
            it.isNavigationBarContrastEnforced = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                it.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        )
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(constraintMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            val bottomInset = if (imeVisible) imeHeight else systemBars.bottom
            v.updatePadding(bottom = initialBottomPadding + bottomInset)
            insets
        }
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Handle the back button press here
                // Return 'true' to indicate that the event has been consumed
                isPreventBackButton
            } else {
                false
            }
        }
    }

    private fun setDialogMargin() {
        //-------Set background blur-------//
        frameLayout = BlurView(mActivity)
        frameLayout?.setOverlayColor(
            ContextCompat.getColor(
                requireActivity(),
                R.color.blurColor
            )
        )
        if (::rootView.isInitialized) {
            frameLayout?.let { blurRadius(rootView, context = mActivity, it, 8f) }
        }

        val blurViewLayoutParams = FrameLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        val layoutParam = FrameLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParam.rightMargin = CommonUtils.dpToPx(mActivity, 24)
        layoutParam.leftMargin = CommonUtils.dpToPx(mActivity, 24)
        binding.root.layoutParams = layoutParam

        requireActivity().addContentView(frameLayout, blurViewLayoutParams)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        frameLayout?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            it.gone()
        }
        frameLayout = null
        removeObserver()
    }

}