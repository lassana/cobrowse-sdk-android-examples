package io.cobrowse.sample.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.data.model.detailsUrl
import io.cobrowse.sample.databinding.FragmentMainBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory

/**
 * Fragment that displays the recent transactions statistics and also the list of transactions
 * made in the past months.
 */
class MainFragment : Fragment(), CobrowseIO.Redacted {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding
    private var menu: Menu? = null
    private var isTransactionListPresented = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(MainViewModel::class.java)
        viewModel.cobrowseDelegate.current.observe(this@MainFragment, Observer {
            updateUiWithSession(it)
        })
        viewModel.recentTransactionsResult.observe(this@MainFragment, Observer {
            updateChart(it)
        })
        viewModel.balanceResult.observe(this@MainFragment, Observer {
            updateBalance(it)
        })
        viewModel.allTransactionsResult.observe(this@MainFragment, Observer {
            updateTransactions(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                this@MainFragment.menu = menu
                updateUiWithSession(viewModel.cobrowseDelegate.current.value)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.end_cobrowse_session -> {
                        viewModel.endCobrowseSession()
                        true
                    }
                    R.id.accountFragment -> {
                        //return menuItem.onNavDestinationSelected(findNavController(view))
                        findNavController(view).navigate(R.id.action_mainFragment_to_accountFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        if (viewModel.recentTransactionsResult.isInitialized
            && viewModel.recentTransactionsResult.value?.isEmpty() == false) {
            updateChart(viewModel.recentTransactionsResult.value!!)
        } else {
            viewModel.loadRecentTransactions()
        }

        if (viewModel.balanceResult.isInitialized) {
            updateBalance(viewModel.balanceResult.value)
        } else {
            viewModel.loadBalance()
        }

        if (viewModel.allTransactionsResult.isInitialized
            && viewModel.allTransactionsResult.value?.isEmpty() == false) {
            updateTransactions(viewModel.allTransactionsResult.value!!)
        } else {
            viewModel.loadAllTransactions()
        }

        presentTransactionsList(binding.transactionsBottomSheet, savedInstanceState)
    }

    private fun presentTransactionsList(
        list: LinearLayoutCompat,
        savedInstanceState: Bundle?) {
        try {
            if (isTransactionListPresented || savedInstanceState != null) {
                // Fragment views are being constantly recreated during navigation,
                // and we want to animate the list only on its very first appearance.
                return
            }
            val duration = resources.getInteger(android.R.integer.config_longAnimTime).toLong() * 2
            list.postDelayed(duration) {
                with(BottomSheetBehavior.from(list)) {
                    halfExpandedRatio = 0.4f
                    state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }
        } finally {
            isTransactionListPresented = true
        }
    }

    private fun updateUiWithSession(session: io.cobrowse.Session?) {
        menu?.findItem(R.id.end_cobrowse_session).let {
            it?.isVisible = session?.isActive == true
        }
    }

    private fun updateBalance(balance: Double?) {
        if (balance != null)
            binding.textviewBalance.text = getString(R.string.transaction_amount, balance)
    }

    private fun updateChart(transactions: List<Transaction>) {
         updateChart(binding.chart, binding.textviewTotalSpent, transactions)
    }

    private fun updateChart(chart: PieChart,
                            total: TextView,
                            transactions: List<Transaction>) {
        val transactionsDictionary = transactions
            .groupBy { it.category }
            .mapValues { next -> next.value.sumOf { it.amount } }
        val pieEntries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        for (transaction in transactionsDictionary) {
            colors.add(transaction.key.color)

            val icon = getDrawable(requireContext(), transaction.key.icon)
            icon?.setTint(Color.WHITE)
            pieEntries.add(PieEntry(transaction.value.toFloat(), icon))
        }

        total.text = getString(R.string.transaction_amount, transactionsDictionary.values.sum())

        val pieDataSet = PieDataSet(pieEntries, "type")
        pieDataSet.valueTextSize = 12f
        pieDataSet.colors = colors
        pieDataSet.sliceSpace = 4f

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)

        chart.description = null
        chart.legend.isEnabled = false
        chart.isRotationEnabled = false
        chart.data = pieData
        chart.invalidate()
    }

    private fun updateTransactions(items: List<Transaction>) {
        with(binding.transactionsList) {
            val adapter = TransactionsRecyclerViewAdapter.from(items)
            adapter.setOnTransactionSelected(::onTransactionSelected)
            this.adapter = adapter
        }
    }

    private fun onTransactionSelected(transaction: Transaction) {
        findNavController(binding.root)
            .navigate(R.id.action_mainFragment_to_transactionWebViewFragment,
                      Bundle().also {
                          it.putString("url", transaction.detailsUrl(requireContext()))
                      })
    }

    override fun redactedViews(): MutableList<View> {
        val redacted = listOf<View>(
            binding.textviewBalance,
            binding.textviewTotalSpent)
            .toMutableList()
        binding.transactionsList.adapter.let {
            if (it is TransactionsRecyclerViewAdapter) {
                redacted.addAll(it.redactedViews())
            }
        }
        return redacted
    }
}