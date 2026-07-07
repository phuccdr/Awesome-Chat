package com.rikkeisoft.awesome

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.friends.R
import com.rikkeisoft.awesome.friends.databinding.FragmentFriendsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsFragment :
    BaseFragment<FragmentFriendsBinding, FriendsViewModel>(R.layout.fragment_friends) {
    private val viewModel: FriendsViewModel by viewModels()
    override fun getVM() = viewModel

}