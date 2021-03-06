package myproject.api.functions

import myproject.api.Serializers._
import myproject.api.{ApiFunction, ApiSummaryDoc}
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper
import myproject.database.ApplicationDatabase
import myproject.iam.Channels.{CRUD, ChannelAccessChecker}
import myproject.iam.Users
import myproject.iam.Users.User

class GetChannels(implicit authz: User => ChannelAccessChecker, db: ApplicationDatabase) extends ApiFunction {
  override val name = "get_channels"
  override val doc = ApiSummaryDoc(
    description = "get all the existing channels on the platform (requires high privileges such as platform administrator's",
    `return` = "a list of objects containing the requested channel's data")

  override def process(implicit p: ReifiedDataWrapper, user: Users.User) = {
    implicit val checker = authz(user)

    CRUD.getAllChannels map (_.serialize)
  }
}
