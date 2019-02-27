package myproject.common

import myproject.common.Validation.ValidationError

sealed abstract class CustomException(msg: String) extends Exception(msg)
case class NullValueException(msg: String) extends CustomException(msg)
case class MissingKeyException(msg: String) extends CustomException(msg)
case class InvalidTypeException(msg: String) extends CustomException(msg)
case class InvalidParametersException(msg: String, errors: List[String]) extends CustomException(msg)
case class ObjectNotFoundException(msg: String) extends CustomException(msg)
case class UnexpectedErrorException(msg: String) extends CustomException(msg)
case class DatabaseErrorException(msg: String) extends CustomException(msg)
case class NotImplementedException(msg: String) extends CustomException(msg)
case class AuthenticationNeededException(msg: String) extends CustomException(msg)
case class AccessRefusedException(msg: String) extends CustomException(msg)
case class InvalidContextException(msg: String) extends CustomException(msg)
case class AuthenticationFailedException(msg: String) extends CustomException(msg)
case class TokenExpiredException(msg: String) extends CustomException(msg)
case class IllegalOperationException(msg: String) extends CustomException(msg)
case class UniquenessCheckException(msg: String) extends CustomException(msg)
case class LoginAlreadyExistsException(msg: String) extends CustomException(msg)
case class EmailAlreadyExistsException(msg: String) extends CustomException(msg)
case class ValidationErrorException(msg: String, errors: List[ValidationError]) extends CustomException(msg) {
  override def toString = "ValidationErrorException, errors: " + errors.mkString("/")
}
