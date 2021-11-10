package com.wonderful.freshair.infrastructure.console

import arrow.core.NonEmptyList
import arrow.core.sequenceValidated
import arrow.typeclasses.Semigroup
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.domain.minAirQualityIndex
import com.wonderful.freshair.infrastructure.City

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService
) {

    fun compare(cities: NonEmptyList<String>): Unit =
        cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it).toValidatedNel() }
            .sequenceValidated(Semigroup.nonEmptyList())
            .fold(
                { errors -> errors.forEach { println(it.description()) } },
                { indexes -> println("${indexes.minAirQualityIndex().cityName} has the cleaner air quality index.")}
            )
}
