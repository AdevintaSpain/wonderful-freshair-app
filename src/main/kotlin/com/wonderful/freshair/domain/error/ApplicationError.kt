package com.wonderful.freshair.domain.error

sealed class ApplicationError
object CityNotFoundError: ApplicationError()
object EmptyPollutionDataError: ApplicationError()