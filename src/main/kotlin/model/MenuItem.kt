package model

data class MenuItem(
    val label: String,
    val action: () -> Unit,
    val enabled: Boolean = true
)