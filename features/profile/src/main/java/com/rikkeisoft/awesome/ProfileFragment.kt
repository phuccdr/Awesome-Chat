package com.rikkeisoft.awesome

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.profile.R
import com.rikkeisoft.awesome.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {
    private val viewModel: ProfileViewModel by viewModels()

    override fun getVM(): ProfileViewModel = viewModel
}