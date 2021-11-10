package com.wonderful.freshair.infrastructure.console

import arrow.core.NonEmptyList
import arrow.core.computations.either
import arrow.core.getOrHandle
import arrow.core.sequenceEither
import com.wonderful.freshair.domain.AirQualityIndex
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.domain.error.ApplicationError
import com.wonderful.freshair.infrastructure.City

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService
) {

    fun compare(cities: NonEmptyList<String>): Unit = either.eager<ApplicationError, Unit> {
        val airQualityIndex = cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .sequenceEither()
            .bind()
        val cleanestCity = airQualityIndex.minWithOrNull(Comparator.comparing(AirQualityIndex::index))
        if (cleanestCity != null)
            println("${cleanestCity.cityName} has the cleaner air quality index.")
        else
            println("City list is empty.")
    }
    .getOrHandle { error -> println("Cannot compare air quality due to error ${error.javaClass.simpleName}.")}
}
