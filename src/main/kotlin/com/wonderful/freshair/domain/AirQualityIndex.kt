package com.wonderful.freshair.domain

import arrow.core.NonEmptyList
import java.math.BigDecimal
import java.math.RoundingMode

data class AirQualityIndex(val cityName : String, private val doubleIndex: Double) {
    val index: BigDecimal = BigDecimal(doubleIndex)
        .setScale(2, RoundingMode.HALF_UP)
}

fun NonEmptyList<AirQualityIndex>.minAirQualityIndex(): AirQualityIndex = this
    .foldLeft(this.head) { x, y -> if (x.index < y.index) x else y }