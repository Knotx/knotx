import org.apache.tools.ant.filters.ReplaceTokens
import org.nosphere.apache.rat.RatTask
import java.util.Date

/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  id("java-library")
  id("maven-publish")
  id("org.nosphere.apache.rat") version "0.4.0"
}

description = "Knot.x Core"

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = "io.knotx"
      artifactId = "knotx-core"

      from(components["java"])

      pom {
        name.set("Knot.x Core")
        description.set("Knot.x - efficient, high-performance and scalable integration platform for modern websites")
        url.set("http://knotx.io")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("tomaszmichalak")
            name.set("Tomasz Michalak")
            email.set("tomasz.michalak@cognifide.com")
          }
          developer {
            id.set("skejven")
            name.set("Maciej Laskowski")
            email.set("maciej.laskowski@cognifide.com")
          }
          developer {
            id.set("marcinczeczko")
            name.set("Marcin Czeczko")
            email.set("marcin.czeczko@cognifide.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/Cognifide/knotx.git")
          developerConnection.set("scm:git:ssh://github.com:Cognifide/knotx.git")
          url.set("http://knotx.io")
        }
      }
    }
    repositories {
      maven {
        val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
        url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        url = uri("$buildDir/repo")
      }
    }
  }
}

dependencies {

  annotationProcessor(platform("io.knotx:knotx-dependencies:${project.version}"))
  annotationProcessor(group = "io.vertx", name = "vertx-codegen")
  annotationProcessor(group = "io.vertx", name = "vertx-service-proxy", classifier = "processor")
  annotationProcessor(group = "io.vertx", name = "vertx-rx-java2-gen")

  implementation(platform("io.knotx:knotx-dependencies:${project.version}"))
  implementation(group = "io.vertx", name = "vertx-core")
  implementation(group = "io.vertx", name = "vertx-service-proxy")
  implementation(group = "io.vertx", name = "vertx-rx-java2")
  implementation(group = "io.vertx", name = "vertx-codegen")
  implementation(group = "io.vertx", name = "vertx-config")
  implementation(group = "io.vertx", name = "vertx-config-hocon")
  implementation(group = "io.vertx", name = "vertx-web")
  implementation(group = "io.vertx", name = "vertx-web-api-contract")
  implementation(group = "io.vertx", name = "vertx-web-client")
  implementation(group = "io.vertx", name = "vertx-service-discovery")
  implementation(group = "io.vertx", name = "vertx-circuit-breaker")
  implementation(group = "io.vertx", name = "vertx-hazelcast")

  implementation(group = "ch.qos.logback", name = "logback-classic")
  implementation(group = "com.google.guava", name = "guava")
  implementation(group = "commons-io", name = "commons-io")
  implementation(group = "org.apache.commons", name = "commons-lang3")
  implementation(group = "org.jsoup", name = "jsoup")
  implementation(group = "com.typesafe", name = "config")
  implementation(group = "commons-collections", name = "commons-collections")

  testImplementation(group = "io.knotx", name = "knotx-junit5")
  testImplementation(group = "io.vertx", name = "vertx-junit5")
  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api")
  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params")
  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-migrationsupport")
  testImplementation(group = "io.vertx", name = "vertx-unit")
  testImplementation(group = "com.github.stefanbirkner", name = "system-rules") {
    exclude(module = "junit-dep")
  }
  testImplementation(group = "com.googlecode.zohhak", name = "zohhak")
  testImplementation(group = "uk.co.datumedge", name = "hamcrest-json")
  testImplementation(group = "org.hamcrest", name = "hamcrest-all")

  testImplementation(group = "io.vertx", name = "vertx-core")
  testImplementation(group = "io.vertx", name = "vertx-web")
  testImplementation(group = "io.vertx", name = "vertx-web-api-contract")
  testImplementation(group = "io.vertx", name = "vertx-web-client")
  testImplementation(group = "io.vertx", name = "vertx-rx-java2")
  testImplementation(group = "io.vertx", name = "vertx-service-proxy")
  testImplementation(group = "io.vertx", name = "vertx-config")
  testImplementation(group = "io.vertx", name = "vertx-config-hocon")
  testImplementation(group = "io.vertx", name = "vertx-hazelcast")
}

tasks.withType<JavaCompile>().configureEach {
  with(options) {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    encoding = "UTF-8"
  }
}

sourceSets {
  main {
    java {
      setSrcDirs(listOf("/src/main/java", "src/main/generated"))
    }
  }
}

fun timestamp(): Long {
  return Date().time
}

tasks {

  register<Copy>("templateClassProcessing") {
    val tokens = mapOf("project.version" to project.version, "build.timestamp" to "${timestamp()}")
    inputs.properties(tokens)

    from("src/main/java-templates") {
      include("*.java")
      filter<ReplaceTokens>("tokens" to tokens)
    }
    into("src/main/generated/io/knotx")
  }
  getByName<JavaCompile>("compileJava").dependsOn("templateClassProcessing")

  named<RatTask>("rat") {
    excludes.addAll("**/*.json", "**/*.MD", "**/*.templ", "**/*.adoc", "**/build/*", "**/out/*", "**/generated/*", "/src/test/resources/*")
  }
  getByName("build").dependsOn("rat")

  named<JavaCompile>("compileJava") {
    options.annotationProcessorGeneratedSourcesDirectory = File("$projectDir/src/main/generated")
  }

  named<Delete>("clean") {
    delete.add("src/main/generated")
  }

  named<Test>("test") {
    useJUnitPlatform()
    testLogging { showStandardStreams = true }
    testLogging { showExceptions = true }
    failFast = true
  }
}
