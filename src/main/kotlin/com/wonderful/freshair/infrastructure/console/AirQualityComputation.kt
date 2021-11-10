package com.wonderful.freshair.infrastructure.console

import arrow.core.NonEmptyList
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.infrastructure.City

class AirQualityComputation(
    private val cityAirQualityService: CityAirQualityService
) {
    fun compute(cities: NonEmptyList<String>) =
        cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .forEach {  it.fold(
                { error -> println(error.description()) },
                { index -> println("${index.cityName} average air quality index forecast is ${index.index}")}
                )
            }
}