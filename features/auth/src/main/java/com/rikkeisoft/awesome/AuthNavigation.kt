package com.rikkeisoft.awesome

import android.os.Bundle

interface AuthNavigation {
    fun openLoginToHomeScreen(bundle: Bundle? = null)

    fun openLoginToRegisterScreen(bundle: Bundle? = null)
}