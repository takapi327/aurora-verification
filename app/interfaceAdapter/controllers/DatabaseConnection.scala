package interfaceAdapter.controllers

import java.security.Security
import javax.inject.Inject

import scala.util.Random

import com.zaxxer.hikari.HikariDataSource

import play.api.mvc._

class DatabaseConnectionController @Inject()(implicit
  cc: MessagesControllerComponents,
  dataSource: HikariDataSource
) extends AbstractController(cc) {

  def connection = Action {

    println("=================")
    println(Security.getProperty("networkaddress.cache.ttl"))
    println("=================")

    val connection = dataSource.getConnection
    if (connection.isValid(10)) {
      val name = Random.alphanumeric.take(10).mkString
      val insert = connection.prepareStatement(s"INSERT INTO user (name) VALUES ('$name')")
      insert.executeUpdate()
      val statement = connection.prepareStatement("SELECT name FROM user")
      val resultSet = statement.executeQuery()
      while (resultSet.next()) {
        println(resultSet.getString("name"))
      }
      insert.close()
      statement.close()
      connection.close()
      Ok("ok")
    } else {
      NotFound("コネクションが存在しない")
    }
  }
}
