package securesocial.core.providers.responseParsing

import org.specs2.mutable.Specification
import securesocial.core.providers.ProviderResponseMocker
import play.api.libs.json.{JsNumber, JsObject}
import securesocial.core.{OAuth2Info, AuthenticationException}
import scala.util.matching.Regex

class ProviderQueryStringResponseParserSpec extends Specification with ProviderResponseMocker {

  import securesocial.core.OAuth2Constants._

  val queryStringResponseParser = new ProviderQueryStringResponseParser {
    protected val fieldsRegex: Map[String, Regex] = Map(
      AccessToken -> """.*wiredTokenParameterName=([^&]*).*""".r
    )
  }

  "ProviderQueryStringResponseParser" should {
    "build OAuth2Info" in {

      s"from 'huhItsStrange=123456&wiredTokenParameterName=$testToken&somebodyDoesNotFollowReference=true'" in {
        val resp = response withBody s"""huhItsStrange=123456&wiredTokenParameterName=$testToken&somebodyDoesNotFollowReference=true"""
        queryStringResponseParser.buildInfo(resp) === OAuth2Info(accessToken = testToken)
      }

    }
    "throw an exception" in {
      val responseWithoutAccessToken = response withJson JsObject(Vector(ExpiresIn -> JsNumber(123456)))
      queryStringResponseParser.buildInfo(responseWithoutAccessToken) should throwA[AuthenticationException]
    }
  }

}
