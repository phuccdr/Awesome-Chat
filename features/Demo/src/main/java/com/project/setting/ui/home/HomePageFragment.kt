package com.project.setting.ui.home

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.project.core.adapter.OnItemClickListener
import com.project.core.base.dialog.CONFIRM_DIALOG_FRAGMENTxoa123
import com.project.core.base.dialog.ConfirmDialogListener
import com.project.core.base.dialog.NOTICE_DIALOG_FRAGMENTxoa123
import com.project.core.base.dialog.NoticeDialog
import com.project.core.base.dialog.NoticeDialogListener
import com.project.core.base.fragment.BaseFragment
import com.project.core.utils.prefetcher.bindToLifecyclexoa123
import com.project.core.utils.prefetcher.setupWithPrefetchViewPoolxoa123
import com.project.core.utils.setOnSafeClickListenerxoa123
import com.project.core.utils.toast
import com.project.permission.requestPermission
import com.project.setting.DemoNavigation
import com.project.setting.R
import com.project.setting.adapter.HomePageAdapter
import com.project.setting.adapter.HomeSlideViewHolder
import com.project.setting.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomePageFragment :
    BaseFragment<FragmentHomePageBinding, HomePageViewModel>(R.layout.fragment_home_page),
    ConfirmDialogListener, NoticeDialogListener {

    @Inject
    lateinit var appNavigation: DemoNavigation

    private var adapterHomePage: HomePageAdapter? = null

    private val viewModel: HomePageViewModel by viewModels()

    override fun getVM() = viewModel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {

        binding.layoutHome.apply {

            adapterHomePage = HomePageAdapter(object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    //do nothing
                }
            })

            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapterHomePage?.getItemViewType(position)) {
                        R.layout.item_home_slide_layout -> 2
                        R.layout.item_song_layout -> 2
                        R.layout.item_title_home_layout -> 2
                        R.layout.item_album_layout -> 1
                        else -> 2
                    }
                }
            }

            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            adapter = adapterHomePage

            setupWithPrefetchViewPoolxoa123 {
                setPrefetchBoundxoa123(viewType = R.layout.item_album_layout, count = 6)
                setPrefetchBoundxoa123(viewType = R.layout.item_song_layout, count = 16)
                setPrefetchBoundxoa123(viewType = R.layout.item_home_slide_layout, count = 1)
                setPrefetchBoundxoa123(viewType = R.layout.item_title_home_layout, count = 2)
            }.bindToLifecyclexoa123(viewLifecycleOwner)

        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listHomePage.collect {
                    adapterHomePage?.submitList(it)
                }
            }
        }

    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btn.setOnSafeClickListenerxoa123 {
            appNavigation.openDemoViewPager()
        }

        binding.btnDialog.setOnSafeClickListenerxoa123 {
            if (childFragmentManager.findFragmentByTag(NOTICE_DIALOG_FRAGMENTxoa123) == null) {
                val demoDialog = NoticeDialog.getInstance("Title")
                demoDialog.dialogListener = this
                demoDialog.show(childFragmentManager, CONFIRM_DIALOG_FRAGMENTxoa123)
            }
        }

        binding.btnPermission.setOnSafeClickListenerxoa123 {
            requestPermission(123, Manifest.permission.CAMERA)
        }
    }

    override fun onDestroyView() {
        val holder = binding.layoutHome.findViewHolderForAdapterPosition(0)
        if (holder is HomeSlideViewHolder) {
            holder.onViewRecycled()
        }
        adapterHomePage = null
        super.onDestroyView()
    }

    override fun onClickOk(type: Int?) {
        getString(com.project.core.R.string.ok).toast(requireContext())
    }

    override fun onClickCancel(type: Int?) {
        getString(com.project.core.R.string.cancel).toast(requireContext())
    }

}