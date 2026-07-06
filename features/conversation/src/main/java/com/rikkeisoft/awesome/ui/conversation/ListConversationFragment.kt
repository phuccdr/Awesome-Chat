package com.rikkeisoft.awesome.ui.conversation

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.R
import com.rikkeisoft.awesome.databinding.FragmentListConversationBinding

class ListConversationFragment :
    BaseFragment<FragmentListConversationBinding, ListConversationViewModel>(
        R.layout.fragment_list_conversation
    ) {
    private val viewModel: ListConversationViewModel by viewModels()
    override fun getVM() = viewModel
}