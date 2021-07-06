package com.wonderful.freshair.infrastructure.api

import com.wonderful.freshair.domain.AirQualityForecast
import com.wonderful.freshair.domain.AirQualityForecastService
import com.wonderful.freshair.domain.GeoCoordinates
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class OMWAirQuality(val aqi: Int)
class OMWAirQualityForecast(val main: OMWAirQuality)
class OMWAirQualityForecasts(val list: List<OMWAirQualityForecast>)

class OWMAirQualityForecastService(
    private val baseUrl: URL,
    private val apiKey: String) : AirQualityForecastService {

    private var objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun getAirQualityForecast(coordinates: GeoCoordinates): List<AirQualityForecast> {
        val request = HttpRequest.newBuilder()
            .uri(URL(
                baseUrl,
                "data/2.5/air_pollution/forecast?lat=${coordinates.lat}&lon=${coordinates.lon}&appid=$apiKey"
                ).toURI())
            .GET()
            .build()

        val response: HttpResponse<String> = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        val airQualityForecasts: OMWAirQualityForecasts = objectMapper.readValue(response.body())

        return airQualityForecasts
            .list
            .map { AirQualityForecast(it.main.aqi) }
    }

}
