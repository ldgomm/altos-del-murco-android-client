package com.premierdarkcoffee.tourism.altosdelmurco.util.extrension

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val usdFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
private val abbreviatedDateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

fun Double.priceText(): String = usdFormatter.format(this)

fun Date.abbreviatedDateText(): String = abbreviatedDateFormatter.format(this)
