package io.cobrowse.sample.compose.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.cobrowse.sample.compose.ui.transactions.TransactionWebView
import io.cobrowse.sample.compose.ui.transactions.TransactionsChartScreen
import io.cobrowse.sample.compose.ui.transactions.TransactionsScreen
import io.cobrowse.sample.core.R
import io.cobrowse.sample.data.model.detailsUrl
import kotlinx.coroutines.launch

/**
 * A page displayed in the bottom sheet.
 */
private sealed interface SheetDestination {
    data object Transactions : SheetDestination
    data class TransactionDetail(val url: String) : SheetDestination
}

/**
 * Displays the recent transactions statistics, and all transactions in a bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModelFactory: CobrowseViewModelFactory,
    onAccount: () -> Unit
) {
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )
    val scope = rememberCoroutineScope()
    val isSheetExpanded = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded

    // The pages opened in the bottom sheet, the last one is displayed
    var sheetPages by remember {
        mutableStateOf(listOf<SheetDestination>(SheetDestination.Transactions))
    }

    BackHandler(enabled = sheetPages.size > 1 || isSheetExpanded) {
        if (sheetPages.size > 1) {
            sheetPages = sheetPages.dropLast(1)
        } else {
            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
        }
    }

    RequestNotificationPermission()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = maxHeight * 0.4f,
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetContent = {
                AnimatedContent(
                    targetState = sheetPages,
                    transitionSpec = {
                        val forward = targetState.size >= initialState.size
                        (slideInHorizontally(tween(300)) { if (forward) it else -it } +
                            fadeIn(tween(220))) togetherWith
                            (slideOutHorizontally(tween(300)) { if (forward) -it else it } +
                                fadeOut(tween(180)))
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "sheet_content_animation"
                ) { pages ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        when (val page = pages.last()) {
                            is SheetDestination.Transactions -> {
                                SheetTopBar(
                                    title = stringResource(R.string.fragment_transaction),
                                    isSheetExpanded = isSheetExpanded
                                )
                                TransactionsScreen(
                                    viewModelFactory = viewModelFactory,
                                    onTransactionClick = { transaction ->
                                        sheetPages = sheetPages +
                                            SheetDestination.TransactionDetail(transaction.detailsUrl(context))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                            is SheetDestination.TransactionDetail -> {
                                SheetTopBar(
                                    title = stringResource(R.string.fragment_transaction_webview),
                                    isSheetExpanded = isSheetExpanded,
                                    onBack = { sheetPages = sheetPages.dropLast(1) }
                                )
                                TransactionWebView(
                                    url = page.url,
                                    onOpenUrl = { uri ->
                                        sheetPages = sheetPages +
                                            SheetDestination.TransactionDetail(uri.toString())
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .navigationBarsPadding()
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                TransactionsChartScreen(
                    viewModelFactory = viewModelFactory,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    shadowElevation = 4.dp
                ) {
                    Row {
                        EndSessionAction()
                        IconButton(onClick = onAccount) {
                            Icon(
                                painter = painterResource(R.drawable.ic_action_account),
                                contentDescription = stringResource(R.string.menu_item_account)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * The app bar of the bottom sheet. The sheet covers the screen's own actions when expanded,
 * so it shows them instead.
 */
@Composable
private fun SheetTopBar(
    title: String,
    isSheetExpanded: Boolean,
    onBack: (() -> Unit)? = null
) {
    SampleTopAppBar(
        title = title,
        onBack = onBack,
        windowInsets = WindowInsets(0),
        actions = { if (isSheetExpanded) EndSessionAction() }
    )
}

/**
 * Asks for the permission to show notifications, which Firebase Cloud Messaging needs.
 */
@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
