package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.sample.data.TransactionsRepository
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.ui.BaseViewModel

/**
 * View-Model for [MainFragment].
 */
class MainViewModel(private val repository: TransactionsRepository) : BaseViewModel() {

    private val _recentTransactionsResult = MutableLiveData<List<Transaction>>()
    val recentTransactionsResult: LiveData<List<Transaction>> = _recentTransactionsResult

    private val _balanceResult = MutableLiveData<Double>()
    val balanceResult: LiveData<Double> = _balanceResult

    private val _allTransactionsResult = MutableLiveData<List<Transaction>>()
    val allTransactionsResult: LiveData<List<Transaction>> = _allTransactionsResult

    fun loadRecentTransactions() {
        _recentTransactionsResult.value = repository.recentTransactions()
    }

    fun loadBalance() {
        _balanceResult.value = (1000..3000).random() + (0..99).random() / 100.0
    }

    fun loadAllTransactions() {
        _allTransactionsResult.value = repository.allTransactions()
    }
}