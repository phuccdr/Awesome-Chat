package com.project.setting.ui.tabFavorite

import androidx.fragment.app.viewModels
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.setLanguagexoa123
import com.project.core.utils.setOnSafeClickListenerxoa123
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

        binding.btnVietNam.setOnSafeClickListenerxoa123 {
            changeLanguage("vi")
        }

        binding.btnEnglish.setOnSafeClickListenerxoa123 {
            changeLanguage("en")
        }
    }

    private fun changeLanguage(language: String) {
        requireContext().setLanguagexoa123(language)
        viewModel.setLanguage(language)

        binding.btnVietNam.text = getString(com.project.core.R.string.viet_nam)
        binding.btnEnglish.text = getString(com.project.core.R.string.english)
    }

}