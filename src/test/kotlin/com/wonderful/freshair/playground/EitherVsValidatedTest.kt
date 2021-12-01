package com.wonderful.freshair.playground

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.invalid
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.zip
import arrow.typeclasses.Semigroup
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.wonderful.freshair.playground.EitherVsValidatedTest.Error.FirstError
import com.wonderful.freshair.playground.EitherVsValidatedTest.Error.SecondError
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class EitherVsValidatedTest {

  sealed class Error {
    object FirstError : Error()
    object SecondError : Error()
  }

  private val computationOne: () -> Either<Error, Int> = {
    println("I am computation one and I rock!")
    FirstError.left()
  }

  private val computationTwo: () -> Either<Error, Int> = {
    println("I am computation two and I rock too!")
    SecondError.left()
  }

  private val standardOut = System.out
  private lateinit var outputStreamCaptor: ByteArrayOutputStream

  @BeforeEach
  fun setUp() {
    outputStreamCaptor = ByteArrayOutputStream()
    System.setOut(PrintStream(outputStreamCaptor))
  }

  @AfterEach
  fun tearDown() {
    System.setOut(standardOut)
  }

  @Test
  fun `should short-circuit computation for Either`() {
    val computationAll = computationOne().flatMap { computationTwo() }

    assertThat(computationAll).isEqualTo(FirstError.left())
    assertThat(outputStreamCaptor.toString().trim().lines()).containsExactly(
      "I am computation one and I rock!"
    )
  }

  @Test
  fun `should execute all chained computations for Validated`() {
    val computationAll = computationOne().toValidatedNel()
      .zip(Semigroup.nonEmptyList(), computationTwo().toValidatedNel()) { x, y -> x + y }

    assertThat(computationAll).isEqualTo(nonEmptyListOf(FirstError, SecondError).invalid())
    assertThat(outputStreamCaptor.toString().trim().lines()).containsExactly(
      "I am computation one and I rock!",
      "I am computation two and I rock too!"
    )
  }
}
