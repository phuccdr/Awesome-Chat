package com.project.setting.ui.tabFavorite

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.setLanguage
import com.project.core.utils.setOnSafeClickListener
import com.project.setting.R
import com.project.setting.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment :
    BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>(R.layout.fragment_favorite) {
    private val viewModel: FavoriteViewModel by viewModels()

    override fun getVM(): FavoriteViewModel = viewModel

    override fun setOnClick() {
        super.setOnClick()

        binding.btnVietNam.setOnSafeClickListener {
            changeLanguage("vi")
        }

        binding.btnEnglish.setOnSafeClickListener {
            changeLanguage("en")
        }
    }

    private fun changeLanguage(language: String) {
        requireContext().setLanguage(language)
        viewModel.setLanguage(language)

        binding.btnVietNam.text = getString(com.project.core.R.string.viet_nam)
        binding.btnEnglish.text = getString(com.project.core.R.string.english)
    }

}