package com.wonderful.freshair

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.compose
import arrow.core.identity
import arrow.core.invalid
import arrow.core.valid
import com.wonderful.freshair.TestTools.genNullableString
import com.wonderful.freshair.TestTools.genValidated
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.math.max

class ValidatedAsApplicativeTest : StringSpec({

    "Validated functor must preserve identity arrows" {
        checkAll(genNullableString) { a ->
            identity(a.valid()) shouldBe identity(a).valid()
            identity(a.invalid()) shouldBe identity(a).invalid()
        }
    }

    "Validated functor must preserve function composition" {
        checkAll(genNullableString) { a ->
            val f = { b: String? -> "PRE-APPENDED${b}" }
            val g = { b: String? -> "${b}POST-APPENDED" }
            g.compose(f)(a).valid() shouldBe a.valid().map(g.compose(f))
            g.compose(f)(a).invalid() shouldBe a.invalid().mapLeft(g.compose(f))
        }
    }

     "Validated applicative must satisfy identity law" {
        checkAll(genValidated) { validated ->
            val validatedId = Validated.lift<NonEmptyList<String>, String?, String?>{ a -> identity(a) }
            validatedId(validated) shouldBe validated
        }
    }

    "Validated applicative must satisfy homomorphism law" {
        checkAll(genNullableString) { a ->
            val f = { b: String? -> "PRE-APPENDED${b}" }
            val validatedF = Validated.lift<Nothing, String?, String?>(f)
            validatedF(a.valid()) shouldBe f(a).valid()
        }
    }

    "Validated applicative must satisfy interchange law" {
        checkAll(genNullableString) { a ->
            val f = { b: String? -> "PRE-APPENDED${b}" }
            val validatedF1 = Validated.lift<Nothing, String?, String?>(f)
            val validatedF2 = Validated.lift<Nothing, (String?) -> String?, String?> { f -> f(a) }
            validatedF1(a.valid()) shouldBe validatedF2(f.valid())
        }
    }

    "Validated applicative must satisfy composition law" {
        checkAll(genNullableString) { a ->
            val f = { b: String? -> "PRE-APPENDED${b}" }
            val g = { b: String? -> "${b}POST-APPENDED" }
            val validatedF1 = Validated.lift<Nothing, String?, String?>(f)
            val validatedF2 = Validated.lift<Nothing, String?, String?>(g)
            (validatedF2.compose(validatedF1))(a.valid()) shouldBe validatedF1(validatedF2(a.valid()))
        }
    }
})

object TestTools {
    private fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>): Arb<NonEmptyList<A>> =
        Arb.list(arb, 1..max(1, 15)).filter(List<A>::isNotEmpty).map(NonEmptyList.Companion::fromListUnsafe)

    private fun <L, R> Arb.Companion.validated(left: Arb<L>, right: Arb<R>): Arb<Validated<L, R>> {
        val failure: Arb<Validated<L, R>> = left.map { l -> l.invalid() }
        val success: Arb<Validated<L, R>> = right.map { r -> r.valid() }
        return Arb.choice(failure, success)
    }

    val genNullableString = Arb.string().orNull()
    val genValidated = Arb.validated(Arb.nonEmptyList(Arb.string()), genNullableString)
}