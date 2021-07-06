package com.wonderful.freshair.domain

data class AirQualityForecast(val index: Int)

interface AirQualityForecastService {
    fun getAirQualityForecast(coordinates: GeoCoordinates): List<AirQualityForecast>
}
