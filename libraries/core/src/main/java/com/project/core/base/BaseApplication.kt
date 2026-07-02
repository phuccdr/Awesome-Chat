package com.project.core.base

import android.app.Application
import com.project.core.BuildConfig
import com.project.core.utils.resource.ResourceUtils
import timber.log.Timber

open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        ResourceUtils.init(this)
    }

}