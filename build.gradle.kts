buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
    }
}

plugins {
    kotlin("android") version "1.7.0" apply false
}

fun String.execute(currentWorkingDir: File = file("./")): String {
    val byteOut = java.io.ByteArrayOutputStream()
    exec {
        workingDir = currentWorkingDir
        commandLine = split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

val verCode by extra
var verName by extra("")

val minSdker by extra(23)
val targetSdker by extra(29)
val compileSdker by extra(33)
val ndker by extra("25.0.8775105")
val javaer by extra(Javaersion.ERSION_11)

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
