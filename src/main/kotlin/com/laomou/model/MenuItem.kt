package com.laomou.model

data class MenuItem(
    val label: String,
    val action: () -> Unit,
    val enabled: Boolean = true
)