package com.wonderful.freshair.domain

import arrow.core.Either
import com.wonderful.freshair.domain.error.ApplicationError

data class AirQualityForecast(val index: Int)

interface AirQualityForecastService {
    fun getAirQualityForecast(coordinates: GeoCoordinates): Either<ApplicationError, List<AirQualityForecast>>
}
