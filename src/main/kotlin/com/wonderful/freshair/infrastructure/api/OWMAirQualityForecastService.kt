package com.wonderful.freshair.infrastructure.api

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wonderful.freshair.domain.AirQualityForecast
import com.wonderful.freshair.domain.AirQualityForecastService
import com.wonderful.freshair.domain.GeoCoordinates
import com.wonderful.freshair.domain.error.ApplicationError
import com.wonderful.freshair.domain.error.EmptyPollutionDataError
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

    override fun getAirQualityForecast(coordinates: GeoCoordinates): Either<ApplicationError, List<AirQualityForecast>> {
        val request = HttpRequest.newBuilder()
            .uri(URL(
                baseUrl,
                "data/2.5/air_pollution/forecast?lat=${coordinates.lat}&lon=${coordinates.lon}&appid=$apiKey"
                ).toURI())
            .GET()
            .build()

        val response: HttpResponse<String> = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        val forecasts = objectMapper.readValue<OMWAirQualityForecasts>(response.body())
            .list
            .map { AirQualityForecast(it.main.aqi) }

        return if (forecasts.isEmpty()) EmptyPollutionDataError.left() else forecasts.right()

    }

}
