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

import org.apache.tools.ant.filters.ReplaceTokens
import org.nosphere.apache.rat.RatTask

plugins {
  id("java-library")
  id("maven-publish")
  id("signing")
  id("org.nosphere.apache.rat") version "0.4.0"
}

group = "io.knotx"

// -----------------------------------------------------------------------------
// Dependencies
// -----------------------------------------------------------------------------

val junitTestCompile = configurations.create("junitTestCompile")

apply(from = "../gradle/common.deps.gradle.kts")
apply(from = "../gradle/codegen.deps.gradle.kts")
dependencies {
  api(project(":knotx-fragment-api"))
  api(group = "com.google.guava", name = "guava")
  api(group = "commons-io", name = "commons-io")
  api(group = "org.apache.commons", name = "commons-lang3")
  api(group = "com.typesafe", name = "config")
  api(group = "commons-collections", name = "commons-collections")
}

junitTestCompile.extendsFrom(configurations.named("testImplementation").get())

// -----------------------------------------------------------------------------
// Source sets
// -----------------------------------------------------------------------------

apply(from = "../gradle/common.gradle.kts")
sourceSets.named("main") {
  java.srcDir("src/main/generated")
}
sourceSets.create("junitTest") {
  compileClasspath += sourceSets.named("main").get().output
}


// -----------------------------------------------------------------------------
// Tasks
// -----------------------------------------------------------------------------


tasks {
  //FIXME there is race condition with copying Version to generated and compiling project
  register<Copy>("templatesProcessing") {
    val tokens = mapOf("project.version" to project.version, "build.timestamp" to "${Utils.timestamp()}")
    inputs.properties(tokens)

    from("src/main/java-templates") {
      include("*.java")
      filter<ReplaceTokens>("tokens" to tokens)
    }
    into("src/main/generated/io/knotx/launcher")
  }
  getByName<JavaCompile>("compileJava").dependsOn("templatesProcessing")

  named<RatTask>("rat") {
    excludes.addAll("**/*.json", "**/*.MD", "**/*.templ", "**/*.adoc", "**/build/*", "**/out/*", "**/generated/*", "/src/test/resources/*", "*.iml")
  }
  getByName("build").dependsOn("rat")

  named<Test>("test") {
    useJUnitPlatform()
    testLogging { showStandardStreams = true }
    testLogging { showExceptions = true }
    failFast = true
  }
}

// -----------------------------------------------------------------------------
// Publication
// -----------------------------------------------------------------------------
tasks.register<Jar>("sourcesJar") {
  from(sourceSets.named("main").get().allJava)
  classifier = "sources"
}

tasks.register<Jar>("javadocJar") {
  from(tasks.named<Javadoc>("javadoc"))
  classifier = "javadoc"
}

tasks.register<Jar>("testJar") {
  from(sourceSets.named("junitTest").get().output)
  classifier = "tests"
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      artifactId = "knotx-launcher"
      from(components["java"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      artifact(tasks["testJar"])
      pom {
        name.set("Knot.x Launcher")
        description.set("Knot.x Launcher - deploys all Knot.x modules in Vert.x instance.")
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
        val releasesRepoUrl = "${Sonatype.releasesStaging}"
        val snapshotsRepoUrl = "${Sonatype.releasesSnapshot}"
        url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        credentials {
          username = if (project.hasProperty("ossrhUsername")) project.property("ossrhUsername")?.toString() else "UNKNOWN"
          password = if (project.hasProperty("ossrhPassword")) project.property("ossrhPassword")?.toString() else "UNKNOWN"
          println("Connecting with user: ${username}")
        }
      }
    }
  }
}

signing {
  sign(publishing.publications["mavenJava"])
}

tasks.named<Javadoc>("javadoc") {
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}
