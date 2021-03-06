package com.wonderful.freshair.infrastructure.console

import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wonderful.freshair.domain.AirQualityIndex
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.domain.error.ApplicationError.CityNotFoundError
import com.wonderful.freshair.infrastructure.City
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AirQualityComparerTest {

    private lateinit var cityAirQualityService: CityAirQualityService
    private lateinit var airQualityComparer: AirQualityComparer

    private val standardOut = System.out
    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
        cityAirQualityService = mock()
        airQualityComparer = AirQualityComparer(cityAirQualityService)
    }

    @AfterEach
    fun teardown() {
        System.setOut(standardOut)
    }

    @Test
    fun `should compare air quality index and get best city`() {
        val country = "ES"
        val barcelona = "Barcelona"
        val barcelonaIndex = 1.50
        val madrid = "Madrid"
        val madridIndex = 1.49
        val cities = nonEmptyListOf("$barcelona,$country", "$madrid,$country")
        whenever(cityAirQualityService.averageIndex(City(barcelona, country)))
            .thenReturn(AirQualityIndex(barcelona, barcelonaIndex).right())
        whenever(cityAirQualityService.averageIndex(City(madrid, country)))
            .thenReturn(AirQualityIndex(madrid, madridIndex).right())

        airQualityComparer.compare(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("$madrid has the cleaner air quality index.")
    }

    @Test
    fun `should fail if cannot compute air quality for a city`() {
        val country = "ES"
        val barcelona = "Barcelona"
        val barcelonaIndex = 1.50
        val madrid = "Madrid"
        val cities = nonEmptyListOf("$barcelona,$country", "$madrid,$country")
        whenever(cityAirQualityService.averageIndex(City(barcelona, country)))
            .thenReturn(AirQualityIndex(barcelona, barcelonaIndex).right())
        whenever(cityAirQualityService.averageIndex(City(madrid, country)))
            .thenReturn(CityNotFoundError(City(madrid, country)).left())

        airQualityComparer.compare(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("Cannot compute air quality index due to city of Madrid,ES not found")
    }


    @Test
    fun `should fail with all errors if cannot compute air quality for two city`() {
        val country = "ES"
        val barcelona = "Barcelon"
        val madrid = "Madri"
        val cities = nonEmptyListOf("$barcelona,$country", "$madrid,$country")
        whenever(cityAirQualityService.averageIndex(City(barcelona, country)))
            .thenReturn(CityNotFoundError(City(barcelona, country)).left())
        whenever(cityAirQualityService.averageIndex(City(madrid, country)))
            .thenReturn(CityNotFoundError(City(madrid, country)).left())

        airQualityComparer.compare(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("""
                Cannot compute air quality index due to city of Barcelon,ES not found
                Cannot compute air quality index due to city of Madri,ES not found
            """.trimIndent())
    }
}