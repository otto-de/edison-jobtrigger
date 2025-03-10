import com.gorylenko.GitPropertiesPluginExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("idea")
    id("java")
    id("project-report")

    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
}

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    mavenLocal()
}

group = "de.otto.edison"
base.archivesName.set("edison-jobtrigger")
version = "3.0.3"

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
    exclude(group = "log4j", module = "log4j")
    exclude(module = "spring-boot-starter-tomcat")
}

dependencies {
    val edisonrelease = "3.4.2"
    val logbackVersion = "1.5.16"

    // edison
    implementation("de.otto.edison:edison-core:${edisonrelease}")

    // bootstrap
    //Don't forget to also update the links in the html templates if you change this!
    implementation("org.webjars:jquery:3.7.1")
    implementation("org.webjars:bootstrap:5.3.3")
    //Don't forget to also update the links in the html templates if you change this!

    // guava
    implementation("com.google.guava:guava:33.4.0-jre")

    // logging
    implementation("ch.qos.logback:logback-core:${logbackVersion}")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")

    // spring
    implementation("org.springframework.vault:spring-vault-core:3.1.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.2.Final")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // jetty
    implementation("org.springframework.boot:spring-boot-starter-jetty")

    // gson
    implementation("com.google.code.gson:gson:2.11.0")

    // asyncHttp
    implementation("org.asynchttpclient:async-http-client:3.0.1")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("de.otto.edison:edison-testsupport:${edisonrelease}")

    testImplementation("org.eclipse.jetty:jetty-server:12.0.16")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
}

tasks {
    dependencyUpdates {
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }

    withType<ProcessResources> {
        doLast {
            val resourcesDir = project.sourceSets.main.get().output.resourcesDir
            resourcesDir!!.mkdirs()

            val versionProperties = File(resourcesDir, "version.properties")
            val version = version
            val commit = getCommitHash()
            versionProperties.writeText("\nedison.status.vcs.version = $version\nedison.status.vcs.commit = $commit")
        }
    }

    withType<Test> {
        useJUnitPlatform {
            // Show test results.
            testLogging {
                showStackTraces = true
                showCauses = true
                displayGranularity = -1
                exceptionFormat = TestExceptionFormat.FULL
                events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            }
            excludeEngines("junit-vintage")
        }
        jvmArgs("-XX:+AllowRedefinitionToAddDeleteMethods")
    }
}

fun getCommitHash(): String {
    val env = System.getenv()
    return env["COMMIT"] ?: "unknown"
}

configure<GitPropertiesPluginExtension> {
    dateFormat = "yyyy-MM-dd'T'HH:mmZ"
    dateFormatTimeZone = "CET"
}

fun isNonStable(version: String): Boolean {
    val regex = """(?i).*[.-](alpha|beta|rc|cr|m|preview|b|ea|pr)[.\d-+]*""".toRegex()
    return regex.matches(version)
}