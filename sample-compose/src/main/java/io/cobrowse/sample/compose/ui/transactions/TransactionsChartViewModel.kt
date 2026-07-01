package io.cobrowse.sample.compose.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.cobrowse.sample.data.TransactionsRepository
import io.cobrowse.sample.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TransactionsChartUiState(
    val recentTransactions: List<Transaction> = emptyList(),
    val balance: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TransactionsChartViewModel(private val repository: TransactionsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsChartUiState())
    val uiState: StateFlow<TransactionsChartUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val allTransactions = repository.allTransactions()
            // Get transactions from current month
            val currentMonth = LocalDate.now()
            val recentTransactions = allTransactions.filter {
                it.date.year == currentMonth.year && it.date.month == currentMonth.month
            }

            val balance = (5000..15000).random().toDouble()

            _uiState.value = _uiState.value.copy(
                recentTransactions = recentTransactions,
                balance = balance,
                isLoading = false,
                errorMessage = null
            )
        }
    }
}

