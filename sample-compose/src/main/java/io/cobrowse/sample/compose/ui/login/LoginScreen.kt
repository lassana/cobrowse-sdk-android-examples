package io.cobrowse.sample.compose.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.cobrowse.cobrowseRedacted
import io.cobrowse.sample.compose.R
import io.cobrowse.sample.compose.ui.CobrowseViewModelFactory
import io.cobrowse.sample.compose.ui.EndSessionAction
import io.cobrowse.sample.compose.ui.SampleTopAppBar
import io.cobrowse.sample.compose.ui.openPrivacyPolicy

/**
 * A screen with the login form.
 */
@Composable
fun LoginScreen(
    viewModelFactory: CobrowseViewModelFactory,
    onLoginSuccess: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = stringResource(R.string.app_name),
                actions = { EndSessionAction() }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(io.cobrowse.sample.core.R.string.login_welcome),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text(stringResource(io.cobrowse.sample.core.R.string.prompt_email)) },
                singleLine = true,
                isError = uiState.usernameError != null,
                supportingText = {
                    uiState.usernameError?.let { Text(stringResource(it)) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .cobrowseRedacted()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text(stringResource(io.cobrowse.sample.core.R.string.prompt_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.passwordError != null,
                supportingText = {
                    uiState.passwordError?.let { Text(stringResource(it)) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.login() }
                ),
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .cobrowseRedacted()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.login() },
                enabled = !uiState.isLoading &&
                         uiState.username.isNotBlank() &&
                         uiState.password.isNotEmpty(),
                modifier = Modifier.widthIn(min = 120.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(io.cobrowse.sample.core.R.string.action_sign_in))
                }
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(it),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = stringResource(io.cobrowse.sample.core.R.string.login_welcome_secondary),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { context.openPrivacyPolicy() }) {
                Text(stringResource(io.cobrowse.sample.core.R.string.privacy_policy))
            }
        }
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
fun LoginScreenPreview() {
    LoginScreen(CobrowseViewModelFactory()) {
    }
}
