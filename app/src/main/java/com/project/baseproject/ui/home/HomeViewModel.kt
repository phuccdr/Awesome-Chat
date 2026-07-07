package com.project.baseproject.ui.home

import androidx.lifecycle.viewModelScope
import com.project.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : BaseViewModel() {
    private val _bottomNavSelected: MutableSharedFlow<Int> = MutableSharedFlow(0)
    val bottomNavSelected: SharedFlow<Int> = _bottomNavSelected.asSharedFlow()
    private val _itemSelected: MutableStateFlow<Int> = MutableStateFlow(1)
    val itemSelected: StateFlow<Int> = _itemSelected.asStateFlow()

    fun onClickItem(id: Int) {
        viewModelScope.launch {
            _bottomNavSelected.emit(id)
        }
    }

    fun onItemSelected(id: Int) {
        _itemSelected.value = id
        viewModelScope.launch {
            _bottomNavSelected.emit(id)
        }
    }
}
