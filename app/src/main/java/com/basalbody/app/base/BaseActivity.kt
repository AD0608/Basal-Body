package com.basalbody.app.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.basalbody.app.R
import com.basalbody.app.datastore.LocalDataRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.utils.ActivityLauncher
import com.basalbody.app.utils.ActivityLauncher.registerActivityForResult
import com.basalbody.app.extensions.hideLoader
import com.basalbody.app.extensions.showLoader
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.LimitCount
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.Validation
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.language.LocaleHelper
import com.basalbody.app.utils.showSnackBar
import javax.inject.Inject
import androidx.core.graphics.toColorInt
import com.basalbody.app.extensions.changeBackground
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.ui.auth.activity.LoginActivity
import com.basalbody.app.utils.dotsindicator.setBackgroundCompat

abstract class BaseActivity<V : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {
    enum class ScreenType {
        SELECT_PREF,
        LOGIN
    }

    private val TAG_BASE = "BaseActivity() -->  "

    lateinit var activity: Activity
    var sessionExpireCodeList = ArrayList<Int>()

    @Inject
    lateinit var viewModel: V
    lateinit var binding: VB
    protected abstract fun getViewBinding(): VB

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    @Inject
    lateinit var context: Context

    protected abstract fun initSetup()

    protected abstract fun listeners()

    protected lateinit var activityLauncher: ActivityLauncher<Intent, ActivityResult>

    private var mProgressDialog: Dialog? = null

    protected open fun addObservers() {/*Add observer in this method*/
    }

    protected open fun setupBeforeOnCreate() {/*Define setup before onCreate method*/
    }

    protected open fun removeObservers() {/*Remove observer in this method*/
    }

    open fun clearAuthFlowStack(isClearAuthFlow: Boolean) {}
    open fun onPhoneVerify(phone: String) {}

    var isInternetAvailable = false
    var applyInsetsForChat: Boolean = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        //---HERE BELOW DISABLE NIGHTMODE FORCEFULLY---//
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        /* Set application orientation to portrait */
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setupBeforeOnCreate()
        LocaleHelper.updateResources(this, LocaleHelper.loadLocale(this))
        super.onCreate(savedInstanceState)
        activityLauncher = registerActivityForResult(this)
        performDataBinding()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }

    protected fun getCurrentFragment(): BaseFragment<*, *>? {
        val fragment = supportFragmentManager.findFragmentById(0)
        if (fragment != null && fragment is BaseFragment<*, *>) {
            return fragment
        }
        return null
    }

    private fun performDataBinding() {
        activity = this
        binding = getViewBinding()
        setContentView(binding.root)
        binding.root.changeBackground(R.drawable.ic_app_bg)
        setupEdgeToEdge()
        initSetup()
        listeners()
        addObservers()
        addValidationObserver()
    }

    /*private val gradientDrawable = object : Drawable() {
        private val paint = Paint()

        override fun draw(canvas: Canvas) {
            val shader = LinearGradient(
                0f, 0f, 0f, bounds.height().toFloat(),
                intArrayOf(
                    "#96F49C".toColorInt(),
                    "#FFFFFF".toColorInt(),
                    "#FFFFFF".toColorInt(),
                    "#A6E87A".toColorInt()
                ),
                floatArrayOf(0f, 0.33f, 0.66f, 1f), // positions of colors
                Shader.TileMode.CLAMP
            )
            paint.shader = shader
            canvas.drawRect(bounds, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }
        override fun getOpacity(): Int = PixelFormat.OPAQUE
    }*/


    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        if (applyInsetsForChat) {
            Logger.e("AjayEWW-->", "For chat screen")
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                window.isNavigationBarContrastEnforced = false
                v.setPaddingRelative(systemBars.left, systemBars.top, systemBars.right, 0)
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = if (imeVisible) imeHeight else systemBars.bottom
                }

                insets
            }
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            window.isNavigationBarContrastEnforced = false
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPaddingRelative(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }
        }
    }

    private fun addValidationObserver() {
        viewModel.validationState.observe(this) {
            Validation.showMessageDialog(this, it)
        }
    }

    fun setLightStatusBarText(isLightText: Boolean) {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars =
            !isLightText // false = white text, true = black text
        windowInsetsController.isAppearanceLightNavigationBars =
            !isLightText // false = white text, true = black text
    }

    fun manageLoader(type: Resource.Loading<*>?) {
        if (type?.isLoadingShow == true) {
            showLoader()
        } else {
            hideLoader()
        }
    }

    fun clearDataOnLogoutAndNavigateToLoginScreen() {
        localDataRepository.resetUserData()
        startNewActivity(className = LoginActivity::class.java, isClearAllStacks = true)
    }

    fun showApiErrorMessage(message: String?) {
        message?.let { showSnackBar(it, Constants.STATUS_ERROR, activity) }
    }

    fun password(value: String): Boolean {

        if (value.isBlank()) {
            viewModel.setValidationValue(ValidationStatus.EMPTY_PASSWORD)
            return false
        }

        if (value.length < LimitCount.passwordMin) {
            viewModel.setValidationValue(ValidationStatus.PASSWORD_LENGTH)
            return false
        }

        if (value.first() == ' ' || value.last() == ' ') {
            viewModel.setValidationValue(ValidationStatus.PASSWORD_START_END_BLANK_SPACE)
            return false
        }

        return true

    }

    fun newPassword(value: String): Boolean {

        if (value.isBlank()) {
            viewModel.setValidationValue(ValidationStatus.EMPTY_NEW_PASSWORD)
            return false
        }

        if (value.length < LimitCount.passwordMin) {
            viewModel.setValidationValue(ValidationStatus.NEW_PASSWORD_LENGTH)
            return false
        }

        if (value.first() == ' ' || value.last() == ' ') {
            viewModel.setValidationValue(ValidationStatus.NEW_PASSWORD_START_END_BLANK_SPACE)
            return false
        }

        return true

    }

    fun confirmPassword(value: String): Boolean {

        if (value.isBlank()) {
            viewModel.setValidationValue(ValidationStatus.EMPTY_CONFIRM_PASSWORD)
            return false
        }

        if (value.length < LimitCount.passwordMin) {
            viewModel.setValidationValue(ValidationStatus.CONFIRM_PASSWORD_LENGTH)
            return false
        }
        if (value.first() == ' ' || value.last() == ' ') {
            viewModel.setValidationValue(ValidationStatus.CONFIRM_PASSWORD_START_END_BLANK_SPACE)
            return false
        }
        return true
    }

    fun confirmNewPassword(value: String): Boolean {

        if (value.isBlank()) {
            viewModel.setValidationValue(ValidationStatus.EMPTY_CONFIRM_NEW_PASSWORD)
            return false
        }

        if (value.length < LimitCount.passwordMin) {
            viewModel.setValidationValue(ValidationStatus.CONFIRM_NEW_PASSWORD_LENGTH)
            return false
        }

        if (value.first() == ' ' || value.last() == ' ') {
            viewModel.setValidationValue(ValidationStatus.CONFIRM_NEW_PASSWORD_START_END_BLANK_SPACE)
            return false
        }

        return true

    }

    fun enablePaginationForRecyclerView(
        recyclerView: RecyclerView,
        nextPageFunction: () -> Unit
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as? LinearLayoutManager ?: return

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem >= totalItemCount - 1) {
                    nextPageFunction()
                }
            }
        })
    }

    fun enableChatPaginationForRecyclerView(
        recyclerView: RecyclerView,
        nextPageFunction: () -> Unit
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as? LinearLayoutManager ?: return

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleThreshold = 1
                if (firstVisibleItemPosition <= visibleThreshold) {
                    nextPageFunction()
                }
            }
        })
    }
}