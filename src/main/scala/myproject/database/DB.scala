package myproject.database

import java.util.UUID

import myproject.common.security.BCrypt
import myproject.iam.Users.User
import myproject.iam.dao.UserDAO
import slick.jdbc.JdbcProfile

object DB extends JdbcProfile with UserDAO {

  import api._

  lazy val db = Database.forConfig("database")

  def reset = db.run(DBIO.seq(
    users.schema.drop.asTry,
    users.schema.create,
    users ++= Seq(
      User(UUID.fromString("e498dd4e-2758-4e01-80f6-392f4f43606b"), "admin", BCrypt.hashPassword("Kondor_123")))))
}