package myproject.modules.iam.domain

import myproject.modules.iam.User
import org.bouncycastle.crypto.generators.OpenBSDBCrypt

trait Authentication {

  def loginPassword(user: User, candidate: String): Option[User] =
    if(checkPassword(candidate, user.hashedPassword)) Some(user) else None

  private def checkPassword(candidate: String, hashedPassword: String): Boolean =
    OpenBSDBCrypt.checkPassword(hashedPassword, candidate.toCharArray)
}
