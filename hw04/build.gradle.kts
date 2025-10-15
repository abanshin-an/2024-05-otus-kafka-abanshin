plugins {
    id("java")
    id("application")
}

group="ru.otus.kafka"
version="1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:4.1.0")
    implementation("org.apache.kafka:kafka-streams:4.1.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.5.19")
}

application {
    mainClass.set("ru.otus.kafka.hw04.Application") // Replace with your actual main class
}

tasks.jar {
    from(configurations.compileClasspath.get().map { if (it.isDirectory()) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
