package com.project.core.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.project.core.base.activity.BaseActivityNotRequireViewModel
import com.project.core.utils.toast
import com.project.permission.PermissionListener
import com.project.permission.PermissionStatus
import timber.log.Timber

abstract class BaseFragmentNotRequireViewModel<BD : ViewDataBinding>(@LayoutRes id: Int) :
    Fragment(id), PermissionListener {

    private var _binding: BD? = null
    protected val binding: BD
        get() = _binding
            ?: throw IllegalStateException("Cannot access view after view destroyed or before view creation")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated")
        _binding = DataBindingUtil.bind(view)
        _binding?.lifecycleOwner = viewLifecycleOwner

        if (savedInstanceState == null) {
            onInit()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            onInit(savedInstanceState)
        }
    }

    private fun onInit(savedInstanceState: Bundle? = null) {

        initView(savedInstanceState)

        //Xử lý click
        setOnClick()
        //Lắng nghe và update UIState từ viewModel lên UI
        bindingStateView()
        //Lắng nghe các event từ viewModel
        bindingAction()
    }

    open fun setOnClick() {
        //Xử lý click
    }

    open fun initView(savedInstanceState: Bundle?) {
        // setup view
    }

    open fun bindingStateView() {
        //Lắng nghe và update UIState từ viewModel lên UI
    }

    open fun bindingAction() {
        //Lắng nghe các event từ viewModel
    }

    override fun onDestroyView() {
        logLifecycle("onDestroyView")
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }

    override fun onPermissionGranted(requestCode: Int?) {
        "onPermissionGranted".toast(requireContext().applicationContext)
    }

    override fun onPermissionDenied(
        requestCode: Int?,
        permissions: List<PermissionStatus>,
        isDoNotAskAgain: Boolean
    ) {
        "onPermissionDenied".toast(requireContext().applicationContext)
    }


    fun showHideLoading(isShow: Boolean) {
        if (activity != null && activity is BaseActivityNotRequireViewModel<*>) {
            if (isShow) {
                (activity as BaseActivityNotRequireViewModel<*>?)!!.showLoading()
            } else {
                (activity as BaseActivityNotRequireViewModel<*>?)!!.hiddenLoading()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logLifecycle("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        logLifecycle("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        logLifecycle("onPause")
        super.onPause()
    }

    override fun onStop() {
        logLifecycle("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        logLifecycle("onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        logLifecycle("onDetach")
        super.onDetach()
    }

    private fun logLifecycle(callback: String) {
        Timber.tag("FragmentLifecycle").d("${this::class.java.simpleName}: $callback")
    }
}



