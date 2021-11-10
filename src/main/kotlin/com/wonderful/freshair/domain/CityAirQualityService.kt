package com.wonderful.freshair.domain

import arrow.core.Either
import arrow.core.computations.either
import com.wonderful.freshair.domain.error.ApplicationError
import com.wonderful.freshair.infrastructure.City

class CityAirQualityService(
    private val cityGeocodingService: CityGeoCodingService,
    private val airQualityForecastService: AirQualityForecastService
) {
    fun averageIndex(city: City): Either<ApplicationError, AirQualityIndex> = either.eager {
        val (_, _, coordinates) = cityGeocodingService.getGeoCoordinates(city).bind()
        val forecasts = airQualityForecastService.getAirQualityForecast(coordinates).bind()
        AirQualityIndex(
            city.name,
            forecasts.map { forecast -> forecast.index }.average()
        )
    }

}
