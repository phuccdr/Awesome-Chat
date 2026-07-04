package com.project.baseproject.navigation

import android.os.Bundle
import com.project.baseproject.R
import com.project.core.navigationComponent.BaseNavigatorImpl
import com.project.setting.DemoNavigation
import com.rikkeisoft.awesome.AuthNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation, DemoNavigation,
    AuthNavigation {
    override fun openSplashToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_homeFragment, bundle)
    }

    override fun openSplashToLoginScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_loginFragment)
    }

    override fun openDemoViewPager(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_demoViewPager, bundle)
    }

    override fun openLoginToHomeScreen(bundle: Bundle?) {
        openScreen(R.id.action_loginFragment_to_homeFragment, bundle)
    }

    override fun openLoginToRegisterScreen(bundle: Bundle?) {
        openScreen(R.id.action_loginFragment_to_registerFragment, bundle)
    }

    override fun openRegisterToLogin(bundle: Bundle?) {
        navigateUp()
    }

}