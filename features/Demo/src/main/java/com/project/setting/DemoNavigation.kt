package com.project.setting

import android.os.Bundle
import com.project.core.navigationComponent.BaseNavigator

interface DemoNavigation : BaseNavigator {
    fun openDemoViewPager(bundle: Bundle? = null)
}