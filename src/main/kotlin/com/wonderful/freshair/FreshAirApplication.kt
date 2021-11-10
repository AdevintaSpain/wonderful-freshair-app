package com.wonderful.freshair

import com.wonderful.freshair.domain.AirQualityForecastService
import com.wonderful.freshair.domain.CityAirQualityService
import com.wonderful.freshair.domain.CityGeoCodingService
import com.wonderful.freshair.infrastructure.api.OWMAirQualityForecastService
import com.wonderful.freshair.infrastructure.api.OWMCityGeoCodingService
import com.wonderful.freshair.infrastructure.console.AirQualityComparer
import com.wonderful.freshair.infrastructure.console.AirQualityComputation
import java.net.URL

fun main(args: Array<String>) {

  val baseUrl = URL("http://api.openweathermap.org")
  val apiKey = System.getenv("OWM_APIKEY")

  val cityGeoCodingService: CityGeoCodingService = OWMCityGeoCodingService(baseUrl, apiKey)
  val airQualityForecastService: AirQualityForecastService = OWMAirQualityForecastService(baseUrl, apiKey)
  val cityAirQualityService = CityAirQualityService(cityGeoCodingService, airQualityForecastService)
  val airQualityComputation = AirQualityComputation(cityAirQualityService)
  val airQualityComparer = AirQualityComparer(cityAirQualityService)

  val arguments = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
    if (elem.startsWith("-")) Pair(map + (elem to emptyList()), elem)
    else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
  }.first

  for (argument in arguments.keys) {
    val cities = arguments[argument] ?: emptyList()
    if (cities.isEmpty()) {
      println("City list should not be empty.")
      break
    }
    when (argument) {
      "--city" -> airQualityComputation.compute(cities)
      "--compare" -> airQualityComparer.compare(cities)
    }
  }
}

