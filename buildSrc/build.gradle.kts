plugins {
  `kotlin-dsl`
}

repositories {
  mavenLocal()
  jcenter()
  maven { url = uri("https://plugins.gradle.org/m2/") }
  maven { url = uri("http://dl.bintray.com/cognifide/maven-public") }
  maven { url = uri("https://dl.bintray.com/neva-dev/maven-public") }
}
