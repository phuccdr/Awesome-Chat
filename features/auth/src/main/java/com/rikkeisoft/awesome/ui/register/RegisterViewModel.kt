package com.rikkeisoft.awesome.ui.register

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
class RegisterViewModel @Inject constructor() : BaseViewModel(
) {
    val registerAction  = SingleLiveEvent<RegisterActionState>()
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
            name.isNotBlank() &&
                    email.isNotBlank() &&
                    password.length >= 6 &&
                    isChecked
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )




    fun onUserNameChanged(userName:String){
        _username.value = userName
    }
      fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onBack(){
        registerAction.value = RegisterActionState.NavToLoginScreen
    }
    fun onCheckedTermsAndConditions(checked:Boolean){
        _checkedTerm.value = checked
    }

    fun onLogin() {
        isLoading.value = true
        if (_email.value.isEmailValid() && _password.value.validatepassword() == 3&&_username.value.isNotBlank()) {
            // xly login
        } else {
            messageError.value = ""
            // xử lý hiển thị dialog
        }
    }

    sealed class RegisterActionState{
        data object NavToLoginScreen: RegisterActionState()
    }

}