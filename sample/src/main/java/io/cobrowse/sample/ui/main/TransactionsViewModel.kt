package io.cobrowse.sample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.cobrowse.sample.data.TransactionsRepository
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.ui.BaseViewModel

/**
 * View-Model for [TransactionsFragment].
 */
class TransactionsViewModel(private val repository: TransactionsRepository) : BaseViewModel() {

    private val _allTransactionsResult = MutableLiveData<List<Transaction>>()
    val allTransactionsResult: LiveData<List<Transaction>> = _allTransactionsResult

    fun loadAllTransactions() {
        _allTransactionsResult.value = repository.allTransactions()
    }
}