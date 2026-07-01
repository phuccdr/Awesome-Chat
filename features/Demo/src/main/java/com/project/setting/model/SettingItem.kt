package com.project.setting.model

data class SettingItem(
    val name: String,
    var isSelected: Boolean,
    var isSelectedEnd: Boolean = false
)