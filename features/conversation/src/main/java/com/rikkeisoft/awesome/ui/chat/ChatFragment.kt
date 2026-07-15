package com.rikkeisoft.awesome.ui.chat

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(R.layout.fragment_chat) {
    private val viewModel: ChatViewModel by viewModels()
    override fun getVM() = viewModel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupChatRecyclerView()
    }

    private fun setupChatRecyclerView() {
        binding.rvMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.rvMessages.postDelayed({
                    val count = binding.rvMessages.adapter?.itemCount ?: 0
                    if (count > 0) {
                        binding.rvMessages.smoothScrollToPosition(count - 1)
                    }
                }, 100)
            }
        }
    }
}
