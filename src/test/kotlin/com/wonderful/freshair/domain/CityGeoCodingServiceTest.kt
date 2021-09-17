package com.wonderful.freshair.domain

import arrow.core.getOrHandle
import arrow.core.left
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.wonderful.freshair.domain.error.CityNotFoundError
import com.wonderful.freshair.infrastructure.City
import com.wonderful.freshair.infrastructure.api.OWMCityGeoCodingService
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CityGeoCodingServiceTest {
    private val apiKey = "API_KEY"

    private lateinit var cityGeoCodingService : CityGeoCodingService

    @BeforeAll
    fun init() {
        val server = WireMockServer(
            options()
                .port(WireMockConfiguration.DYNAMIC_PORT)
                .extensions(ResponseTemplateTransformer(true)))
        server.start()
        configureFor(server.port())
        cityGeoCodingService = OWMCityGeoCodingService(URL("http://localhost:${server.port()}"), apiKey)
    }

    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `should get geo coordinates for cities`() {
        val cityName = "Barcelona"
        val cityCountry = "ES"
        val city = City(name = cityName, country = cityCountry)
        val lat = 41.3888
        val lon = 2.159
        stubFor(
            get("/geo/1.0/direct?q=$cityName,$cityCountry&limit=1&appid=$apiKey")
                .willReturn(
                    aResponse()
                        .withBodyFile("barcelona-coordinates.json")
                        .withTransformerParameter("lat", lat)
                        .withTransformerParameter("lon", lon)
                )
        )

        cityGeoCodingService.getGeoCoordinates(city)
            .map {
                assertAll {
                    assertThat(it.name).isEqualTo(cityName)
                    assertThat(it.countryCode).isEqualTo(cityCountry)
                    assertThat(it.coordinates).isEqualTo(GeoCoordinates(lat, lon))
                }
            }.getOrHandle { fail("Should no be None.") }
    }

    @Test
    fun `should return left when city does not exist`() {
        val cityName = "Barcelona"
        val cityCountry = "ES"
        val city = City(name = cityName, country = cityCountry)
        stubFor(
            get("/geo/1.0/direct?q=$cityName,$cityCountry&limit=1&appid=$apiKey")
                .willReturn(
                    aResponse()
                        .withBody("""[]""")
                )
        )

        assertThat(cityGeoCodingService.getGeoCoordinates(city)).isEqualTo(CityNotFoundError.left())
    }
}