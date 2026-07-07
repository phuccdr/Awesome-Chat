package com.rikkeisoft.awesome.ui.conversation

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.setOnSafeClickListener
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentListConversationBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListConversationFragment :
    BaseFragment<FragmentListConversationBinding, ListConversationViewModel>(
        R.layout.fragment_list_conversation
    ) {
    private val viewModel: ListConversationViewModel by viewModels()
    override fun getVM() = viewModel

    @Inject
    lateinit var appNavigator: ConversationNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.btnChat.setOnSafeClickListener {
            appNavigator.openListConversationToChat()
        }
    }
}