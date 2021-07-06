package com.wonderful.freshair.domain

import com.wonderful.freshair.infrastructure.City
import java.math.BigDecimal
import java.math.RoundingMode

class AirQualityIndex(val cityName : String, doubleIndex: Double) {
    val index: BigDecimal = BigDecimal(doubleIndex)
        .setScale(2, RoundingMode.HALF_UP)
}

class CityAirQualityService(
  private val cityGeocodingService: CityGeoCodingService,
  private val airQualityForecastService: AirQualityForecastService
) {
    fun averageIndex(city: City): AirQualityIndex {
        val (name, _, coordinates) = cityGeocodingService.getGeoCoordinates(city)
        val airQualityForecasts = airQualityForecastService.getAirQualityForecast(coordinates)

        return AirQualityIndex(name, airQualityForecasts
            .map { it.index }
            .average())
    }

}
