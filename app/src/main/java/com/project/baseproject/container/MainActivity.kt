package com.project.baseproject.container

import android.os.Bundle
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.project.baseproject.R
import com.project.baseproject.databinding.ActivityMainBinding
import com.project.baseproject.navigation.AppNavigation
import com.project.core.base.activity.BaseActivityNotRequireViewModel
import com.project.core.base.dialog.ConfirmDialogListener
import com.project.core.network.connectivity.NetworkConnectionManager
import com.project.core.pref.RxPreferences
import com.project.core.utils.isTouched
import com.project.core.utils.setLanguage
import com.project.core.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivityNotRequireViewModel<ActivityMainBinding>(), ConfirmDialogListener {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    @Inject
    lateinit var rxPreferences: RxPreferences
    override val layoutId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        appNavigation.bind(navHostFragment.navController)

        lifecycleScope.launch {
            val language = rxPreferences.getLanguage().first()
            language?.let { setLanguage(it) }
        }

        networkConnectionManager.isNetworkConnectedFlow.onEach {
            if (it) {
                Timber.tag("VietBH").d("onCreate: Network connected")
            } else {
                Timber.tag("VietBH").d("onCreate: Network disconnected")
            }
        }.launchIn(lifecycleScope)

    }

    override fun shouldHideKeyboard(event: MotionEvent): Boolean {
        val inputMessageLayout =
            findViewById<ConstraintLayout>(com.rikkeisoft.awesome.conversation.R.id.layout_input)

        if (inputMessageLayout != null && inputMessageLayout.isTouched(event)) {
            return false
        }
        return super.shouldHideKeyboard(event)
    }

    override fun onStart() {
        super.onStart()
        networkConnectionManager.startListenNetworkState()
    }

    override fun onStop() {
        Timber.tag("ahihi").d("onStop")
        super.onStop()
        networkConnectionManager.stopListenNetworkState()
    }

    override fun onClickOk(type: Int?) {
        "Ok Activity".toast(this)
    }

    override fun onClickCancel(type: Int?) {
        "Cancel Activity".toast(this)
    }

    override fun onDestroy() {
        Timber.tag("ahihi").d("onDestroy")
        super.onDestroy()
    }


}