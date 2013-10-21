package securesocial.core.providers.responseParsing

import securesocial.core.{AuthenticationException, OAuth2Constants, OAuth2Info}
import play.api.Logger
import scala.util.matching.Regex
import securesocial.core.OAuth2Constants._
import play.api.libs.ws.Response
import play.api.libs.json.JsString

trait ProviderResponseParser {

  def buildInfo(response: Response): OAuth2Info

}

trait ProviderJsonResponseParser extends ProviderResponseParser {

  def buildInfo(response: Response): OAuth2Info = {
    val json = response.json
    if (Logger.isDebugEnabled) {
      Logger.debug("[securesocial] got json back [" + json + "]")
    }
    (json \ AccessToken) match {
      case JsString(accessToken) => OAuth2Info(
        accessToken,
        (json \ OAuth2Constants.TokenType).asOpt[String],
        (json \ OAuth2Constants.ExpiresIn).asOpt[Int],
        (json \ OAuth2Constants.RefreshToken).asOpt[String]
      )
      case _ =>
        Logger.error("[securesocial] invalid response format for accessToken")
        throw new AuthenticationException()
    }
  }

}

trait ProviderQueryStringResponseParser extends ProviderResponseParser {

  protected val fieldsRegex: Map[String, Regex]

  def buildInfo(response: Response): OAuth2Info = {
    val body = response.body
    if (Logger.isDebugEnabled) {
      Logger.debug("[securesocial] got string back [" + body + "]")
    }
    val parsedBody = fieldsRegex.flatMap({
      case (key, regexp) => regexp.unapplySeq(body)
        .withFilter(parsedValuesSeq => !parsedValuesSeq.isEmpty)
        .map(parsedValue => (key, parsedValue.head))
    })

    parsedBody.get(AccessToken) match {
      case Some(accessToken) => OAuth2Info(
        accessToken,
        parsedBody.get(TokenType),
        parsedBody.get(ExpiresIn).map(_.toInt),
        parsedBody.get(RefreshToken)
      )
      case None =>
        Logger.error("[securesocial] invalid response format for accessToken")
        throw new AuthenticationException()
    }
  }

}

object ProviderQueryStringResponseParser {
  val referenceFieldsRegex = Map(
    AccessToken -> s""".*$AccessToken=([^&]*).*""".r,
    ExpiresIn -> s""".*$ExpiresIn=([0-9]*).*""".r,
    TokenType -> s""".*$TokenType=([^&=]*).*""".r,
    RefreshToken -> s""".*$RefreshToken=([^&]*).*""".r
  )
}
