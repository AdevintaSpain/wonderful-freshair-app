package com.wonderful.freshair.infrastructure.console

import arrow.core.filterOption
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
            .filterOption()
            .maxWithOrNull(Comparator.comparing(AirQualityIndex::index))

        if (airQualityIndex != null)
            println("${airQualityIndex.cityName} has the cleaner air quality index.")
    }
}
