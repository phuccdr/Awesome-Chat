package com.project.baseproject.navigation

import android.os.Bundle
import com.project.core.navigationComponent.BaseNavigator

interface AppNavigation : BaseNavigator {

    fun openSplashToHomeScreen(bundle: Bundle? = null)
}