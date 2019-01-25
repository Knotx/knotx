dependencies {
  "annotationProcessor"(platform("io.knotx:knotx-dependencies:${project.version}"))
  "annotationProcessor"(group = "io.vertx", name = "vertx-codegen")
  "annotationProcessor"(group = "io.vertx", name = "vertx-service-proxy", classifier = "processor")
  "annotationProcessor"(group = "io.vertx", name = "vertx-rx-java2-gen")
}

tasks.named<JavaCompile>("compileJava") {
  options.annotationProcessorGeneratedSourcesDirectory = file("src/main/generated")
}

tasks.named<Delete>("clean") {
  delete.add("src/main/generated")
}
