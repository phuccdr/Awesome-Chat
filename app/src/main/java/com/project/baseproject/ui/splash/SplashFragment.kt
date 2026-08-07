package com.project.baseproject.ui.splash

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import androidx.fragment.app.viewModels
import com.project.baseproject.R
import com.project.baseproject.databinding.FragmentSplashBinding
import com.project.baseproject.navigation.AppNavigation
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.textspan.CustomTypefaceSpan
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
        viewModel.actionSPlash.observe(viewLifecycleOwner) { action ->
//            appNavigation.openSplashToHomeScreen()
            when (action) {
                is SplashViewModel.SplashActionState.NavToHomeScreen -> {
                    appNavigation.openSplashToHomeScreen()
                }

                is SplashViewModel.SplashActionState.NavToLoginScreen -> {
                    appNavigation.openSplashToLoginScreen()
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val spannable = SpannableString(ResourceUtils.getString(R.string.app_name))
        val black = ResourceUtils.getFont(com.project.core.R.font.exo_black)
        val regular = ResourceUtils.getFont(com.project.core.R.font.exo_light)
        if (black != null && regular != null) {
            spannable.setSpan(
                CustomTypefaceSpan(black), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(regular), 8, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvAppName.text = spannable
    }
}