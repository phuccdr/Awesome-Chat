package com.project.baseproject.ui.splash

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.core.base.BaseViewModel
import com.project.core.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fireAuth: FirebaseAuth
) : BaseViewModel() {
    val actionSPlash = SingleLiveEvent<SplashActionState>()

    init {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var action: SplashActionState = SplashActionState.NavToLoginScreen
            if (fireAuth.currentUser != null) {
                action = SplashActionState.NavToHomeScreen
            }
            val endTime = System.currentTimeMillis()
            delay(maxOf(0, 1000 - endTime + startTime))
            actionSPlash.value = action
        }
    }

    sealed class SplashActionState {
        data object NavToLoginScreen : SplashActionState()
        data object NavToHomeScreen : SplashActionState()
    }
}
