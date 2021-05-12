package com.wonderful.freshair.infrastructure.console

import com.wonderful.freshair.domain.AirQualityIndex
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.infrastructure.City
import arrow.core.getOrElse
import arrow.core.sequenceEither

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService
) {

    fun compare(cities: List<String>) {
        val airQualityIndex = cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .sequenceEither()
            .getOrElse { listOf() }
            .maxWithOrNull(Comparator.comparing(AirQualityIndex::index))

        if (airQualityIndex != null)
            println("${airQualityIndex.cityName} has the cleaner air quality index.")
        else
            println("Cannot compare air quality due to application error.")
    }
}
