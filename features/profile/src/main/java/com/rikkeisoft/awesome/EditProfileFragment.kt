package com.rikkeisoft.awesome

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.loadImage
import com.project.core.utils.toString
import com.rikkeisoft.awesome.profile.R
import com.rikkeisoft.awesome.profile.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding, EditProfileViewModel>(R.layout.fragment_edit_profile) {
    private val viewModel: EditProfileViewModel by viewModels()

    override fun getVM(): EditProfileViewModel {
        return viewModel
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        viewModel.loadUserProfile()
    }

    override fun setOnClick() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val username = binding.edtName.text.toString()
            val phone = binding.edtPhoneNumber.text.toString()
            val birthday = binding.edtBirthOfDate.text.toString()
            viewModel.updateUserProfile(username, phone, birthday)
        }
    }

    override fun bindingStateView() {
        lifecycleScope.launch {
            viewModel.userProfile.collectLatest { user ->
                user?.let {
                    binding.ivAvatar.loadImage(it.avatar, true)
                    binding.edtName.setText(it.username)
                    binding.edtPhoneNumber.setText(it.phoneNumber)
                    binding.edtBirthOfDate.setText(
                        it.birthOfDay?.toDate()?.toString("dd/MM/yyyy") ?: ""
                    )
                }
            }
        }
    }

    override fun bindingAction() {
        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().popBackStack()
            }
        }
    }
}