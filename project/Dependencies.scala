import sbt._

// NOTE: Dependencies that require specification of the Scala version use the
// `%%` operator to append the specified scala version.  See
// https://www.scala-sbt.org/1.x/docs/Library-Dependencies.html#Getting+the+right+Scala+version+with

// Follows SBT recommended structure
// See https://www.scala-sbt.org/1.x/docs/Organizing-Build.html#Tracking+dependencies+in+one+place
object Dependencies {

  lazy val zioVersion = "1.0.17"

  object Deps {
    // Versions
    lazy val logbackVersion = "1.4.4"
    lazy val clistVersion = "3.5.1"

    // Libraries
    val clistCore = "org.backuity.clist" %% "clist-core" % clistVersion
    val clistMacros = "org.backuity.clist" %% "clist-macros" % clistVersion
    val commonsBeanutils =
      "commons-beanutils" % "commons-beanutils" % "1.9.4" // Apparently an untracked dependency of commonsConfiguration2
    val commonsConfiguration2 = "org.apache.commons" % "commons-configuration2" % "2.8.0"
    val commonsIo = "commons-io" % "commons-io" % "2.11.0"
    val guice = "com.google.inject" % "guice" % "5.1.0"
    val kiama = "org.bitbucket.inkytonik.kiama" %% "kiama" % "2.5.0"
    val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
    val logbackCore = "ch.qos.logback" % "logback-core" % logbackVersion
    val logging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
    val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.17.1"
    val scalaParserCombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
    val scalaCollectionContrib = "org.scala-lang.modules" %% "scala-collection-contrib" % "0.2.2"
    val scalaz = "org.scalaz" %% "scalaz-core" % "7.3.5"
    val slf4j = "org.slf4j" % "slf4j-api" % "2.0.3"
    val shapeless = "com.chuusai" %% "shapeless" % "2.3.10"
    val tla2tools = "org.lamport" % "tla2tools" % "1.7.0-SNAPSHOT"
    val ujson = "com.lihaoyi" %% "ujson" % "2.0.0"
    val upickle = "com.lihaoyi" %% "upickle" % "2.0.0"
    val z3 = "tools.aqua" % "z3-turnkey" % "4.11.2"
    val zio = "dev.zio" %% "zio" % zioVersion
    // Keep up to sync with version in plugins.sbt
    val zioGrpcCodgen = "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.0-test3" % "provided"
    val grpcNetty = "io.grpc" % "grpc-netty" % "1.49.2"
    val scalapbRuntimGrpc =
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
    // Ensures we have access to commonly used protocol buffers (e.g., google.protobuf.Struct)
    // see https://scalapb.github.io/docs/faq/#i-am-getting-import-was-not-found-or-had-errors
    val scalapbRuntime =
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
  }

  // Test only depenendencies
  object TestDeps {
    // Libraries
    val junit = "junit" % "junit" % "4.13.2" % Test
    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
    val easymock = "org.easymock" % "easymock" % "5.0.0" % Test

    val scalaTestVersion = "3.2.11"
    val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    val scalatestplusEasymock = "org.scalatestplus" %% "easymock-4-3" % s"${scalaTestVersion}.0" % Test
    val scalatestplusJunit = "org.scalatestplus" %% "junit-4-13" % s"${scalaTestVersion}.0" % Test
    val scalatestplusScalacheck = "org.scalatestplus" %% "scalacheck-1-15" % s"${scalaTestVersion}.0" % Test

    val zioTest = "dev.zio" %% "zio-test" % zioVersion % Test
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % zioVersion % Test
  }
}
