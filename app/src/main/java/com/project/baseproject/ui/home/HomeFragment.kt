package com.project.baseproject.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.project.baseproject.R
import com.project.baseproject.databinding.FragmentHomeBinding
import com.project.baseproject.navigation.AppNavigation
import com.project.baseproject.navigation.HomeNavigation
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.setOnSafeClickListenerxoa123
import com.project.core.utils.tint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var homeNavigation: HomeNavigation
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
        homeNavigation.bind(navController)
        binding.apply {
            bottomNav.btnConversation.setOnSafeClickListenerxoa123 {
                viewModel.onItemSelected(1)
            }
            bottomNav.btnFriends.setOnSafeClickListenerxoa123 {
                viewModel.onItemSelected(2)
            }
            bottomNav.btnProfile.setOnSafeClickListenerxoa123 {
                viewModel.onItemSelected(3)
            }
        }
        updateBottomNavUI()
    }

    override fun bindingAction() {
        super.bindingAction()
        val options =
            NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true).setPopUpTo(
                navController.graph.findStartDestination().id, inclusive = false, saveState = true
            ).build()
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomNavSelected.collect { idSelected ->
                    when (idSelected) {
                        1 -> {
                            navController.navigate(R.id.conversations_graph, null, options)
                        }

                        2 -> {
                            navController.navigate(R.id.friends_graph, null, options)
                        }

                        3 -> {
                            navController.navigate(R.id.profile_graph, null, options)
                        }
                    }
                }
            }
        }
    }

    private fun updateBottomNavUI() {
        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController, destination: NavDestination, arguments: Bundle?
            ) {
                val selectedIndex = when {
                    destination.hierarchy.any { it.id == R.id.conversations_graph } -> 1
                    destination.hierarchy.any { it.id == R.id.friends_graph } -> 2
                    destination.hierarchy.any { it.id == R.id.profile_graph } -> 3
                    else -> return
                }
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
                val isTopLevelDestination =
                    destination.id == R.id.listConversationFragment || destination.id == R.id.friendsFragment || destination.id == R.id.profileFragment
                binding.bottomNav.root.isVisible = isTopLevelDestination
            }
        })

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
