package com.rikkeisoft.awesome

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.collectLatestFlowOnView
import com.project.core.utils.loadImage
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.profile.R
import com.rikkeisoft.awesome.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var appNavigation: ProfileNavigation

    override fun getVM(): ProfileViewModel = viewModel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        viewModel.loadUserProfile()
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.btnEditProfile.setOnSafeClickListener {
            appNavigation.openProfileToEditProfile()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.userProfile.collectLatestFlowOnView(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.username
                binding.tvEmail.text = it.email
                binding.ivAvatar.loadImage(it.avatar, isCircle = true)
                binding.ivCoverPhoto.loadImage(it.avatar)
            }
        }
    }
}