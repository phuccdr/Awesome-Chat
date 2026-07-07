package com.rikkeisoft.awesome.ui.search

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    private val viewModel: ChatViewModel by viewModels()
    override fun getVM() = viewModel
}