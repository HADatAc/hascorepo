organization := "org.hadatac"

name := "hascoapi"

version := "10.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.17"
//  "2.13.4"

val playPac4jVersion = "11.1.0-PLAY2.8"
val pac4jVersion = "5.7.0"
val playVersion = "2.8.19"
val guiceVersion = "5.1.0"

val guiceDeps = Seq(
  "com.google.inject" % "guice" % guiceVersion,
  "com.google.inject.extensions" % "guice-assistedinject" % guiceVersion
)

libraryDependencies ++= Seq(
  "com.feth" %% "play-easymail" % "0.9.3",
  guice,
  caffeine,
  //ehcache,
  //  cacheApi,
  evolutions,
  javaWs,
  javaJdbc,
  "net.minidev" % "json-smart" % "2.4.9",
  "org.webjars" % "bootstrap" % "5.2.3",
  "org.webjars" % "jquery" % "3.6.3",
  "org.webjars" %% "webjars-play" % "2.8.18",
  "org.easytesting" % "fest-assert" % "1.4" % "test",
  "org.apache.commons" % "commons-text" % "1.10.0",
  "commons-validator" % "commons-validator" % "1.7",
  "org.pac4j" %% "play-pac4j" % playPac4jVersion,
  "org.pac4j" % "pac4j-http" % pac4jVersion,
  "org.pac4j" % "pac4j-jwt" % pac4jVersion exclude("commons-io" , "commons-io"),
  "org.apache.jena" % "jena-core" % "4.7.0",
  "org.apache.jena" % "jena-arq" % "4.7.0",
  "org.eclipse.rdf4j" % "rdf4j-model" % "4.2.3",
  "org.eclipse.rdf4j" % "rdf4j-repository-api" % "4.2.3",
  "org.eclipse.rdf4j" % "rdf4j-runtime" % "4.2.3",
  "args4j" % "args4j" % "2.33",
  "joda-time" % "joda-time" % "2.12.5",
  "org.jasypt" % "jasypt" % "1.9.3",
  "com.typesafe.play" %% "play-iteratees" % "2.6.1",
  "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1",
  "com.google.code.gson" % "gson" % "2.10.1",
  "org.apache.commons" % "commons-jcs" % "2.2.1" pomOnly(),
  "com.typesafe.play" % "play-cache_2.12" % playVersion,
  "commons-io" % "commons-io" % "2.11.0",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.15.2",
  "org.apache.poi" % "poi-ooxml" % "5.2.3",
  "org.apache.commons" % "commons-configuration2" % "2.8.0",
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "org.springframework.security" % "spring-security-crypto" % "6.0.2",
  "org.apache.logging.log4j" % "log4j-api" % "2.20.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.20.0",
  "org.jsoup" % "jsoup" % "1.16.1",
  "org.xhtmlrenderer" % "flying-saucer-pdf-openpdf" % "9.1.22",
  // "com.itextpdf" % "itextpdf" % "5.5.13.3",
  "ca.uhn.hapi.fhir" % "hapi-fhir-base" % "6.6.1",
  "ca.uhn.hapi.fhir" % "hapi-fhir-structures-r4" % "6.6.1",
  "ch.qos.logback" % "logback-classic" % "1.4.8",
  "net.aichler" % "jupiter-interface" % "0.11.1" % Test ,

  //For Java > 8
  "javax.xml.bind" % "jaxb-api" % "2.3.1",
  "javax.annotation" % "javax.annotation-api" % "1.3.2",
  "javax.el" % "javax.el-api" % "3.0.0",
  "org.glassfish" % "javax.el" % "3.0.0"


) .map(_.exclude("*", "slf4j-log4j12")) ++ guiceDeps //For Play 2.6 & JDK9

resolvers ++= Seq(Resolver.mavenLocal, "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/", "Shibboleth releases" at "https://build.shibboleth.net/nexus/content/repositories/releases/",
  "Spring Framework Security" at "https://mvnrepository.com/artifact/org.springframework.security/spring-security-crypto")

routesGenerator := InjectedRoutesGenerator
