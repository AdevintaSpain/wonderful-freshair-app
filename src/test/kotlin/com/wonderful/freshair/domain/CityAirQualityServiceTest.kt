package com.wonderful.freshair.domain

import arrow.core.None
import arrow.core.Some
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
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
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(Some(cityGeocoded))
        whenever(airQualityForecastService.getAirQualityForecast(coordinates)).thenReturn(Some(airQualityForecasts))

        val airQualityIndex = cityAirQualityService.averageIndex(city)

        assertThat(airQualityIndex).isEqualTo(Some(AirQualityIndex(cityName, 1.5)))
    }

    @Test
    fun `should return none if city does not exist`() {
        val cityName = "Barcelona"
        val countryCode = "ES"
        val city = City(cityName, countryCode)
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(None)

        assertThat(cityAirQualityService.averageIndex(city)).isEqualTo(None)
    }

    @Test
    fun `should return none if pollution data is empty`() {
        val cityName = "Barcelona"
        val countryCode = "ES"
        val city = City(cityName, countryCode)
        val coordinates = GeoCoordinates(41.0, 2.0)
        val cityGeocoded = CityGeoCoded(cityName, countryCode, coordinates)
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(Some(cityGeocoded))
        whenever(airQualityForecastService.getAirQualityForecast(coordinates)).thenReturn(None)

        assertThat(cityAirQualityService.averageIndex(city)).isEqualTo(None)
    }
}