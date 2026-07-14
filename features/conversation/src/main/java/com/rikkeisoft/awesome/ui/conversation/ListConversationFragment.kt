package com.rikkeisoft.awesome.ui.conversation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.core.base.fragment.BaseFragment
import com.project.core.navigationComponent.BundleKeys.CONVERSATION_ID
import com.project.core.utils.prefetcher.bindToLifecycle
import com.project.core.utils.prefetcher.setupWithPrefetchViewPool
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.ConversationNavigation
import com.rikkeisoft.awesome.adapter.ConversationSearchAdapter
import com.rikkeisoft.awesome.adapter.ListConversationAdapter
import com.rikkeisoft.awesome.conversation.R
import com.rikkeisoft.awesome.conversation.databinding.FragmentListConversationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ListConversationFragment :
    BaseFragment<FragmentListConversationBinding, ListConversationViewModel>(R.layout.fragment_list_conversation
    ) {
    val PRELOAD_CONVERSATION = 5
    private var adapterConversation: ListConversationAdapter? = null
    private var adapterSearch: ConversationSearchAdapter? = null
    private val viewModel: ListConversationViewModel by viewModels()
    override fun getVM() = viewModel

    @Inject
    lateinit var appNavigator: ConversationNavigation

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initAdapter()
        setupSearchTextInput()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchTextInput(){
        binding.apply {
            edtSearch.setOnFocusChangeListener { _, hasFocus ->
                viewModel.setSearching(true)
            }
            edtSearch.doAfterTextChanged { text ->
                viewModel.onSearch(text.toString())
                edtSearch.setCompoundDrawablesWithIntrinsicBounds(
                    edtSearch.compoundDrawables[0], null, if (text.isNullOrBlank()) {
                        null
                    } else {
                        ResourceUtils.getDrawable(R.drawable.ic_clear_edt)
                    }, null
                )
            }

            edtSearch.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = 2
                    edtSearch.compoundDrawables[drawableEnd]?.let {
                        if (event.x >= (edtSearch.width - edtSearch.paddingRight - it.bounds.width())) {
                            edtSearch.text?.clear()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
            btnCancelSearch.setOnClickListener {
                viewModel.setSearching(false)
            }
        }
    }

    private fun initAdapter() {
        adapterConversation = ListConversationAdapter{conversationId ->
            val data = Bundle().apply {
                putString(CONVERSATION_ID , conversationId)
            }
            appNavigator.openListConversationToChat(data)
        }
        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterConversation

            setupWithPrefetchViewPool {
                setPrefetchBound(viewType = R.layout.item_conversation, count = 10)
                setPrefetchBound(viewType = R.layout.item_loading_footer, count = 2)
            }.bindToLifecycle(viewLifecycleOwner)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // FIX: Chỉ xử lý khi cuộn xuống và danh sách có dữ liệu
                    if (dy <= 0) return
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (!viewModel.isLoadingNextConversation.value && 
                        viewModel.hasMoreData && 
                        totalItemCount > 0 && 
                        lastVisibleItem >= totalItemCount - PRELOAD_CONVERSATION) {
                        viewModel.loadNextPageConversations()
                    }
                }
            })
        }

        adapterSearch = ConversationSearchAdapter()
        binding.rvSearchMessage.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterSearch
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.items.collect {
                        adapterConversation?.submitList(it)
                    }
                }
                launch {
                    viewModel.resultSearch.collect { results ->
                        adapterSearch?.submitList(results)
                        val isSearching = !binding.edtSearch.text.isNullOrBlank()
                        Timber.d((isSearching && results.isEmpty()).toString())
                        binding.icNoResult.isVisible = isSearching && results.isEmpty()
                        binding.tvNoResult.isVisible = isSearching && results.isEmpty()
                    }
                }
                launch{
                    viewModel.isSearching.collect { isSearching ->
                        if(isSearching){
                            binding.apply{
                                rvConversations.visibility = View.GONE
                                groupSearchMessage.isVisible = true
                            }
                        }else{
                            binding.apply{
                                rvConversations.visibility = View.VISIBLE
                                groupSearchMessage.isVisible = false
                                edtSearch.text?.clear()
                                edtSearch.clearFocus()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvConversations.adapter = null
        binding.rvSearchMessage.adapter = null
        adapterConversation = null
        adapterSearch = null
        Timber.d("onDestroyView: View destroyed")
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("Attach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView: View initialized")
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated: View initialized")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Timber.d("onViewStateRestored: View created")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart: View started")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume: View resumed")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause: View started")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop: View created")
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }
}
