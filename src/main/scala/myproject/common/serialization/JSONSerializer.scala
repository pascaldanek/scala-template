package myproject.common.serialization

import java.lang.reflect.{ParameterizedType, Type}

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JSONSerializer {

  case class JSONConfig(prettyPrint: Boolean)

  private val mapper = new ObjectMapper()

  mapper.registerModule(DefaultScalaModule)

  mapper.setSerializationInclusion(Include.NON_NULL)

  mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
    WRITE_DATES_AS_TIMESTAMPS, false)

  mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.
    FAIL_ON_UNKNOWN_PROPERTIES, false)

  mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.
    FAIL_ON_UNRESOLVED_OBJECT_IDS, true)

  mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.
    ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

  mapper.registerModule(new JavaTimeModule())

  implicit class JsonString(doc: String) {

    /**
      * Deserialize the json document.
      * @param m the class manifest, automatically fed by Scala compiler
      * @tparam A the target type
      * @return the object of type A
      */
    def fromJson[A](implicit m: Manifest[A]): A = mapper.readValue[A](doc, typeReference[A])

    private[this] def typeReference[T: Manifest] = new TypeReference[T] {
      override def getType = typeFromManifest(manifest[T])
    }

    private[this] def typeFromManifest(m: Manifest[_]): Type = {
      if (m.typeArguments.isEmpty) {
        m.runtimeClass
      }
      else new ParameterizedType {
        def getRawType = m.runtimeClass

        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

        def getOwnerType = null
      }
    }
  }

  def toJson[A: Manifest](obj: A)(implicit config: JSONConfig = JSONConfig(false)): String =
    if (config.prettyPrint)
      mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
    else
      mapper.writeValueAsString(obj)

  def prettyPrintJsonString(json: String): String = {
    val obj = mapper.readValue(json, classOf[Object])
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
  }

  def simplePrintJsonString(json: String): String = {
    val obj = mapper.readValue(json, classOf[Object])
    mapper.writeValueAsString(obj)
  }
}
