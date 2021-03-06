package myproject.iam

import java.util.UUID

import myproject.common.FutureImplicits._
import myproject.common.{ObjectNotFoundException, TimeManagement, TokenExpiredException}
import myproject.iam.Authorization.VoidIAMAccessChecker
import myproject.iam.Tokens.CRUD._
import myproject.iam.Tokens.{Token, TokenRole}
import myproject.iam.Users.GroupRole
import org.scalatest.DoNotDiscover
import test.{DatabaseSpec, IAMHelpers, IAMTestDataFactory}

@DoNotDiscover
class TokenSpecs extends DatabaseSpec {
  val channel = IAMTestDataFactory.getChannel
  val group = IAMTestDataFactory.getGroup(channel.id)
  val user = IAMTestDataFactory.getGroupUser(group.id, Some(GroupRole.Admin))
  val expiredToken = Token(UUID.randomUUID, user.id, TokenRole.Authentication, expires = Some(TimeManagement.getCurrentDateTime.plusSeconds(0)))
  val validToken = Token(UUID.randomUUID, user.id, TokenRole.Signup, expires = Some(TimeManagement.getCurrentDateTime.plusMinutes(10)))

  implicit val authz = VoidIAMAccessChecker
  
  it should "create a token" in {
    IAMHelpers.createChannel(channel).futureValue
    IAMHelpers.createGroup(group).futureValue
    IAMHelpers.createUser(user).futureValue
    createToken(expiredToken).futureValue.role shouldBe TokenRole.Authentication
    createToken(validToken).futureValue.role shouldBe TokenRole.Signup
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
