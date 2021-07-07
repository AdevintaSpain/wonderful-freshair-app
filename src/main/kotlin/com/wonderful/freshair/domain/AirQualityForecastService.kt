package com.wonderful.freshair.domain

import arrow.core.Option

data class AirQualityForecast(val index: Int)

interface AirQualityForecastService {
    fun getAirQualityForecast(coordinates: GeoCoordinates): Option<List<AirQualityForecast>>
}
