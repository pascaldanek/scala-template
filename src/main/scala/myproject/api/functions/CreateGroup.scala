package myproject.api.functions

import java.util.UUID

import myproject.api.Serializers._
import myproject.api.{ApiFunction, ApiSummaryDoc}
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper._
import myproject.database.ApplicationDatabase
import myproject.iam.Groups.{CRUD, GroupAccessChecker, GroupStatus, GroupUpdate}
import myproject.iam.Users
import myproject.iam.Users.User

class CreateGroup(implicit authz: User => GroupAccessChecker, db: ApplicationDatabase) extends ApiFunction {
  override val name = "create_group"
  override val doc = ApiSummaryDoc(
    description = "create a new users group (requires admin privileges on the target channel)",
    `return` = "an object containing the newly created group data")

  override def process(implicit p: ReifiedDataWrapper, user: Users.User) = {
    val groupName = required(p.nonEmptyString("name"))
    val channelId = required(p.uuid("channel_id"), "the channel id the new group will belong to")
    val parentId = optional(p.uuid("parent_id"), "an optional parent group id can be provided in case the created group belongs to an organization")

    implicit val checker = authz(user)

    checkParamAndProcess(groupName, channelId, parentId) {
      CRUD.createGroup(UUID.randomUUID, channelId.get, parentId = parentId.get, GroupUpdate(Some(groupName.get), Some(GroupStatus.Active))) map (_.serialize)
    }
  }
}
