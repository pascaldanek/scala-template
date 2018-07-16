package myproject.iam

import myproject.common.FutureImplicits._
import myproject.common.{ObjectNotFoundException, TokenExpiredException}
import myproject.iam.Tokens.CRUD._
import myproject.iam.Tokens.TokenRole
import test.DatabaseSpec

import scala.concurrent.duration._

class TokenSpecs extends DatabaseSpec {

  lazy val company = Companies.CRUD.createCompany("ACME").futureValue
  lazy val user = Users.CRUD.createUser("tokens-specs", "secret", company.id).futureValue
  lazy val expiredToken = createToken(user.id, TokenRole.Authentication, Some(0.second)).futureValue
  lazy val validToken = createToken(user.id, TokenRole.Signup, Some(10.minutes)).futureValue

  it should "create a token" in {
    expiredToken.role shouldBe TokenRole.Authentication
    validToken.role shouldBe TokenRole.Signup
  }

  it should "not retrieve the expired token" in {
    a [TokenExpiredException] should be thrownBy getToken(expiredToken.id).futureValue
  }

  it should "retrieve the valid token" in {
    getToken(validToken.id).futureValue.role shouldBe TokenRole.Signup
  }

  it should "delete the token" in {
    deleteToken(validToken.id).futureValue
    a [ObjectNotFoundException] shouldBe thrownBy(getToken(validToken.id).futureValue)
  }
}
