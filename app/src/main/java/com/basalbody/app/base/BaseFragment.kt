package com.basalbody.app.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.basalbody.app.datastore.LocalDataRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.utils.ActivityLauncher
import com.basalbody.app.utils.ActivityLauncher.registerActivityForResult
import com.basalbody.app.extensions.hideLoader
import com.basalbody.app.extensions.nullSafe
import com.basalbody.app.extensions.showLoader
import com.basalbody.app.utils.CommonUtils.showOkDialog
import javax.inject.Inject

abstract class BaseFragment<V : BaseViewModel, B : ViewBinding>(private val inflate: Inflate<B>) :
    Fragment(), View.OnClickListener {

    @Inject
    lateinit var viewModel: V

    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected abstract fun getViewBinding(): B

    protected fun isInternetAvailable(): Boolean {
        var available = false
        ifBaseActivity {
            available = it.isInternetAvailable
        }
        return available
    }

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    open fun onNetworkChange(isNetworkAvailable: Boolean) {}
    open fun onSocketConnectionChange(isConnected: Boolean) {}
    open fun onGoingApiCall(isApiCall: Boolean) {}


    protected lateinit var activityLauncher: ActivityLauncher<Intent, ActivityResult>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLauncher = registerActivityForResult(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
//        createViewModel()
        initSetup()
        addObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
        removeObserver()
    }

    open fun addObserver() = Unit

    open fun removeObserver() = Unit

    override fun onClick(view: View?) = Unit

    protected abstract fun initSetup()
    protected abstract fun listeners()
    /*private fun createViewModel() {
        this.viewModel = getViewModel(clazz = modelClass)
    }*/

    open fun onChangeFilter(isFilterChange: Boolean) {}

    fun showGuestDialog(afterLogin: (() -> Unit)? = null) {
        /* GuestUserDialog.newInstance().apply {
             onLoginRegister = {
 //                startSignInActivityForResult(afterLogin)
                 afterLogin!!.invoke()
             }
         }.show(
             requireActivity().supportFragmentManager,
             GuestUserDialog::class.java.simpleName
         )*/
    }

    open fun onBackPressed(): Boolean {
        return true
    }

    private fun ifBaseActivity(callback: (BaseActivity<*, *>) -> Unit) {
        activity?.let {
            if (it is BaseActivity<*, *>) {
                callback.invoke(it)
            }
        }
    }

    protected fun checkLogin(
        doLogin: (() -> Unit)? = null,
        onLoggedIn: (() -> Unit)? = null
    ) {
        /* if (localDataRepository.isLoggedIn()) {
             onLoggedIn?.invoke()
         } else {
             doLogin?.invoke()
         }*/
    }

    fun manageLoader(type: Resource.Loading<*>?) {
        if (type?.isLoadingShow == true) {
            requireContext().showLoader()
        } else {
            requireContext().hideLoader()
        }
    }

    fun showApiErrorMessage(type: Resource.Error<*>) {
        showAlert(type.message.toString())
    }

    fun clearDataOnLogoutAndNavigateToLoginScreen() {
        localDataRepository.resetUserData()
        /*requireActivity().startNewActivity(
            RoleSelectionActivity::class.java,
            isClearAllStacks = true
        )*/
    }

    fun showApiErrorMessage(message: String?) {
        showAlert(message.nullSafe())
    }

    fun showAlert(message: String) {
        ifBaseActivity {
            it.showApiErrorMessage(message = message)
        }
    }

    fun showNoInternetError(type: Resource.NoInternetError<*>) {
        showOkDialog(
            requireContext(),
            message = type.message ?: "No Internet Connection"
        )
    }

    fun changeFragment(hideFragment: Fragment, showFragment: Fragment) {
        childFragmentManager.beginTransaction()
            .hide(hideFragment)
            .show(showFragment)
            .commit()
    }

    fun fireLogEvent(eventName: String) {
        FirebaseAnalytics.getInstance(requireActivity()).logEvent(eventName, null)
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
}