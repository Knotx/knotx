import org.nosphere.apache.rat.RatTask

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
  id("java")
  id("org.nosphere.apache.rat") version "0.4.0"
}

group = "io.knotx"

// -----------------------------------------------------------------------------
// Dependencies
// -----------------------------------------------------------------------------

apply(from = "../gradle/common.deps.gradle.kts")
dependencies {
  testCompile(project(":knotx-splitter-html"))
  testCompile(project(":knotx-assembler"))
  testCompile(project(":knotx-core"))
  testCompile(project(":knotx-fallback"))
  testCompile(project(":knotx-launcher"))
}

// -----------------------------------------------------------------------------
// Source sets
// -----------------------------------------------------------------------------

apply(from = "../gradle/common.gradle.kts")

// -----------------------------------------------------------------------------
// Tasks
// -----------------------------------------------------------------------------


tasks {
  getByName<JavaCompile>("compileJava")

  named<RatTask>("rat") {
    excludes.addAll("**/*.json", "**/*.MD", "**/*.templ", "**/*.adoc", "**/build/*", "**/out/*", "**/generated/*", "/src/test/resources/*", "*.iml", ".vertx/*")
  }
  getByName("build").dependsOn("rat")

  named<Test>("test") {
    useJUnitPlatform()
    testLogging { showStandardStreams = true }
    testLogging { showExceptions = true }
    failFast = true
  }
}

