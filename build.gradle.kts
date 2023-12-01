plugins {
    kotlin("jvm") version "1.9.20"
}

dependencies {
    implementation("org.assertj:assertj-core:3.11.1")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
