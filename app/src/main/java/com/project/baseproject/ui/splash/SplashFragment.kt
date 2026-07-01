package com.project.baseproject.ui.splash

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.project.baseproject.R
import com.project.baseproject.databinding.FragmentSplashBinding
import com.project.baseproject.navigation.AppNavigation
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.TextSpanUtil
import com.project.core.utils.setTextCompute
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(R.layout.fragment_splash) {

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: SplashViewModel by viewModels()

    override fun getVM() = viewModel

    override fun bindingAction() {
        super.bindingAction()
        Timber.tag("ahiuhi").d("sdkjfg")
        viewModel.actionSPlash.observe(viewLifecycleOwner) {
            appNavigation.openSplashToHomeScreen()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
//        binding.tvAppName = TextSpanUtil.build("")
    }
}