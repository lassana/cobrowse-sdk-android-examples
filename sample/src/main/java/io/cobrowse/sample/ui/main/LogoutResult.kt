package io.cobrowse.sample.ui.main

/**
 * Deauthentication result : success (boolean) or error message.
 */
data class LogoutResult(
    val success: Boolean? = null,
    val error: Int? = null
)