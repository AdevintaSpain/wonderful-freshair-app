package com.wonderful.freshair.domain

import arrow.core.nonEmptyListOf
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class AirQualityIndexTest {

    @Test
    fun `should find minimum quality index in a non empty list`() {
        val barcelona = "Barcelona"
        val barcelonaIndex = 1.50
        val madrid = "Madrid"
        val madridIndex = 1.49
        val barcelonaQualityIndex = AirQualityIndex(barcelona, barcelonaIndex)
        val madridQualityIndex = AirQualityIndex(madrid, madridIndex)
        val indexes = nonEmptyListOf(barcelonaQualityIndex, madridQualityIndex)

        assertThat(indexes.minAirQualityIndex()).isEqualTo(madridQualityIndex)
    }
}