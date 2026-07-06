package com.rikkeisoft.awesome.ui.search

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.rikkeisoft.awesome.R
import com.rikkeisoft.awesome.databinding.FragmentSearchMessageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchMessageFragment :
    BaseFragment<FragmentSearchMessageBinding, SearchMessageViewModel>(R.layout.fragment_search_message) {
    private val viewModel: SearchMessageViewModel by viewModels()
    override fun getVM() = viewModel
}