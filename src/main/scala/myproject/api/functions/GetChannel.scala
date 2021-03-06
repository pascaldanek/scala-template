package myproject.api.functions

import myproject.api.Serializers._
import myproject.api.{ApiFunction, ApiSummaryDoc}
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper._
import myproject.database.ApplicationDatabase
import myproject.iam.Channels.{CRUD, ChannelAccessChecker}
import myproject.iam.Users.User

class GetChannel(implicit authz: User => ChannelAccessChecker, db: ApplicationDatabase) extends ApiFunction {
  override val name = "get_channel"
  override val doc = ApiSummaryDoc("get an existing channel (a channel is a group of groups)", "an object containing the requested channel's data")

  override def process(implicit p: ReifiedDataWrapper, user: User) = {
    val channelId = required(p.uuid("channel_id"))

    implicit val checker = authz(user)

    checkParamAndProcess(channelId) {
      CRUD.getChannel(channelId.get) map (_.serialize)
    }
  }
}
