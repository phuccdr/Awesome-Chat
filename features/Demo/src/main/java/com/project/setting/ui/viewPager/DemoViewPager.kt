package com.project.setting.ui.viewPager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.project.core.base.fragment.BaseFragmentNotRequireViewModel
import com.project.core.utils.getCurrentFragment
import com.project.core.utils.getFragmentAt
import com.project.core.utils.setOnSafeClickListener
import com.project.setting.R
import com.project.setting.databinding.FragmentDemoViewpagerBinding
import timber.log.Timber

class DemoViewPager :
    BaseFragmentNotRequireViewModel<FragmentDemoViewpagerBinding>(R.layout.fragment_demo_viewpager),
    DemoFragment.LoadFragmentListener {

    private val mViewModel: DemoViewPagerViewModel by activityViewModels()

    //ViewPager dynamic
    private var viewPager1Adapter: MyPagerAdapter? = null
    private val listFragment1 = arrayListOf<DemoFragment>()

    private var viewPager2Adapter: MyStatePagerAdapter? = null
    private val listFragment2 = arrayListOf<DemoFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        initDynamicViewPager2()
//        initDynamicViewPager1()
    }

    private fun initDynamicViewPager2() {
        listFragment2.add(getFragmentAtVP2(0) ?: DemoFragment.getInstance(1))
        listFragment2.add(getFragmentAtVP2(1) ?: DemoFragment.getInstance(2))
        listFragment2.add(getFragmentAtVP2(2) ?: DemoFragment.getInstance(3))
        listFragment2.add(getFragmentAtVP2(3) ?: DemoFragment.getInstance(4))
        viewPager2Adapter = MyStatePagerAdapter(
            listFragment2,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )
        binding.viewPager2.adapter = viewPager2Adapter
        binding.viewPager2.post {
            binding.viewPager2.offscreenPageLimit = 1
            binding.viewPager2.currentItem = 3
        }

//        listFragment2.forEach {
//            it.setFragmentListener(object : DemoFragment.LoadFragmentListener{
//                override fun onResumeItem() {
//                    if (it === binding.viewPager2.getFragmentAt(childFragmentManager,3)){
//                        mViewModel.canLoadData()
//                    }
//                }
//            })
//        }
    }

    private fun getFragmentAtVP2(position: Int): DemoFragment? {
        val currentFragment =
            binding.viewPager2.getFragmentAt(childFragmentManager, position)
        return currentFragment as? DemoFragment
    }

    private fun getFragmentAtVP1(position: Int): DemoFragment? {
        val currentFragment =
            binding.viewPager1.getFragmentAt(childFragmentManager, position)
        return currentFragment as? DemoFragment
    }

    override fun onResumeItem(demoFragment: DemoFragment) {
        if (demoFragment === binding.viewPager2.getFragmentAt(childFragmentManager, 3)) {
            mViewModel.canLoadData()
        }
    }

    private fun initDynamicViewPager1() {
        listFragment1.add(getFragmentAtVP1(0) ?: DemoFragment.getInstance(1))
        listFragment1.add(getFragmentAtVP1(1) ?: DemoFragment.getInstance(2))
        listFragment1.add(getFragmentAtVP1(2) ?: DemoFragment.getInstance(3))
        listFragment1.add(getFragmentAtVP1(3) ?: DemoFragment.getInstance(4))

        viewPager1Adapter = MyPagerAdapter(listFragment1, childFragmentManager)
        binding.viewPager1.adapter = viewPager1Adapter
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.btn.setOnSafeClickListener {
            val fragment = binding.viewPager1.getCurrentFragment(childFragmentManager)
            if (fragment is DemoFragment) {
                fragment.updateText()
            }

            val fragment2 = binding.viewPager2.getCurrentFragment(childFragmentManager)
            if (fragment2 is DemoFragment) {
                fragment2.updateText()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.tag("VietBH").d("B   " + "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("VietBH").d("B   " + "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.tag("VietBH").d("B   " + "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.tag("VietBH").d("B   " + "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.tag("VietBH").d("B   " + "onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.tag("VietBH").d("B   " + "onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.tag("VietBH").d("B   " + "onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.tag("VietBH").d("B   " + "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.tag("VietBH").d("B   " + "onDestroyView")
        listFragment1.clear()
        binding.viewPager1.adapter = null

        listFragment2.clear()
        binding.viewPager2.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("VietBH").d("B   " + "onDestroy")
        super.onDestroy()
    }
}