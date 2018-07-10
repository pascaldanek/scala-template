package myproject.web.server

import akka.http.scaladsl.server.Directives.AsyncAuthenticator
import akka.http.scaladsl.server.directives.Credentials
import myproject.common.security.JWT
import myproject.common.{DefaultExecutionContext, ObjectNotFoundException}
import myproject.database.Database
import myproject.modules.iam.domain.Authentication
import myproject.modules.iam.{Guest, User, UserGeneric}

import scala.concurrent.Future

trait WebAuth extends DefaultExecutionContext with Database with JWT with Authentication {

  def jwtAuthenticate(token: String): Future[Option[User]] = {
    Future(extractToken(token)) flatMap {
      case Left(_) => Future.successful(None)
      case Right(payload) =>
        getById(payload.uid) map (Some(_)) recover { case ObjectNotFoundException(_) => None }
    }
  }

  def jwtAuthenticator: AsyncAuthenticator[UserGeneric] = {
    case Credentials.Missing => Future.successful(Some(Guest()))
    case Credentials.Provided(token) => jwtAuthenticate(token)
  }

  def loginPasswordToJwt(login: String, password: String): Future[Option[String]] = {
    getByLoginName(login) map { user =>
      loginPassword(user, password) map { _ =>
        createToken(user.login, user.id)
      }
    }
  }
}
