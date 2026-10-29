package io.cobrowse.sample.compose.ui.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.cobrowse.cobrowseRedacted
import io.cobrowse.sample.compose.ui.CobrowseViewModelFactory
import io.cobrowse.sample.compose.ui.EndSessionAction
import io.cobrowse.sample.compose.ui.SampleButton
import io.cobrowse.sample.compose.ui.SampleTextButton
import io.cobrowse.sample.compose.ui.SampleTopAppBar
import io.cobrowse.sample.compose.ui.openPrivacyPolicy
import io.cobrowse.sample.compose.ui.rememberCobrowseSession
import io.cobrowse.sample.compose.ui.theme.AccentLightColor
import io.cobrowse.sample.compose.ui.theme.PrimaryDarkColor
import io.cobrowse.sample.compose.ui.theme.viewTextStyle
import io.cobrowse.sample.core.R

/**
 * Displays the user who is currently logged in, and lets them share a session code or start
 * the Agent Present mode.
 */
@Composable
fun AccountScreen(
    viewModelFactory: CobrowseViewModelFactory,
    onAgentPresent: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: AccountViewModel = viewModel(factory = viewModelFactory)
    val sessionCode by viewModel.sessionCode.collectAsStateWithLifecycle()
    val session by rememberCobrowseSession()
    val isSessionActive = session?.isActive == true
    val context = LocalContext.current

    // The session code is no longer needed once the session has started or ended
    LaunchedEffect(isSessionActive, session == null) {
        if (isSessionActive || session == null) viewModel.clearSessionCode()
    }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = stringResource(R.string.fragment_account_title),
                onBack = onBack,
                statusBarColor = PrimaryDarkColor,
                actions = {
                    EndSessionAction()
                    IconButton(onClick = onSettings) {
                        Icon(
                            painter = painterResource(R.drawable.ic_action_settings),
                            contentDescription = stringResource(R.string.menu_item_settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Like in the View-based sample: the logo is 92dp below the status bar,
            // and the 56dp app bar covers the top of that space
            Spacer(modifier = Modifier.height(36.dp))

            Image(
                painter = painterResource(R.drawable.logo_account),
                contentDescription = null
            )

            Text(
                text = viewModel.user?.displayName.orEmpty(),
                style = viewTextStyle(22.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.cobrowseRedacted()
            )

            Text(
                text = viewModel.user?.email.orEmpty(),
                style = viewTextStyle(14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.cobrowseRedacted()
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = sessionCode,
                style = viewTextStyle(22.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.alpha(if (sessionCode.isEmpty()) 0f else 1f)
            )

            // Keep the buttons' space while hidden, so the screen doesn't jump
            val sessionButtonsAlpha = if (isSessionActive) 0f else 1f
            SampleButton(
                onClick = { viewModel.requestSessionCode() },
                enabled = !isSessionActive,
                modifier = Modifier
                    .width(220.dp)
                    .alpha(sessionButtonsAlpha)
            ) {
                Text(stringResource(R.string.button_get_session_code))
            }

            SampleButton(
                onClick = onAgentPresent,
                enabled = !isSessionActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentLightColor,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .width(220.dp)
                    .alpha(sessionButtonsAlpha)
            ) {
                Text(stringResource(R.string.button_agent_present))
            }

            SampleTextButton(
                text = stringResource(R.string.log_out),
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.width(220.dp)
            )

            SampleTextButton(
                text = stringResource(R.string.privacy_policy),
                onClick = { context.openPrivacyPolicy() },
                modifier = Modifier.width(220.dp)
            )
        }
    }
}
