package com.wonderful.freshair.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wonderful.freshair.infrastructure.City
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.math.RoundingMode

class CityAirQualityServiceTest {

    private lateinit var cityGeocodingService: CityGeoCodingService
    private lateinit var airQualityForecastService: AirQualityForecastService
    private lateinit var cityAirQualityService: CityAirQualityService

    @BeforeEach
    fun setUp() {
        cityGeocodingService = mock()
        airQualityForecastService = mock()
        cityAirQualityService = CityAirQualityService(cityGeocodingService, airQualityForecastService)
    }

    @Test
    fun `should compute city air quality index`() {
        val cityName = "Barcelona"
        val countryCode = "ES"
        val city = City(cityName, countryCode)
        val coordinates = GeoCoordinates(41.0, 2.0)
        val cityGeocoded = CityGeoCoded(cityName, countryCode, coordinates)
        val airQualityForecasts = listOf(
            AirQualityForecast(2),
            AirQualityForecast(1)
        )
        val expectedAverageAitQualityIndex = BigDecimal(1.50).setScale(2, RoundingMode.HALF_UP)
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(cityGeocoded)
        whenever(airQualityForecastService.getAirQualityForecast(coordinates)).thenReturn(airQualityForecasts)

        val airQualityIndex: AirQualityIndex = cityAirQualityService.averageIndex(city)

        assertThat(airQualityIndex.index).isEqualTo(expectedAverageAitQualityIndex)
    }
}