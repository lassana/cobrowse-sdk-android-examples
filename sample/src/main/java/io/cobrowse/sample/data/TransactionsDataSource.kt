package io.cobrowse.sample.data

import io.cobrowse.sample.data.model.Transaction
import java.io.IOException
import android.icu.text.MessageFormat
import android.os.Build
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TransactionsDataSource {
    fun generate(count: Int,
                 categories: Array<Transaction.Category>,
                 between: LocalDateRange): Result<List<Transaction>> {
        try {
            val allPossibleDates = between.iterator().asSequence().shuffled().toList()

            val transactions = (0 ..< count).map { i ->
                val category = categories.random()
                val randomDate = allPossibleDates.random()
                val date = LocalDateTime.of(randomDate.year,
                                            randomDate.month,
                                            randomDate.dayOfMonth,
                                            (0..23).random(),
                                            (0..59).random())
                val title = category.randomName()
                // TODO extract string resource instead of hard-coded "at"
                val subtitle = "${getOrdinal(date.dayOfMonth)} at ${DateTimeFormatter.ofPattern("HH:mm").format(date)}"
                val amount = category.amountRange.random() + (0..99).random() / 100.0

                Transaction(title = title,
                            subtitle = subtitle,
                            amount = amount,
                            date = date,
                            category = category)
            }
            return Result.Success(transactions.sortedBy { it.date }.toList())
        } catch (e: Throwable) {
            return Result.Error(IOException("Error while loading transaction data in", e))
        }
    }

    private fun getOrdinal(number: Int): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val formatter = MessageFormat("{0,ordinal}", Locale.getDefault())
            formatter.format(arrayOf(number))
        } else {
            number.toString()
        }
    }
}