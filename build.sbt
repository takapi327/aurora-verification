import sbt._

name         := "sample-application-for-ecr-deployment"
organization := "io.github.takapi327"

ThisBuild / organizationName := "Takahiko Tominaga"

ThisBuild / scalaVersion := "2.13.3"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(DockerPlugin)
  .enablePlugins(EcrPlugin)

libraryDependencies ++= Seq(
  guice
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
  "-Ywarn-dead-code",
  "-Ymacro-annotations"
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
Docker / maintainer         := "t.takapi0327+infra-sample-canary-deploy@gmail.com"
dockerBaseImage             := "amazoncorretto:8"
Docker / dockerExposedPorts := Seq(9000, 9000)
Docker / daemonUser         := "daemon"

/** setting AWS Ecr */
import com.amazonaws.regions.{ Region, Regions }

Ecr / region           := Region.getRegion(Regions.AP_NORTHEAST_1)
Ecr / localDockerImage := (Docker / packageName).value + ":" + (Docker / version).value
Ecr / repositoryTags   := Seq(version.value, "latest")
Ecr / repositoryName   := {
  //if (master) { "prod-" + (Docker / packageName).value }
  //else        { "stg-"  + (Docker / packageName).value }
  if (master) { "prod-sample-canary-deploy" }
  else        { "stg-sample-canary-deploy" }
}

/** Setting sbt-release */
import ReleaseTransformations._

releaseVersionBump := sbtrelease.Version.Bump.Bugfix

releaseProcess := {
  if (master) {
    Seq[ReleaseStep](
      ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
      inquireVersions,
      runClean,
      setReleaseVersion,
      ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  } else if (staging) {
    Seq[ReleaseStep](
      runClean,
      ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
    )
  } else {
    Seq[ReleaseStep](
      runClean,
      ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
    )
  }
}
