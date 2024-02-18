plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.liquibase.gradle") version "2.2.0"
}

group = "com.accmanagement"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("com.h2database:h2")
    liquibaseRuntime("com.h2database:h2")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("org.yaml:snakeyaml")
}
apply(plugin = "org.liquibase.gradle")

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
                "url" to "jdbc:h2:mem:testdb",
                "username" to "sa",
                "password" to "password"
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
