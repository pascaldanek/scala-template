package myproject.iam.dao

import java.time.LocalDateTime
import java.util.UUID

import myproject.common.ObjectNotFoundException
import myproject.common.Runtime.ec
import myproject.database.DAO
import myproject.iam.Tokens.{Token, TokenRole}

import scala.concurrent.Future

trait TokenDAO extends DAO {

  import api._

  implicit def tokenRoleMapper = MappedColumnType.base[TokenRole.Value, Int](
    e => e.id,
    i => TokenRole(i))

  protected class Tokens(tag: Tag) extends Table[Token](tag, "TOKENS") {
    def id = column[UUID]("TOKEN_ID", O.PrimaryKey, O.SqlType("UUID"))
    def userId = column[UUID]("USER_ID", O.SqlType("UUID"))
    def role = column[TokenRole.Value]("ROLE")
    def expires = column[Option[LocalDateTime]]("EXPIRES")
    def * = (id, userId, role, expires) <> (Token.tupled, Token.unapply)
  }

  protected val tokens = TableQuery[Tokens]

  def getToken(id: UUID): Future[Token] = db.run(tokens.filter(_.id===id).result) map {
    case Nil => throw ObjectNotFoundException(s"token with id $id was not found")
    case t +: _ => t
  }

  def createToken(token: Token) = db.run(tokens += token) map (_ => token)
}