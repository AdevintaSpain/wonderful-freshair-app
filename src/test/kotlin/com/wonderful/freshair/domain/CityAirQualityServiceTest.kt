package com.wonderful.freshair.domain

import arrow.core.None
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wonderful.freshair.infrastructure.City
import assertk.assertions.isInstanceOf
import com.wonderful.freshair.domain.error.ApplicationError
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(cityGeocoded.right())
        whenever(airQualityForecastService.getAirQualityForecast(coordinates)).thenReturn(Some(airQualityForecasts))

        val airQualityIndex = cityAirQualityService.averageIndex(city)

        assertThat(airQualityIndex).isEqualTo(AirQualityIndex(cityName, 1.5).right())
    }

    @Test
    fun `should return left if city does not exist`() {
        val cityName = "Barcelona"
        val countryCode = "ES"
        val city = City(cityName, countryCode)
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(ApplicationError().left())

        assertThat(cityAirQualityService.averageIndex(city)).isInstanceOf(ApplicationError().left()::class)
    }

    @Test
    fun `should return left if pollution data is empty`() {
        val cityName = "Barcelona"
        val countryCode = "ES"
        val city = City(cityName, countryCode)
        val coordinates = GeoCoordinates(41.0, 2.0)
        val cityGeocoded = CityGeoCoded(cityName, countryCode, coordinates)
        whenever(cityGeocodingService.getGeoCoordinates(city)).thenReturn(cityGeocoded.right())
        whenever(airQualityForecastService.getAirQualityForecast(coordinates)).thenReturn(None)

        assertThat(cityAirQualityService.averageIndex(city)).isInstanceOf(ApplicationError().left()::class.java)
    }
}