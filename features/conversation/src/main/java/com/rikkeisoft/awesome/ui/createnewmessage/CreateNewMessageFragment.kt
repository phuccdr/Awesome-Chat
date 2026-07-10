package com.rikkeisoft.awesome.ui.createnewmessage

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentCreateNewMessageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateNewMessageFragment :
    BaseFragment<FragmentCreateNewMessageBinding, CreateNewMessageViewModel>(R.layout.fragment_create_new_message) {
    private val viewModel: CreateNewMessageViewModel by viewModels()
    override fun getVM(): CreateNewMessageViewModel {
        return viewModel
    }
}