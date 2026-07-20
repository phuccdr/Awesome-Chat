package com.rikkeisoft.awesome.ui.register

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
import com.project.core.base.dialog.CONFIRM_DIALOG_FRAGMENT
import com.project.core.base.dialog.NoticeDialog
import com.project.core.base.dialog.NoticeDialogListener
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.resource.ResourceUtils
import com.project.core.utils.setOnSafeClickListener
import com.project.core.utils.textspan.CustomTypefaceSpan
import com.project.core.utils.textspan.setClickableSpan
import com.rikkeisoft.awesome.AuthNavigation
import com.rikkeisoft.awesome.auth.R
import com.rikkeisoft.awesome.auth.databinding.FragmentRegisterBinding
import com.rikkeisoft.awesome.ui.AsteriskPasswordTransformationMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(R.layout.fragment_register),
    NoticeDialogListener {
    private val viewModel: RegisterViewModel by viewModels()
    override fun getVM(): RegisterViewModel {
        return viewModel
    }

    @Inject
    lateinit var appNavigator: AuthNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupTermsAndConditions()
        setupBackToLogin()
        setupUserNameEdt()
        setupEmailEdt()
        setupPasswordEdt()
        setupCheckBox()
        setupRegisterButton()
    }
    private fun setupUserNameEdt(){
        binding.edtName.
            doAfterTextChanged {text->
                viewModel.onUserNameChanged(
                    text.toString()
                )
            }

    }
    private fun setupEmailEdt(){
        binding.edtEmail.
        doAfterTextChanged {text->
            viewModel.onEmailChanged(
                text.toString()
            )
        }
    }

    private fun setupPasswordEdt() {
    binding.edtPassword.apply {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = AsteriskPasswordTransformationMethod()

        doAfterTextChanged { text ->
            viewModel.onPasswordChange(text.toString())
        }

//        setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                scrollDown()
//            }else{
//                scrollUp()
//            }
//        }
    }
}

//private fun scrollDown() {
//    binding.scrollView.postDelayed({
//        binding.scrollView.fullScroll(android.view.View.FOCUS_DOWN)
//    }, 300)
//}
//    private fun scrollUp() {
//        binding.scrollView.postDelayed({
//            binding.scrollView.fullScroll(android.view.View.FOCUS_UP)
//        }, 300)
//    }
    private fun setupCheckBox() {
        binding.btnCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onCheckedTermsAndConditions(isChecked)
        }
    }
    private fun setupBackToLogin() {
        val spannable = SpannableString(ResourceUtils.getString(R.string.back_to_login))

        val regular = ResourceUtils.getFont(com.project.core.R.font.lato_regular)
        val bold = ResourceUtils.getFont(com.project.core.R.font.lato_bold)

        if (regular != null && bold != null) {
            spannable.setSpan(
                CustomTypefaceSpan(regular),
                0,
                16,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(bold),
                17,
                spannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                ForegroundColorSpan(
                    ResourceUtils.getColor(com.project.core.R.color.text_gray_secondary)
                ),
                0,
                16,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setClickableSpan(
                17,
                spannable.length,
                ResourceUtils.getColor(com.project.core.R.color.primary_color)
            ) {
                viewModel.onBack()
            }

            binding.tvBackLogin.apply {
                text = spannable
                movementMethod = LinkMovementMethod.getInstance()
                highlightColor = Color.TRANSPARENT
            }
        }
    }

    private fun setupTermsAndConditions() {
        val spannable = SpannableString(ResourceUtils.getString(R.string.terms_and_conditions))

        val regular = ResourceUtils.getFont(com.project.core.R.font.lato_regular)
        val bold = ResourceUtils.getFont(com.project.core.R.font.lato_bold)
        if (regular != null && bold != null) {
            spannable.setSpan(
                CustomTypefaceSpan(regular), 0, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(bold), 19, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(regular),
                31,
                32,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                CustomTypefaceSpan(bold), 33, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        spannable.setSpan(
            ForegroundColorSpan(ResourceUtils.getColor(com.project.core.R.color.text_gray_secondary)),
            0,
            18,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(ResourceUtils.getColor(com.project.core.R.color.text_gray_secondary)),
            31,
            34,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setClickableSpan(19, 29, ResourceUtils.getColor(com.project.core.R.color.primary_color)) {
            //Open Chính sách
        }
        spannable.setClickableSpan(33,spannable.length, ResourceUtils.getColor(com.project.core.R.color.primary_color)){
            //Open diều kiện
        }
            binding.tvTermsAndConditions.apply {
                text = spannable
                movementMethod = LinkMovementMethod.getInstance()
                highlightColor = Color.TRANSPARENT
            }
    }}

    private fun setupRegisterButton(){
        binding.btnRegister.setOnSafeClickListener {
            viewModel.onRegister()
        }
    }

    override fun bindingAction(){
        super.bindingAction()
        viewModel.registerAction.observe(viewLifecycleOwner){action->
            if(action == RegisterViewModel.RegisterActionState.NavToLoginScreen){
                appNavigator.openRegisterToLogin()
            }else if(action == RegisterViewModel.RegisterActionState.NavToHomeScreen){

            }
        }
}

    override fun bindingStateView() {
        super.bindingStateView()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isEnableRegisterButton.collect { enable ->
                        binding.btnRegister.isEnabled = enable
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

    private fun showUpNoticeDialog(title:String){
        if (childFragmentManager.findFragmentByTag(CONFIRM_DIALOG_FRAGMENT) == null) {
            val demoDialog = NoticeDialog.getInstance(title)
            demoDialog.dialogListener = this
            demoDialog.show(childFragmentManager, CONFIRM_DIALOG_FRAGMENT)
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.apply{
            btnBack.setOnSafeClickListener {
            viewModel.onBack()
        }
        }
    }

    override fun onClickOk(type: Int?) {
        viewModel.onCloseNoticeDialog()
    }

}