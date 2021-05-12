package com.wonderful.freshair.domain

import com.wonderful.freshair.infrastructure.City
import arrow.core.Either
import com.wonderful.freshair.domain.error.ApplicationError

data class GeoCoordinates(val lat: Double, val lon: Double)
data class CityGeoCoded(val name: String, val countryCode: String, val coordinates: GeoCoordinates)

interface CityGeoCodingService {
    fun getGeoCoordinates(city: City): Either<ApplicationError, CityGeoCoded>
}
