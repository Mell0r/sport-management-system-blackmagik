val kotlinVersion = "1.6.10"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("application")
    id("org.jetbrains.compose") version "1.0.1"
}

group = "ru.senin.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven( "https://maven.pkg.jetbrains.space/public/p/compose/dev" )
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val exposedVersion: String by project

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
    .configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.cli.ExperimentalCli"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.ExperimentalStdlibApi"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

dependencies {

    // kotlin-stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    // kotlin-cli
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    // jetbrains compose
    implementation(compose.desktop.currentOs)
    // tinylog
    implementation("org.tinylog:tinylog-api-kotlin:2.3.2")
    implementation("org.tinylog:tinylog-impl:2.3.2")
    // kotlin-result
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.13")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.13")
    // h2 database
    implementation("com.h2database:h2:1.4.199")
    // jetbrains exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    // SLF4J (logging)
    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
    // kotlin-test
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.10")
    testImplementation("junit:junit:4.13.2")

    // workaround for skiko crash
    val osName = System.getProperty("os.name")
    val targetOs = when {
        osName == "Mac OS X" -> "macos"
        osName.startsWith("Win") -> "windows"
        osName.startsWith("Linux") -> "linux"
        else -> error("Unsupported OS: $osName")
    }

    val osArch = System.getProperty("os.arch")
    val targetArch = when (osArch) {
        "x86_64", "amd64" -> "x64"
        "aarch64" -> "arm64"
        else -> error("Unsupported arch: $osArch")
    }

    val target = "${targetOs}-${targetArch}"

    implementation("org.jetbrains.skiko:skiko-jvm-runtime-$target:0.5.3")


    // MockK
    testImplementation("io.mockk:mockk:1.12.2")
}

tasks.test {
    useJUnit()
    testLogging {
        outputs.upToDateWhen {false}
        showStandardStreams = true
    }
}

val nonDbTests = tasks.register<Test>("non-db tests") {
    group = "verification"
    useJUnit()
    filter {
        excludeTestsMatching("ru.emkn.kotlin.sms.db.*")
    }
}

application {
    mainClass.set("MainKt")
}
