package myproject.api.functions

import myproject.api.Serializers._
import myproject.api.{ApiFunction, ApiSummaryDoc}
import myproject.common.serialization.OpaqueData
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper.required
import myproject.database.ApplicationDatabase
import myproject.iam.Groups.{CRUD, GroupAccessChecker}
import myproject.iam.Users
import myproject.iam.Users.User

class GetGroupChildren(implicit authz: User => GroupAccessChecker, db: ApplicationDatabase) extends ApiFunction {
  override val name = "get_group_children"
  override val doc = ApiSummaryDoc(
    description = "get all the descendants of a group (this function is more dedicated to a parent group admin as for a channel or platform admin, the complete group hierarchy can be reconstructed from the group lists of a channel using the parent id property)",
    `return` = "a list containing all the children groups")

  override def process(implicit p: OpaqueData.ReifiedDataWrapper, user: Users.User) = {
    val groupId = required(p.uuid("group_id"))

    implicit val checker = authz(user)

    checkParamAndProcess(groupId) {
      CRUD.getGroupChildren(groupId.get) map (_.serialize)
    }
  }
}
