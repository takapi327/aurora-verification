import sbt._

import Dependencies._

name         := "aurora-verification"
organization := "com.github.takapi327"

ThisBuild / organizationName := "takapi327"

ThisBuild / scalaVersion := "2.12.11"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(DockerPlugin)
  .enablePlugins(EcrPlugin)

libraryDependencies ++= Seq(
  guice,
  hikariCP,
  Driver.mysql,
  logbackClassic,
  typesafeConfig,
  slick
)

import scala.sys.process._
lazy val branch  = ("git branch".lineStream_!).find(_.head == '*').map(_.drop(2)).getOrElse("")
lazy val master  = branch == "master"
lazy val staging = branch.startsWith("staging")

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ywarn-dead-code"
)

javaOptions ++= Seq(
  "-Dconfig.file=conf/env.dev/application.conf",
  "-Dlogger.file=conf/env.dev/logback.xml"
)

Universal / javaOptions ++= Seq(
  "-Dpidfile.path=/dev/null"
)

run / fork := true

/**
 * Setting for Docker Image
 */
Docker / maintainer         := "takahiko.tominaga+aws_takapi327_product_b@nextbeat.net"
dockerBaseImage             := "amazoncorretto:8"
Docker / dockerExposedPorts := Seq(9000, 9000)
Docker / daemonUser         := "daemon"

/** setting AWS Ecr */
import com.amazonaws.regions.{ Region, Regions }

Ecr / region           := Region.getRegion(Regions.AP_NORTHEAST_1)
Ecr / localDockerImage := (Docker / packageName).value + ":" + (Docker / version).value
Ecr / repositoryTags   := Seq(version.value, "latest")
Ecr / repositoryName   := (Docker / packageName).value + "-server"

/** Setting sbt-release */
import ReleaseTransformations._

releaseVersionBump := sbtrelease.Version.Bump.Bugfix

releaseProcess := Seq[ReleaseStep](
  runClean,
  ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
  ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
  ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
)
