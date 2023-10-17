package io.cobrowse.sample.data.model

import android.graphics.Color
import io.cobrowse.sample.R
import java.time.LocalDateTime

data class Transaction(
    val title: String,
    val subtitle: String,
    val amount: Double,
    val date: LocalDateTime,
    val category: Category) {

    enum class Category(
        val title: String,
        val amountRange: IntRange,
        val color: Int,
        val icon: Int) {

        Childcare("Childcare", (80..200), Color.rgb(82, 161, 136), R.mipmap.ic_child),
        Groceries("Groceries", (10..200), Color.rgb(82, 135, 161), R.mipmap.ic_shopping_card),
        Leisure("Leisure", (3..20), Color.rgb(92, 82, 161), R.mipmap.ic_movie),
        Utilities("Utilities", (60..200), Color.rgb(150, 161, 82), R.mipmap.ic_home_paper);

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