package com.wonderful.freshair.infrastructure.console

import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.infrastructure.City
import arrow.core.getOrElse
import arrow.core.sequenceEither

class AirQualityComputation(
    private val cityAirQualityService: CityAirQualityService
) {
    fun compute(cities: List<String>) {
        cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .sequenceEither()
            .getOrElse { listOf() }
            .forEach { println("${it.cityName} average air quality index forecast is ${it.index}") }
    }
}