package myproject.api.functions

import java.util.UUID

import myproject.api.Serializers._
import myproject.api.{ApiFunction, ApiSummaryDoc}
import myproject.audit.Audit
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper
import myproject.common.serialization.OpaqueData.ReifiedDataWrapper._
import myproject.iam.Channels.{CRUD, Channel, ChannelAccessChecker, ChannelDAO}
import myproject.iam.Users
import myproject.iam.Users.User

class CreateChannel(implicit authz: User => ChannelAccessChecker, db: ChannelDAO) extends ApiFunction {
  override val name = "create_channel"
  override val doc = ApiSummaryDoc(
    description = "create a new channel (a channel is a group of groups) (requires high level privileges such as platform administrator)",
    `return` = "an object containing the newly created channel's data")

  override def process(implicit p: ReifiedDataWrapper, user: Users.User, auditData: Audit.AuditData) = {
    val channelName = required(p.nonEmptyString("name"))

    implicit val checker = authz(user)

    checkParamAndProcess(channelName) flatMap { _ =>
      val channel = Channel(UUID.randomUUID, channelName.get)
      CRUD.createChannel(channel) map (_.toMap)
    }
  }
}
