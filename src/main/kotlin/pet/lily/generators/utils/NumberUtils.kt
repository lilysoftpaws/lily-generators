package pet.lily.generators.utils

import java.text.NumberFormat
import java.util.Locale

object NumberUtils {

    /**
     * Formats a [Number] as a currency string (e.g., "$1,000.00").
     *
     * @return The formatted currency string.
     */
    fun Number.formatCurrency(): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
        return currencyFormatter.format(this)
    }
}