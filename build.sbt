ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "zstdDemo",
    libraryDependencies++=Seq(
      "com.github.luben" %"zstd-jni" %"1.3.7-3"
    )
  )

