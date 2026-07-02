package com.rikkeisoft.awesome.ui.register

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.R
import com.rikkeisoft.awesome.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(R.layout.fragment_register) {
    private val viewModel: RegisterViewModel by viewModels()
    override fun getVM(): RegisterViewModel {
        return viewModel
    }

}