package com.wonderful.freshair.infrastructure

data class City(val name: String, val country: String) {
    companion object {
        private val regExp = Regex("""(?<city>\w+),(?<country>[a-zA-Z]{2})""")

        fun fromParameter(parameter: String): City {
            val expression = regExp.matchEntire(parameter)
            return City(
                name = expression!!.groups["city"]!!.value,
                country = expression.groups["country"]!!.value
            )
        }
    }
}