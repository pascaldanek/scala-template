package myproject.api.functions

import myproject.api.Serializers._
import myproject.api.{ApiFunction, ApiSummaryDoc}
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper._
import myproject.database.ApplicationDatabase
import myproject.iam.Users._

class UpdateUser(implicit authz: User => UserAccessChecker, db: ApplicationDatabase)  extends ApiFunction {
  override val name = "update_user"
  override val doc = ApiSummaryDoc(
    description = "fully update or patch a user regardless of his type (depending on which fields are updated, different authorization rules may be used)",
    `return` = "an object containing the resulting user's data ")

  override def process(implicit p: ReifiedDataWrapper, user: User) = {
    val userId = required(p.uuid("user_id"), "the user id is bla bla")
    val email = optional(p.email("email"))
    val password = optional(p.nonEmptyString("password"))
    val login = optional(p.nonEmptyString("login"))
    val groupRole = optionalAndNullable(p.enumString("group_role", GroupRole))
    val status = optional(p.enumString("status", UserStatus))

    implicit val checker = authz(user)

    checkParamAndProcess(userId, email, password, login, groupRole, status) {
      CRUD.updateUser(userId.get,
        UserUpdate(
          login = login.get,
          email = email.get,
          password = password.get,
          status = status.get,
          groupRole = groupRole.get)) map (_.serialize)
    }
  }
}
