package com.rikkeisoft.awesome.ui.login

import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.textspan.CustomTypefaceSpan
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
        val spannable =
            SpannableString(ResourceUtils.getString(com.project.core.R.string.register_account))
        val black = ResourceUtils.getFont(com.project.core.R.font.exo_black)
        val regular = ResourceUtils.getFont(com.project.core.R.font.exo_light)
        if (black != null && regular != null) {
            spannable.setSpan(
                CustomTypefaceSpan(black), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(regular), 8, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvRegister.text = spannable
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
        binding.btnLogin.setOnClickListener {
            viewModel.onLogin()
        }
    }


}