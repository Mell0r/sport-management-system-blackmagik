val kotlin_version = "1.6.10"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("application")
    id("org.jetbrains.compose") version "1.0.1-rc2"
}

group = "ru.senin.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven( "https://maven.pkg.jetbrains.space/public/p/compose/dev" )
    google()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
    .configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.cli.ExperimentalCli"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.ExperimentalStdlibApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    implementation("org.tinylog:tinylog-api-kotlin:2.3.2")
    implementation("org.tinylog:tinylog-impl:2.3.2")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.13")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.13")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.10")
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnit()
    testLogging {
        outputs.upToDateWhen {false}
        showStandardStreams = true
    }
}

application {
    mainClass.set("MainKt")
}
