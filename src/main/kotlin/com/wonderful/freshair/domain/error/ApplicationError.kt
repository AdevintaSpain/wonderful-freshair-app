package com.wonderful.freshair.domain.error

import com.wonderful.freshair.infrastructure.City

sealed class ApplicationError {
    abstract val message: String
    fun description(): String = "Cannot compute air quality index due to $message"

    data class CityNotFoundError(val city: City) : ApplicationError() {
        override val message = "city of ${city.name},${city.country} not found"
    }
    object EmptyPollutionDataError : ApplicationError() {
        override val message = "empty pollution data"
    }
}