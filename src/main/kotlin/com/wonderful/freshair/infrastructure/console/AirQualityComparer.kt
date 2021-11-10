package com.wonderful.freshair.infrastructure.console

import arrow.core.NonEmptyList
import arrow.core.sequenceEither
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.domain.minAirQualityIndex
import com.wonderful.freshair.infrastructure.City

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService
) {

    fun compare(cities: NonEmptyList<String>): Unit =
        cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .sequenceEither()
            .fold(
                { error -> println("Cannot compare air quality due to error ${error.javaClass.simpleName}.") },
                { indexes -> println("${indexes.minAirQualityIndex().cityName} has the cleaner air quality index.")}
            )
}
