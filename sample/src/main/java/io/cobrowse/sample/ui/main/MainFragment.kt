package io.cobrowse.sample.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
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
import io.cobrowse.sample.data.getAndroidLogTag
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.data.model.detailsUrl
import io.cobrowse.sample.databinding.FragmentMainBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory
import io.cobrowse.sample.ui.RecyclerViewHeaderItemDecoration
import io.cobrowse.sample.ui.main.TransactionsRecyclerViewAdapter.ListItem.Companion.TYPE_MONTH_AND_YEAR
import io.cobrowse.sample.ui.onSizeChange


/**
 * Fragment that displays the recent transactions statistics and also the list of transactions
 * made in the past months.
 */
class MainFragment : Fragment(), CobrowseIO.Redacted {

    @Suppress("PrivatePropertyName")
    private val Any.TAG: String
        get() = javaClass.getAndroidLogTag()

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding
    private var isTransactionListPresented = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "FCM SDK and the app can post notifications")
        } else {
            Log.w(TAG, "FCM SDK and the app will not show notifications")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(MainViewModel::class.java)
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
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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

        binding.chart.onSizeChange {
            resizeChartSummary()
        }
        presentTransactionsList(binding.transactionsBottomSheet, savedInstanceState)
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "FCM SDK and the app can post notifications")
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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

    private fun updateBalance(balance: Double?) {
        if (balance != null)
            binding.textviewBalance.text = getString(R.string.transaction_amount, balance)
    }

    private fun updateChart(transactions: List<Transaction>) {
        val chart: PieChart = binding.chart
        val total: TextView = binding.textviewTotalSpent

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
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(getColor(requireContext(), android.R.color.transparent))
        chart.data = pieData
        chart.invalidate()
    }

    private fun resizeChartSummary() {
        val chart: PieChart = binding.chart
        val total: TextView = binding.textviewTotalSpent
        val totalHeader: TextView = binding.textviewTotalSpentHeader
        val totalFooter: TextView = binding.textviewTotalSpentFooter
        val balance: TextView = binding.textviewBalance

        val desiredWidth = ((chart.holeRadius / 100f) * (chart.radius) * 1.5f).toInt()
        val desiredHeight = desiredWidth / 3

        if (total.layoutParams is ConstraintLayout.LayoutParams) {
            total.layoutParams.width = desiredWidth
            total.layoutParams.height = desiredHeight
            total.requestLayout()
        }
        if (totalHeader.layoutParams is ConstraintLayout.LayoutParams) {
            totalHeader.layoutParams.width = desiredWidth
            totalHeader.requestLayout()
        }
        if (totalFooter.layoutParams is ConstraintLayout.LayoutParams) {
            totalFooter.layoutParams.width = desiredWidth
            totalFooter.requestLayout()
        }
        if (balance.layoutParams is ConstraintLayout.LayoutParams) {
            // Set the balance label height the same like in the "spent this month" height,
            // so their text sizes will be the same.
            balance.layoutParams.height = desiredHeight
            balance.requestLayout()
        }
    }

    private fun updateTransactions(items: List<Transaction>) {
        with(binding.transactionsList) {
            val adapter = TransactionsRecyclerViewAdapter.from(items)
            adapter.setOnTransactionSelected(::onTransactionSelected)
            this.adapter = adapter
            this.addItemDecoration(RecyclerViewHeaderItemDecoration(
                this,
                isHeader = { adapter.getItemViewType(it) == TYPE_MONTH_AND_YEAR }
            ))
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