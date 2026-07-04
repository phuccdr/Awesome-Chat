package com.rikkeisoft.awesome.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListener
import com.project.core.utils.textspan.CustomTypefaceSpan
import com.project.core.utils.textspan.setClickableSpan
import com.rikkeisoft.awesome.AuthNavigation
import com.rikkeisoft.awesome.R
import com.rikkeisoft.awesome.databinding.FragmentLoginBinding
import com.rikkeisoft.awesome.ui.AsteriskPasswordTransformationMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(R.layout.fragment_login) {
    @Inject
    lateinit var appNavigator: AuthNavigation
    private val viewModel: LoginViewModel by viewModels()
    override fun getVM(): LoginViewModel {
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.edtPassword.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = AsteriskPasswordTransformationMethod()
        }
        setupRegisterText()
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
        spannable.setClickableSpan(
            18, spannable.length, ResourceUtils.getColor(com.project.core.R.color.colorPrimary)
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
                viewModel.enableLogin.collect { enable ->
                    binding.btnLogin.isEnabled = enable
                }
            }
        }
    }

    override fun bindingAction() {
        super.bindingAction()
        viewModel.actionLogin.observe(viewLifecycleOwner) { action ->
            if (action == LoginViewModel.LoginActionState.NavToRegisterScreen) {
                appNavigator.openLoginToRegisterScreen()
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.btnLogin.setOnSafeClickListener {
            viewModel.onLogin()
        }

    }


}