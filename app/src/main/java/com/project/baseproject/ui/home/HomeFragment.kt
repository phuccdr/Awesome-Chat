package com.project.baseproject.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.project.baseproject.R
import com.project.baseproject.databinding.FragmentHomeBinding
import com.project.baseproject.navigation.AppNavigation
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.setOnSafeClickListener
import com.project.core.utils.tint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    @Inject
    lateinit var appNavigation: AppNavigation
    lateinit var navController: NavController
    private val viewModel: HomeViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navHostFragment = childFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment
        navController = navHostFragment.navController
        binding.apply {
            bottomNav.btnConversation.setOnSafeClickListener {
                viewModel.onItemSelected(1)
            }
            bottomNav.btnFriends.setOnSafeClickListener {
                viewModel.onItemSelected(2)
            }
            bottomNav.btnProfile.setOnSafeClickListener {
                viewModel.onItemSelected(3)
            }
        }
    }

    override fun bindingAction() {
        super.bindingAction()
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomNavSelected.collect { idSelected ->
                    when (idSelected) {
                        1 -> {
                            navController.navigate(R.id.listConversationFragment)
                        }

                        2 -> {
                            navController.navigate(R.id.friendsFragment)
                        }

                        3 -> {
                            navController.navigate(R.id.profileFragment)
                        }
                    }
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemSelected.collect { index ->
                    updateBottomNavUI(index)
                }
            }
        }
    }

    private fun updateBottomNavUI(selectedIndex: Int) {
        with(binding.bottomNav) {
            val navItems = listOf(
                Triple(ivConversation, tvConversation, indicatorConversation),
                Triple(ivFriends, tvFriends, indicatorFriends),
                Triple(ivProfile, tvProfile, indicatorProfile)
            )
            navItems.forEachIndexed { index, (icon, label, indicator) ->
                val isSelected = (index + 1 == selectedIndex)
                updateNavItemState(icon, label, indicator, isSelected)
            }
        }
    }

    private fun updateNavItemState(
        icon: ImageView, tv: TextView, indicator: View, isSelected: Boolean
    ) {
        val colorRes =
            if (isSelected) com.project.core.R.color.primary_color else com.project.core.R.color.text_gray_secondary
        val styleRes =
            if (isSelected) R.style.BottomNavTextActive else R.style.BottomNavTextInactive

        icon.tint(colorRes)
        tv.setTextAppearance(styleRes)
        indicator.visibility = if (isSelected) View.VISIBLE else View.GONE
    }

    override fun getVM() = viewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("VietBH").d("A   " + "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("VietBH").d("A   " + "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Timber.tag("VietBH").d("A   " + "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag("VietBH").d("A   " + "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.tag("VietBH").d("A   " + "onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.tag("VietBH").d("A   " + "onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.tag("VietBH").d("A   " + "onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.tag("VietBH").d("A   " + "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.tag("VietBH").d("A   " + "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("VietBH").d("A   " + "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.tag("VietBH").d("A   " + "onCreate")
        super.onDetach()
    }
}
