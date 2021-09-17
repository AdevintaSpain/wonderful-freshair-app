package com.wonderful.freshair.infrastructure.console

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.sequenceEither
import com.wonderful.freshair.domain.AirQualityIndex
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.infrastructure.City

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService
) {

    fun compare(cities: List<String>) {
        val airQualityIndex = cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .sequenceEither()

        when (airQualityIndex) {
            is Left -> println("Cannot compare air quality due to error ${airQualityIndex.value.javaClass.simpleName}.")
            is Right -> {
                val cleanestCity = airQualityIndex.value.maxWithOrNull(Comparator.comparing(AirQualityIndex::index))
                if (cleanestCity != null)
                    println("${cleanestCity.cityName} has the cleaner air quality index.")
                else
                    println("City list is empty.")
            }
        }
    }
}
