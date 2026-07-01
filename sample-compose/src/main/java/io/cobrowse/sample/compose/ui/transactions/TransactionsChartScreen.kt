package io.cobrowse.sample.compose.ui.transactions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.compose.ui.CobrowseViewModelFactory
import kotlin.collections.isNotEmpty

@Composable
fun TransactionsChartScreen(
    viewModelFactory: CobrowseViewModelFactory,
    modifier: Modifier = Modifier
) {
    val viewModel: TransactionsChartViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 72.dp)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadData() }) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Balance",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = String.format("$%.2f", uiState.balance),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.recentTransactions.isNotEmpty()) {
                        TransactionsPieChart(
                            transactions = uiState.recentTransactions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    } else {
                        Text(
                            text = "No transactions this month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionsPieChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val categoryTotals = transactions
        .groupBy { it.category }
        .mapValues { (_, txns) -> txns.sumOf { it.amount } }
    
    val totalAmount = categoryTotals.values.sum()
    val colors = categoryTotals.keys.map { Color(it.color) }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(32.dp)
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val strokeWidth = radius * 0.3f
            val innerRadius = radius - strokeWidth

            var startAngle = -90f
            
            categoryTotals.values.forEachIndexed { index, amount ->
                val sweepAngle = (amount / totalAmount * 360).toFloat()
                
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(
                        (size.width - canvasSize) / 2f,
                        (size.height - canvasSize) / 2f
                    ),
                    size = Size(canvasSize, canvasSize),
                    style = Stroke(width = strokeWidth)
                )
                
                startAngle += sweepAngle
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Spent",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = String.format("$%.2f", totalAmount),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "this month",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

