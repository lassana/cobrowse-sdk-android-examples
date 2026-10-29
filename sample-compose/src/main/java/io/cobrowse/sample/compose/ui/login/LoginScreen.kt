package io.cobrowse.sample.compose.ui.login

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.cobrowse.cobrowseRedacted
import io.cobrowse.sample.compose.R
import io.cobrowse.sample.compose.ui.CobrowseViewModelFactory
import io.cobrowse.sample.compose.ui.EndSessionAction
import io.cobrowse.sample.compose.ui.SampleButton
import io.cobrowse.sample.compose.ui.SampleTextButton
import io.cobrowse.sample.compose.ui.SampleTopAppBar
import io.cobrowse.sample.compose.ui.openPrivacyPolicy
import io.cobrowse.sample.compose.ui.theme.viewTextStyle

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
    val layoutDirection = LocalLayoutDirection.current

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
        // Like in the View-based sample, the form is laid out from the top of the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(96.dp))

                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(io.cobrowse.sample.core.R.string.login_welcome),
                    style = viewTextStyle(14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                UnderlinedTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    hint = stringResource(uiState.usernameError ?: io.cobrowse.sample.core.R.string.prompt_email),
                    isError = uiState.usernameError != null,
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

                UnderlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    hint = stringResource(uiState.passwordError ?: io.cobrowse.sample.core.R.string.prompt_password),
                    isError = uiState.passwordError != null,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { viewModel.login() }),
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                        .cobrowseRedacted()
                )

                Spacer(modifier = Modifier.height(14.dp))

                SampleButton(
                    onClick = { viewModel.login() },
                    enabled = !uiState.isLoading &&
                            uiState.username.isNotBlank() &&
                            uiState.password.isNotEmpty()
                ) {
                    Text(stringResource(io.cobrowse.sample.core.R.string.action_sign_in))
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(48.dp)
                    )
                }

                uiState.errorMessage?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = annotatedStringResource(io.cobrowse.sample.core.R.string.login_welcome_secondary),
                    style = viewTextStyle(14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                SampleTextButton(
                    text = stringResource(io.cobrowse.sample.core.R.string.privacy_policy),
                    onClick = { context.openPrivacyPolicy() },
                    fontSize = 12.sp,
                    modifier = Modifier.width(220.dp)
                )
            }
        }
    }
}

/**
 * A single line text field with an underline, like an `EditText`.
 */
@Composable
private fun UnderlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colors = MaterialTheme.colorScheme
    val textStyle = viewTextStyle(18.sp).copy(color = colors.onSurface)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.secondary),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        modifier = modifier.height(46.dp)
    ) { innerTextField ->
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            Box(
                modifier = Modifier
                    .padding(top = TEXT_FIELD_TEXT_TOP)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = textStyle,
                        color = if (isError) colors.error else colors.onSurfaceVariant
                    )
                }
                innerTextField()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        when {
                            isError -> colors.error
                            isFocused -> colors.secondary
                            else -> colors.onSurfaceVariant
                        }
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Where an `EditText` places its text, below its top edge
private val TEXT_FIELD_TEXT_TOP = 10.dp

/**
 * Loads a string resource with its styling, like `<b>` and `<small>`.
 */
@Composable
private fun annotatedStringResource(@StringRes id: Int): AnnotatedString {
    val text = LocalContext.current.resources.getText(id)
    return remember(text) {
        val spanned = text as? Spanned ?: return@remember AnnotatedString(text.toString())
        buildAnnotatedString {
            append(spanned.toString())
            spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)
                when (span) {
                    is StyleSpan -> if (span.style == Typeface.BOLD) {
                        addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                    }
                    is RelativeSizeSpan -> addStyle(SpanStyle(fontSize = span.sizeChange.em), start, end)
                }
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
