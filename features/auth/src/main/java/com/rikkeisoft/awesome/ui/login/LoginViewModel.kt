package com.rikkeisoft.awesome.ui.login

import androidx.lifecycle.viewModelScope
import com.project.core.base.BaseViewModel
import com.project.core.utils.SingleLiveEvent
import com.project.core.utils.StringUtils.isEmailValid
import com.project.core.utils.StringUtils.validatepassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    val actionLogin = SingleLiveEvent<LoginActionState>()
    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    val enableLogin: StateFlow<Boolean> = combine(email, password) { email, password ->
        email.isNotBlank() && password.isNotBlank()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onLogin() {
        isLoading.value = true
        if (_email.value.isEmailValid() && _password.value.validatepassword() == 3) {
            // xly login
        } else {
            messageError.value = ""
        }
    }

    fun onRegister(){
        actionLogin.value = LoginActionState.NavToRegisterScreen
    }

    sealed class LoginActionState {
        data object NavToHomeScreen : LoginActionState()
        data object NavToRegisterScreen : LoginActionState()
    }


}