package com.wonderful.freshair.domain

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import com.wonderful.freshair.infrastructure.City
import java.math.BigDecimal
import java.math.RoundingMode

data class AirQualityIndex(val cityName : String, private val doubleIndex: Double) {
    val index: BigDecimal = BigDecimal(doubleIndex)
        .setScale(2, RoundingMode.HALF_UP)
}

class CityAirQualityService(
    private val cityGeocodingService: CityGeoCodingService,
    private val airQualityForecastService: AirQualityForecastService
) {
    fun averageIndex(city: City): Option<AirQualityIndex> = option.eager {
        val (_, _, coordinates) = cityGeocodingService.getGeoCoordinates(city).bind()
        val forecasts = airQualityForecastService.getAirQualityForecast(coordinates).bind()
        AirQualityIndex(
            city.name,
            forecasts.map { forecast -> forecast.index }.average()
        )
    }

}
