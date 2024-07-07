plugins {
    kotlin("jvm") version "2.0.0"
    id("app.cash.sqldelight") version "2.0.2"
    application
}

group = "ninja.bryansills"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "ninja.bryansills.jvmnodelight.MainKt"
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("ninja.bryansills.jvmnodelight")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            verifyMigrations.set(true)
            generateAsync.set(true)
            deriveSchemaFromMigrations.set(true)
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.0.2")
        }
    }
}