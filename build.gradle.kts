import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.5.30"
	application
}

group = "com.wonderful"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {

	// Arrow
	implementation("io.arrow-kt:arrow-core:1.0.1")

	// JSON
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

	// Junit 5
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

	// AssertK
	testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")

	// WireMock
	testImplementation("com.github.tomakehurst:wiremock-jre8:2.27.2")

	// Mockito
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")

	// Kotest
	testImplementation("io.kotest:kotest-property:4.6.3")
	testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
	testImplementation("io.kotest:kotest-assertions-core:4.6.3")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events(PASSED, SKIPPED, FAILED)
		exceptionFormat = FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
	}
}

application {
	mainClass.set("com.wonderful.freshair.FreshAirApplicationKt")
}
