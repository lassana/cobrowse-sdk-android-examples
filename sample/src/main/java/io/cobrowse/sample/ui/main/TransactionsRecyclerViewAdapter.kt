package io.cobrowse.sample.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.cobrowse.sample.R
import io.cobrowse.sample.data.model.Transaction
import io.cobrowse.sample.data.model.subtitle
import io.cobrowse.sample.data.model.transactionGroupHeader
import io.cobrowse.sample.databinding.CellTransactionBinding
import io.cobrowse.sample.databinding.CellTransactionMonthBinding
import io.cobrowse.sample.ui.main.TransactionsRecyclerViewAdapter.ListItem.Companion.TYPE_MONTH_AND_YEAR
import io.cobrowse.sample.ui.main.TransactionsRecyclerViewAdapter.ListItem.Companion.TYPE_TRANSACTION
import java.time.LocalDate

/**
 * [RecyclerView.Adapter] that can display a [io.cobrowse.sample.data.model.Transaction].
 */
class TransactionsRecyclerViewAdapter(
    private val values: List<ListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MONTH_AND_YEAR -> MonthAndYearViewHolder(
                CellTransactionMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_TRANSACTION -> TransactionViewHolder(
                CellTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw RuntimeException("Unknown cell type: $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_MONTH_AND_YEAR -> (holder as MonthAndYearViewHolder)
                .rebind(item = values[position] as MonthAndYearItem)
            TYPE_TRANSACTION -> (holder as TransactionViewHolder)
                .rebind(item = values[position] as TransactionItem)
            else -> throw RuntimeException("Unknown cell type: ${holder.itemViewType}")
        }
    }

    override fun getItemViewType(position: Int): Int = values[position].type

    override fun getItemCount(): Int = values.size

    inner class MonthAndYearViewHolder(private val binding: CellTransactionMonthBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun rebind(item: MonthAndYearItem) {
            binding.textviewMonth.text = item.monthAndYear
        }
    }

    inner class TransactionViewHolder(private val binding: CellTransactionBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun rebind(item: TransactionItem) {
            binding.root.context.let {
                // TODO implement drawable cache
                with(ContextCompat.getDrawable(it, item.transaction.category.icon)) {
                    this?.setTint(item.transaction.category.color)
                    binding.imageviewTransactionLogo.setImageDrawable(this)
                }
                binding.textviewTransactionName.text = item.transaction.title
                binding.textviewTransactionDate.text = item.transaction.subtitle(it)
                binding.textviewTransactionAmount.text =
                    it.getString(R.string.transaction_amount, item.transaction.amount)
            }

        }
    }

    open class ListItem(val type: Int) {
        companion object {
            const val TYPE_MONTH_AND_YEAR = 0
            const val TYPE_TRANSACTION = 1
        }
    }

    class MonthAndYearItem(val monthAndYear: String) : ListItem(TYPE_MONTH_AND_YEAR)

    class TransactionItem(var transaction: Transaction) : ListItem(TYPE_TRANSACTION)

    companion object {
        fun from(items: List<Transaction>): TransactionsRecyclerViewAdapter {
            val groupedMapMap: Map<LocalDate, List<Transaction>> = items.groupBy {
                it.date.withDayOfMonth(1).toLocalDate()
            }

            val consolidatedList = mutableListOf<ListItem>()
            groupedMapMap.keys
                .sortedDescending()
                .forEach { next: LocalDate ->
                    consolidatedList.add(MonthAndYearItem(next.transactionGroupHeader()))
                    groupedMapMap[next]
                        ?.sortedByDescending { it.date }
                        ?.forEach {
                            consolidatedList.add(TransactionItem(it))
                        }
                }

            return TransactionsRecyclerViewAdapter(consolidatedList)
        }
    }
}