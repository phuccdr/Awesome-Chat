package com.rikkeisoft.awesome.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.project.core.base.dialog.CONFIRM_DIALOG_FRAGMENTxoa123
import com.project.core.base.dialog.NoticeDialog
import com.project.core.base.dialog.NoticeDialogListener
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListenerxoa123
import com.project.core.utils.textspan.CustomTypefaceSpan
import com.project.core.utils.textspan.setClickableSpanxoa123
import com.rikkeisoft.awesome.AuthNavigation
import com.rikkeisoft.awesome.auth.R
import com.rikkeisoft.awesome.auth.databinding.FragmentLoginBinding
import com.rikkeisoft.awesome.ui.AsteriskPasswordTransformationMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login),
    NoticeDialogListener {
    @Inject
    lateinit var appNavigator: AuthNavigation
    private val viewModel: LoginViewModel by viewModels()
    override fun getVM(): LoginViewModel {
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupRegisterText()
        setupEmailEdt()
        setupPasswordEdt()
    }

    private fun setupEmailEdt() {
        binding.edtEmail.doAfterTextChanged { text ->
            viewModel.onEmailChanged(text.toString())
        }
    }

    private fun setupPasswordEdt() {
        binding.edtPassword.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = AsteriskPasswordTransformationMethod()
            doAfterTextChanged { text ->
                viewModel.onPasswordChange(text.toString())
            }
        }
    }

    fun setupRegisterText() {
        val spannable =
            SpannableString(ResourceUtils.getString(com.project.core.R.string.register_account))
        val regular = ResourceUtils.getFont(com.project.core.R.font.lato_regular)
        val bold = ResourceUtils.getFont(com.project.core.R.font.lato_bold)
        if (regular != null && bold != null) {
            spannable.setSpan(
                CustomTypefaceSpan(regular), 0, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(bold), 18, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        spannable.setSpan(
            ForegroundColorSpan(ResourceUtils.getColor(com.project.core.R.color.text_gray_secondary)),
            0,
            18,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setClickableSpanxoa123(
            18, spannable.length, ResourceUtils.getColor(com.project.core.R.color.primary_color)
        ) {
            viewModel.onRegister()
        }
        binding.tvRegister.apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.enableLogin.collect { enable ->
                        binding.btnLogin.isEnabled = enable
                    }
                }
                launch {
                    viewModel.isShowNoticeDialog.filter { it.first }.collect {
                        showUpNoticeDialog(it.second)
                    }
                }
            }
        }
    }

    private fun showUpNoticeDialog(title: String) {
        if (childFragmentManager.findFragmentByTag(CONFIRM_DIALOG_FRAGMENTxoa123) == null) {
            val demoDialog = NoticeDialog.getInstance(title)
            demoDialog.dialogListener = this@LoginFragment
            demoDialog.show(childFragmentManager, CONFIRM_DIALOG_FRAGMENTxoa123)
        }
    }

    override fun bindingAction() {
        super.bindingAction()
        viewModel.actionLogin.observe(viewLifecycleOwner) { action ->
            when (action) {
                is LoginViewModel.LoginActionState.NavToRegisterScreen -> {
                    appNavigator.openLoginToRegisterScreen()
                }

                is LoginViewModel.LoginActionState.NavToHomeScreen -> {
                    appNavigator.openLoginToHomeScreen()
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.btnLogin.setOnSafeClickListenerxoa123 {
            viewModel.onLogin()
        }

    }

    override fun onClickOk(type: Int?) {
        viewModel.onCloseNoticeDialog()
    }
}
