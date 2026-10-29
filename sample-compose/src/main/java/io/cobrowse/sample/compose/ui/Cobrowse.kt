package io.cobrowse.sample.compose.ui

import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.cobrowse.Session
import io.cobrowse.sample.core.R
import io.cobrowse.sample.data.CobrowseSessionDelegate

/**
 * Whether the whole UI is hidden from the agent, except for the unredacted composables.
 */
object RedactionByDefault {
    var isEnabled by mutableStateOf(false)
        private set

    fun load(context: Context) {
        isEnabled = CobrowseSessionDelegate.isRedactionByDefaultEnabled(context)
    }

    fun set(context: Context, enabled: Boolean) {
        CobrowseSessionDelegate.setRedactionByDefaultEnabled(context, enabled)
        isEnabled = enabled
    }
}

/**
 * The current Cobrowse.io session, if any.
 */
@Composable
fun rememberCobrowseSession(): State<Session?> = CobrowseSessionDelegate.current.observeAsState()

/**
 * A toolbar action that ends the active Cobrowse.io session. It is shown only during a session.
 */
@Composable
fun EndSessionAction() {
    val session by rememberCobrowseSession()
    if (session?.isActive == true) {
        IconButton(onClick = { session?.end(null) }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_cancel_presentation),
                contentDescription = stringResource(R.string.end_cobrowse_session)
            )
        }
    }
}
