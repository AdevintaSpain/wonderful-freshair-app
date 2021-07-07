package com.wonderful.freshair.domain

import arrow.core.Option
import com.wonderful.freshair.infrastructure.City

data class GeoCoordinates(val lat: Double, val lon: Double)
data class CityGeoCoded(val name: String, val countryCode: String, val coordinates: GeoCoordinates)

interface CityGeoCodingService {
    fun getGeoCoordinates(city: City): Option<CityGeoCoded>
}
