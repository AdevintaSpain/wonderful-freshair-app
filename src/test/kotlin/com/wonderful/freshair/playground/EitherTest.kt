package com.wonderful.freshair.playground

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

data class MyAppValue(val value: Int)
data class MyAppError(val cause: String)

class EitherTest {

  @Test
  fun `should create right`() {
    val value = MyAppValue(123).right()

    when (value) {
      is Right -> {
        assertThat(value.value).isEqualTo(MyAppValue(123))
      }
      else -> throw AssertionError()
    }
  }

  @Test
  fun `should create left`() {
    val value = MyAppError("some error").left()

    when (value) {
      is Left -> {
        assertThat(value.value).isEqualTo(MyAppError("some error"))
      }
      else -> throw AssertionError()
    }
  }

  @Test
  fun `should create left catching exception`() {
    val error = Exception("some error")
    val value = Either.catch {
      throw error
    }

    when (value) {
      is Left -> {
        assertThat(value.value).isSameAs(error)
      }
      else -> throw AssertionError()
    }
  }

  @Test
  fun `should not catch fatal exceptions`() {
    assertThrows<Exception> {
      Either.catch {
        throw InterruptedException("some fatal error")
      }
    }
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7])
  fun `should flat map`(initialValue: Int) {
    val result = addOneIfLessThanFive(MyAppValue(initialValue))
      .flatMap { addOneIfLessThanFive(it) }
      .flatMap { addOneIfLessThanFive(it) }

    assertThat(result).isEqualTo(Right(MyAppValue(initialValue + 3)))
  }

  @ParameterizedTest
  @ValueSource(ints = [8, 9, 10])
  fun `should flat map and short-circuit`(initialValue: Int) {
    val result = addOneIfLessThanFive(MyAppValue(initialValue))
      .flatMap { addOneIfLessThanFive(it) }
      .flatMap { addOneIfLessThanFive(it) }

    assertThat(result).isEqualTo(Left(MyAppError("Cannot add one more!")))
  }

  @ParameterizedTest
  @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7])
  fun `should flat map with eager`(initialValue: Int) {
    val result = either.eager<MyAppError, MyAppValue> {
      val result1 = addOneIfLessThanFive(MyAppValue(initialValue)).bind()
      val result2 = addOneIfLessThanFive(result1).bind()
      addOneIfLessThanFive(result2).bind()
    }

    assertThat(result).isEqualTo(Right(MyAppValue(initialValue + 3)))
  }

  @ParameterizedTest
  @ValueSource(ints = [8, 9, 10])
  fun `should flat map with eager and short-circuit`(initialValue: Int) {
    val result = either.eager<MyAppError, MyAppValue> {
      val result1 = addOneIfLessThanFive(MyAppValue(initialValue)).bind()
      val result2 = addOneIfLessThanFive(result1).bind()
      addOneIfLessThanFive(result2).bind()
    }

    assertThat(result).isEqualTo(Left(MyAppError("Cannot add one more!")))
  }

  private fun addOneIfLessThanFive(value: MyAppValue): Either<MyAppError, MyAppValue> =
    if (value.value < 10) {
      println("Adding one to ${value.value}")
      MyAppValue(value.value + 1).right()
    } else {
      println("Cannot add one to ${value.value}")
      MyAppError("Cannot add one more!").left()
    }
}

