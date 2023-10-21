package io.cobrowse.sample.data.model

import android.content.Context
import android.graphics.Color
import android.icu.text.MessageFormat
import android.os.Build
import io.cobrowse.sample.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Data class that represents a single user financial transaction.
 */
data class Transaction(
    val title: String,
    val amount: Double,
    val date: LocalDateTime,
    val category: Category) {

    enum class Category(
        val title: String,
        val amountRange: IntRange,
        val color: Int,
        val icon: Int) {

        Childcare("Childcare", (80..200), Color.rgb(82, 161, 136), R.drawable.ic_child),
        Groceries("Groceries", (10..200), Color.rgb(82, 135, 161), R.drawable.ic_shopping_card),
        Leisure("Leisure", (3..20), Color.rgb(92, 82, 161), R.drawable.ic_movie),
        Utilities("Utilities", (60..200), Color.rgb(150, 161, 82), R.drawable.ic_home_paper);

        fun randomName (): String {
            return when (this) {
                Childcare -> listOf("Bright Horizons", "KinderCare", "Tutor Time", "Busy Bees").random()
                Groceries -> listOf("Tesco", "Walmart", "Kroger", "Sainsbury's", "Asda", "Morrisons").random()
                Leisure -> listOf("Netflix", "Amazon Prime Video", "Hulu", "Now TV", "Sky", "Disney+").random()
                Utilities -> listOf("British Gas", "EDF Energy", "NPower", "EON", "SSE", "Verizon", "AT&T", "Comcast").random()
            }
        }
    }
}

fun Transaction.subtitle(context: Context): String {

    fun getOrdinal(number: Int): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val formatter = MessageFormat("{0,ordinal}", Locale.getDefault())
            formatter.format(arrayOf(number))
        } else {
            number.toString()
        }
    }

    return context.getString(R.string.transaction_detail_subtitle,
        getOrdinal(date.dayOfMonth),
        DateTimeFormatter.ofPattern("HH:mm").format(date))
}

fun LocalDate.transactionGroupHeader() : String {
    return DateTimeFormatter.ofPattern("MMMM y").format(this)
}