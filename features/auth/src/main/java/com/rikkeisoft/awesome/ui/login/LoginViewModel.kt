package com.rikkeisoft.awesome.ui.login

import androidx.lifecycle.viewModelScope
import com.project.core.base.BaseViewModel
import com.project.core.utils.EdtState
import com.project.core.utils.SingleLiveEvent
import com.project.core.utils.StringUtils.isEmailValid
import com.project.core.utils.StringUtils.validatepassword
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.auth.R
import com.rikkeisoft.awesome.ui.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    val actionLogin = SingleLiveEvent<LoginActionState>()
    private val _isShowNoticeDialog = MutableSharedFlow<Pair<Boolean, String>>()
    val isShowNoticeDialog = _isShowNoticeDialog.asSharedFlow()
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
        val handlerException = CoroutineExceptionHandler { _, e ->
            isLoading.value = false
            viewModelScope.launch {
                _isShowNoticeDialog.emit(
                    Pair(
                        true, ResourceUtils.getString(R.string.failed_login_message)
                    )
                )
            }
        }
        isLoading.value = true
        if (_email.value.isEmailValid() && _password.value.validatepassword() == EdtState.SUCCESS) {
            viewModelScope.launch(handlerException) {
//                authRepository.login(_email.value, _password.value)
                isLoading.value = false
                actionLogin.value = LoginActionState.NavToHomeScreen
            }
        } else {
            isLoading.value = false
            viewModelScope.launch {
                _isShowNoticeDialog.emit(
                    Pair(
                        true, ResourceUtils.getString(R.string.error_validate_email_password_field)
                    )
                )
            }
        }
    }

    fun onRegister() {
        actionLogin.value = LoginActionState.NavToRegisterScreen
    }

    fun onCloseNoticeDialog() {
        viewModelScope.launch {
            _isShowNoticeDialog.emit(Pair(false, ""))
        }
    }

    sealed class LoginActionState {
        data object NavToHomeScreen : LoginActionState()
        data object NavToRegisterScreen : LoginActionState()
    }
}
