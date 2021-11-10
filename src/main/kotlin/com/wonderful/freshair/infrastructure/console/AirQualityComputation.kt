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
            .mapNotNull { cityAirQualityService.averageIndex(it).orNull() }
            .forEach { println("${it.cityName} average air quality index forecast is ${it.index}") }
}