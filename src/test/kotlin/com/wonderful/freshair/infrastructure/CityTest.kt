package com.wonderful.freshair.infrastructure

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class CityTest {

    @Test
    fun `should parse input into City type`() {
        val cityName = "Barcelona"
        val countryCode = "ES"
        val cityParameter = "$cityName,$countryCode"

        assertThat(City.fromParameter(cityParameter)).isEqualTo(City(cityName, countryCode))
    }
}