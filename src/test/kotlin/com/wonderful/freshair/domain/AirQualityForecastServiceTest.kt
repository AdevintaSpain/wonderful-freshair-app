package com.wonderful.freshair.domain

import arrow.core.left
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wonderful.freshair.infrastructure.api.OWMAirQualityForecastService
import assertk.assertions.isInstanceOf
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.wonderful.freshair.domain.error.ApplicationError
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirQualityForecastServiceTest {
    private val apiKey = "API_KEY"

    private lateinit var airQualityForecastService : AirQualityForecastService

    @BeforeAll
    fun init() {
        val server = WireMockServer(
            WireMockConfiguration.options()
                .port(WireMockConfiguration.DYNAMIC_PORT)
                .extensions(ResponseTemplateTransformer(true)))
        server.start()
        WireMock.configureFor(server.port())
        airQualityForecastService = OWMAirQualityForecastService(URL("http://localhost:${server.port()}"), apiKey)
    }

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `should get forecast for air quality`() {
        val lat = 41.3888
        val lon = 2.159
        val expectedAirQualityForecasts: List<AirQualityForecast> = listOf(
            AirQualityForecast(index = 2),
            AirQualityForecast(index = 2),
            AirQualityForecast(index = 1),
            AirQualityForecast(index = 1)
        )
        WireMock.stubFor(
            WireMock.get("/data/2.5/air_pollution/forecast?lat=$lat&lon=$lon&appid=$apiKey")
                .willReturn(
                    WireMock.aResponse()
                        .withBodyFile("barcelona-airpollution.json")
                        .withTransformerParameter("lat", lat)
                        .withTransformerParameter("lon", lon)
                )
        )

        val forecasts = airQualityForecastService.getAirQualityForecast(GeoCoordinates(lat, lon))

        assertThat(forecasts).isEqualTo(expectedAirQualityForecasts.right())
    }

    @Test
    fun `should return left if pollution data is empty`() {
        val lat = 41.3888
        val lon = 2.159
        WireMock.stubFor(
            WireMock.get("/data/2.5/air_pollution/forecast?lat=$lat&lon=$lon&appid=$apiKey")
                .willReturn(
                    WireMock.aResponse()
                        .withBodyFile("barcelona-airpollution-empty.json")
                        .withTransformerParameter("lat", lat)
                        .withTransformerParameter("lon", lon)
                )
        )

        val airQualityForecasts = airQualityForecastService.getAirQualityForecast(GeoCoordinates(lat, lon))

        assertThat(airQualityForecasts).isInstanceOf(ApplicationError().left()::class)
    }
}