import org.jetbrains.kotlin.gradle.utils.extendsFrom
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("com.github.ben-manes.versions") version "0.49.0"
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("java")
    id("com.vaadin")
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

repositories {
    mavenCentral()
    maven { setUrl("https://maven.vaadin.com/vaadin-prereleases") }
    maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
    maven { setUrl("https://repo.osgeo.org/repository/release/") }
}

configurations {
    developmentOnly
    runtimeClasspath.extendsFrom(developmentOnly)
}

dependencies {
    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("com.xdev-software:vaadin-maps-leaflet-flow:4.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // for spring to parse yaml config
    implementation("org.yaml:snakeyaml:2.2")
    implementation("eu.vaadinonkotlin:vok-framework:0.16.0")
    implementation("com.github.mvysny.karibudsl:karibu-dsl:2.1.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

//    implementation("org.geotools:gt-main:30.0")
//    implementation("javax.media:jai_core:1.1.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:24.2.1")
    }
}

