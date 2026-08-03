package com.rikkeisoft.awesome

import android.os.Bundle

interface AuthNavigation {
    fun openLoginToHomeScreen(bundle: Bundle? = null)

    fun openLoginToRegisterScreen(bundle: Bundle? = null)

    fun openRegisterToLogin(bundle: Bundle? = null)
    fun openRegisterToHome(bundle: Bundle? = null)
}