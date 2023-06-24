package interfaceAdapter.controllers

import javax.inject.Inject

import scala.util.Random
import scala.concurrent.ExecutionContext

import com.zaxxer.hikari.HikariDataSource

import slick.jdbc.MySQLProfile.api._
import slick.jdbc.JdbcBackend.Database

import play.api.mvc._

class SlickConnectionController @Inject()(implicit
  cc: MessagesControllerComponents,
  dataSource: HikariDataSource,
  ec: ExecutionContext
) extends AbstractController(cc) {

  private val database = Database.forDataSource(dataSource, None)

  case class User(id: Option[Long], name: String)
  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name).mapTo[User]
  }

  private val userTable = TableQuery[UserTable]

  def connection = Action async {
    for {
      id <- database.run(userTable returning userTable.map(_.id) += User(None, Random.alphanumeric.take(10).mkString))
      result <- database.run(userTable.filter(_.id === id).result)
    } yield {
      Ok(result.map(v => s"ID: ${ v.id }, Name: ${ v.name }").mkString("\n"))
    }
  }
}
