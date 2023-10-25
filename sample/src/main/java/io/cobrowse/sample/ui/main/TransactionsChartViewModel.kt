package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.sample.data.TransactionsRepository
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.ui.BaseViewModel

/**
 * View-Model for [TransactionsChartFragment].
 */
class TransactionsChartViewModel(private val repository: TransactionsRepository) : BaseViewModel() {

    private val _recentTransactionsResult = MutableLiveData<List<Transaction>>()
    val recentTransactionsResult: LiveData<List<Transaction>> = _recentTransactionsResult

    private val _balanceResult = MutableLiveData<Double>()
    val balanceResult: LiveData<Double> = _balanceResult

    fun loadRecentTransactions() {
        _recentTransactionsResult.value = repository.recentTransactions()
    }

    fun loadBalance() {
        _balanceResult.value = (1000..3000).random() + (0..99).random() / 100.0
    }
}