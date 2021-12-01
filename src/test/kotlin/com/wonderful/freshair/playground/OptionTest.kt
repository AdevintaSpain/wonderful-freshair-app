package com.wonderful.freshair.playground

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import arrow.core.toOption
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OptionTest {

  @Test
  fun `should convert null to None`() {
    val value: String? = null
    assertThat(value.toOption()).isEqualTo(None)
  }

  @Test
  fun `should convert non null to Some`() {
    val value: String? = "hello!"
    assertThat(value.toOption()).isEqualTo(Some("hello!"))
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7])
  fun `should flat map`(initialValue: Int) {
    val value = addOneIfLessThanFive(initialValue)
      .flatMap { addOneIfLessThanFive(it) }
      .flatMap { addOneIfLessThanFive(it) }
    assertThat(value).isEqualTo(Some(initialValue + 3))
  }

  @ParameterizedTest
  @ValueSource(ints = [8, 9, 10])
  fun `should flat map and short-circuit`(initialValue: Int) {
    val value = addOneIfLessThanFive(initialValue)
      .flatMap { addOneIfLessThanFive(it) }
      .flatMap { addOneIfLessThanFive(it) }
    assertThat(value).isEqualTo(None)
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7])
  fun `should flat map with eager`(initialValue: Int) {
    val value = option.eager<Int> {
      val value1 = addOneIfLessThanFive(initialValue).bind()
      val value2 = addOneIfLessThanFive(value1).bind()
      addOneIfLessThanFive(value2).bind()
    }
    assertThat(value).isEqualTo(Some(initialValue + 3))
  }

  @ParameterizedTest
  @ValueSource(ints = [8, 9, 10])
  fun `should flat map with eager and short-circuit`(initialValue: Int) {
    val value = option.eager<Int> {
      val value1 = addOneIfLessThanFive(initialValue).bind()
      val value2 = addOneIfLessThanFive(value1).bind()
      addOneIfLessThanFive(value2).bind()
    }
    assertThat(value).isEqualTo(None)
  }

  private fun addOneIfLessThanFive(value: Int): Option<Int> =
    if (value < 10) {
      println("Adding one to $value")
      Some(value + 1)
    } else {
      println("Cannot add one to $value")
      None
    }
}
