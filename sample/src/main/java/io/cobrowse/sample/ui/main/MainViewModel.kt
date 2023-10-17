package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.sample.data.TransactionsRepository
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.ui.BaseViewModel

class MainViewModel(private val repository: TransactionsRepository) : BaseViewModel() {

    private val _transactionsResult = MutableLiveData<List<Transaction>>()
    val transactionsResult: LiveData<List<Transaction>> = _transactionsResult

    private val _balanceResult = MutableLiveData<Double>()
    val balanceResult: LiveData<Double> = _balanceResult

    fun loadTransactions() {
        _transactionsResult.value = repository.recentTransactions()
    }

    fun loadBalance() {
        _balanceResult.value = (1000..3000).random() + (0..99).random() / 100.0
    }
}