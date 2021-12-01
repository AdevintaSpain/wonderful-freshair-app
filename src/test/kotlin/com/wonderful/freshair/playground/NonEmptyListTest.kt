package com.wonderful.freshair.playground

import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Some
import arrow.core.nonEmptyListOf
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NonEmptyListTest {

  @Test
  fun `should create None from emptyList`() {
    assertThat(NonEmptyList.fromList<String>(emptyList()))
          .isEqualTo(None)
  }

  @Test
  fun `should create Some from non empty listOf`() {
    assertThat(NonEmptyList.fromList(listOf("a", "b", "c")))
          .isEqualTo(Some(nonEmptyListOf("a", "b", "c")))
  }

  @Test
  fun `should fail creating NonEmptyList from emptyList`() {
    assertThrows<Exception> {
      NonEmptyList.fromListUnsafe<String>(emptyList())
    }
  }

  @Test
  fun `should create NonEmptyList from non empty listOf`() {
    assertThat(NonEmptyList.fromListUnsafe(listOf("a", "b", "c")))
          .isEqualTo(nonEmptyListOf("a", "b", "c"))
  }

  @Test
  fun `should return head`() {
    assertThat(nonEmptyListOf("a", "b", "c").head)
          .isEqualTo("a")
  }

  @Test
  fun `should return tail`() {
    assertThat(nonEmptyListOf("a", "b", "c").tail)
          .isEqualTo(listOf("b", "c"))
  }
}
