tasks.withType<JavaCompile>().configureEach {
  with(options) {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    encoding = "UTF-8"
  }
}
