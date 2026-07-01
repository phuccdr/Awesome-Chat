package com.project.setting.ui.home

import androidx.lifecycle.viewModelScope
import com.project.core.base.BaseViewModel
import com.project.setting.model.HomePageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "Test_Flow"

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val repository: HomeRepository
) : BaseViewModel() {

    val listHomePage = MutableStateFlow<List<HomePageItem>>(listOf())

    init {
//        getListSong()
//        useCase3()
        viewModelScope.launch {
            val current = System.currentTimeMillis()
            Timber.tag("Ahkljsdfk").d("Start: ")
            val a = async { getAbc(5000) }
            Timber.tag("Ahkljsdfk").d("a: " + (System.currentTimeMillis() - current))
            val b = async { getAbc(1000) }
            Timber.tag("Ahkljsdfk").d("b: " + (System.currentTimeMillis() - current))
//            delay(4000)
            val c = a.await()
            Timber.tag("Ahkljsdfk").d("c: " + (System.currentTimeMillis() - current))
            val d = b.await()
            Timber.tag("Ahkljsdfk").d("Finish: " + (System.currentTimeMillis() - current))
        }
    }

    suspend fun getAbc(duration: Long): Int {
        delay(duration)
        return 1
    }

    private fun getListSong() {
        repository.getDataAsync()
            .flowOn(Dispatchers.IO)
            .onStart {
                isLoading.value = true
            }.onCompletion {
                isLoading.value = false
            }.map {
                repository.handleResponse(it)
            }.onEach {
                listHomePage.value = it
            }.catch {
                messageError.value = it.message
            }.launchIn(viewModelScope)
    }

    //cho n flows chay song song, khi nao tat ca cung xong thi update len view
    private fun useCase1() {
        val flow1 = repository.delayFlow(1500)
        val flow2 = repository.delayFlow(2000)
        val flow3 = repository.delayFlow(3000)

        flow1.zip(flow2) { p1, p2 -> Pair(p1, p2) }
            .zip(flow3) { p1, p2 -> arrayListOf(p1.first, p1.second, p2) }
            .flowOn(Dispatchers.IO)
            .onStart {
            }.onCompletion {
                Timber.tag(TAG).d("onCompletion: ${it?.message}")
            }.onEach {
                //on Result
            }.catch {
                messageError.value = it.message
                Timber.tag(TAG).d("catch: ${it.message}")
            }.launchIn(viewModelScope)
    }

    //cho n flow chay song song, cai nao xong truoc hien thi truoc, khi nao tat ca cung xong thi update len view
    private fun useCase2() {
        val flow1 = repository.delayFlow(1500).map { Pair("Flow1", it) }
        val flow2 = repository.delayFlow(2000).map { Pair("Flow2", it) }
        val flow3 = repository.delayFlow(3000).map { Pair("Flow3", it) }

        merge(flow1, flow2, flow3)
            .flowOn(Dispatchers.IO)
            .onStart {
                Timber.tag(TAG).d("onStart: ")
            }.onCompletion {
                Timber.tag(TAG).d("onCompletion: ${it?.message}")
            }.onEach {
                //on Result
                Timber.tag(TAG).d("onEach: %s", it.first)
            }.catch {
                messageError.value = it.message
                Timber.tag(TAG).d("catch: ${it.message}")
            }.launchIn(viewModelScope)
    }

    //chay tuan tu n flow, khi nao tat ca cung xong thi update len view
    @OptIn(FlowPreview::class)
    private fun useCase3() {
        val flow1 = repository.delayFlow(1500)
        val flow2 = repository.delayFlow(2000)
        val flow3 = repository.delayFlow(3000)

        flow1.flatMapMerge { p1 -> flow2.map { Pair(p1, it) } }
            .flatMapConcat { p1 -> flow3.map { arrayListOf(p1.first, p1.second, it) } }
            .flowOn(Dispatchers.IO)
            .onStart {
                Timber.tag(TAG).d("onStart: ")
            }.onCompletion {
                Timber.tag(TAG).d("onCompletion: ${it?.message}")
            }.onEach {
                //on Result
                Timber.tag(TAG).d("onEach: %s", it)
            }.catch {
                messageError.value = it.message
                Timber.tag(TAG).d("catch: ${it.message}")
            }.launchIn(viewModelScope)
    }

}
