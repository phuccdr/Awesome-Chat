package com.rikkeisoft.awesome.ui.register

import androidx.lifecycle.viewModelScope
import com.project.core.base.BaseViewModel
import com.project.core.utils.EdtState
import com.project.core.utils.SingleLiveEvent
import com.project.core.utils.StringUtils.isEmailValid
import com.project.core.utils.StringUtils.validatepassword
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel(
) {
    val registerAction = SingleLiveEvent<RegisterActionState>()
    private val _isShowNoticeDialog = MutableSharedFlow<Pair<Boolean, String>>()
    val isShowNoticeDialog = _isShowNoticeDialog.asSharedFlow()
    private val _username: MutableStateFlow<String> = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()
    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    private val _checkedTerm: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val checkedTerm: StateFlow<Boolean> = _checkedTerm.asStateFlow()
    val isEnableRegisterButton: StateFlow<Boolean> =
        combine(username, _email, _password, _checkedTerm) { name, email, password, isChecked ->
            name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && isChecked
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), false
        )

    fun onUserNameChanged(userName: String) {
        _username.value = userName
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onBack() {
        registerAction.value = RegisterActionState.NavToLoginScreen
    }

    fun onCheckedTermsAndConditions(checked: Boolean) {
        _checkedTerm.value = checked
    }

    fun onRegister() {
        isLoading.value = true
        val handlerException = CoroutineExceptionHandler { _, e ->
            isLoading.value = false
            Timber.d(e.toString())
            viewModelScope.launch {
                _isShowNoticeDialog.emit(
                    Pair(
                        true,
                        com.project.core.utils.resource.ResourceUtils.getString(R.string.failed_register_message)
                    )
                )
            }
        }

        if (_email.value.isEmailValid() && _password.value.validatepassword() == EdtState.SUCCESS && _username.value.isNotBlank()) {
            viewModelScope.launch(handlerException) {
                authRepository.register(
                    email = _email.value, password = _password.value, username = _username.value
                )
                isLoading.value = false
                registerAction.value = RegisterActionState.NavToHomeScreen
            }
        } else {
            isLoading.value = false
            viewModelScope.launch {
                _isShowNoticeDialog.emit(
                    Pair(
                        true,
                        com.project.core.utils.resource.ResourceUtils.getString(R.string.error_validate_email_password_field)
                    )
                )
            }
        }
    }

    fun onCloseNoticeDialog() {
        viewModelScope.launch {
            _isShowNoticeDialog.emit(Pair(false, ""))
        }
    }

    sealed class RegisterActionState {
        data object NavToLoginScreen : RegisterActionState()

        data object NavToHomeScreen : RegisterActionState()
    }

}
