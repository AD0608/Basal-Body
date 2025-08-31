package com.basalbody.app.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.basalbody.app.R
import javax.inject.Inject
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseDialogFragment<V : BaseViewModel, B : ViewBinding>(
    private val inflate: Inflate<B>,
    private val isCancelAble: Boolean = true
) : DialogFragment() {

    protected abstract val modelClass: KClass<V>
    private var _binding: B? = null
    protected val binding get() = _binding!!


    @Inject
    lateinit var viewModel: V

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        dialog?.let { nonNullDialog ->
            nonNullDialog.window?.let { nonNullWindow ->
                nonNullWindow.requestFeature(Window.FEATURE_NO_TITLE)
                val wmlp = nonNullWindow.attributes
                wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT
                wmlp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                nonNullWindow.attributes = wmlp
                /*Add For Cancelable false*/
                nonNullDialog.setCancelable(isCancelAble)
                nonNullDialog.setCanceledOnTouchOutside(isCancelAble)
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
        addObserver()
    }

    open fun addObserver() = Unit
    open fun removeObserver() = Unit

    abstract fun initControl()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        removeObserver()
        _binding = null
    }

    fun fireLogEvent(eventName: String) {
        FirebaseAnalytics.getInstance(requireActivity()).logEvent(eventName, null)
    }
}