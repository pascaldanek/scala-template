package myproject.api.functions

import myproject.api.ApiFunction
import myproject.api.Serializers._
import myproject.audit.Audit
import myproject.common.serialization.OpaqueData
import myproject.iam.Groups.CRUD
import myproject.iam.{Authorization, Users}

class GetGroupUsers extends ApiFunction {
  override val name = "get_group_users"
  override val description = "get all users in a given group"

  override def process(implicit p: OpaqueData.ReifiedDataWrapper, user: Users.User, auditData: Audit.AuditData) = {
    val groupId = p.uuid("group_id")
    CRUD.getGroupUsers(groupId, Authorization.canListGroupUsers(user, _)) map { users =>
      users.map(_.toMap)
    }
  }
}
