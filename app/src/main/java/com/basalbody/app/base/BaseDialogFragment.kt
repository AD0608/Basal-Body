package com.basalbody.app.base

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.basalbody.app.R
import com.basalbody.app.extensions.blurRadius
import com.basalbody.app.extensions.gone
import com.basalbody.app.utils.blur.BlurView
import javax.inject.Inject
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseDialogFragment<B : ViewBinding>(
    private val inflate: Inflate<B>,
    private val isCancelAble: Boolean = true
) : DialogFragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    protected lateinit var mActivity: FragmentActivity
    protected lateinit var rootView: ViewGroup
    private var frameLayout: BlurView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        dialog?.let { nonNullDialog ->
            nonNullDialog.window?.let { nonNullWindow ->
                nonNullWindow.requestFeature(Window.FEATURE_NO_TITLE)
                /*EO Add For Cancelable false*/
                nonNullWindow.setBackgroundDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    ).toDrawable()
                )
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()
        setDialogMargin()
        addObserver()

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
    }

    open fun addObserver() = Unit
    open fun removeObserver() = Unit

    abstract fun initControl()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        removeObserver()
        frameLayout?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            it.gone()
        }
        _binding = null
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
        binding.root.layoutParams = layoutParam

        requireActivity().addContentView(frameLayout, blurViewLayoutParams)
    }

    fun fireLogEvent(eventName: String) {
        FirebaseAnalytics.getInstance(requireActivity()).logEvent(eventName, null)
    }
}