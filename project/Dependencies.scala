
import sbt._

object Dependencies {
  val hikariCP = "com.zaxxer" % "HikariCP" % "4.0.3"

  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.11"
  object Driver {
    val mysql = "mysql" % "mysql-connector-java" % "5.1.39"
    val maria = "org.mariadb.jdbc" % "mariadb-java-client" % "2.7.2"
    val aws   = "software.aws.rds" % "aws-mysql-jdbc" % "1.1.7"
  }

  val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

  val slick = "com.typesafe.slick" %% "slick" % "3.2.1"
}
