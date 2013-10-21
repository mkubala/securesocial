package securesocial.core.providers.responseParsing

import org.specs2.mutable.Specification
import securesocial.core.providers.ProviderResponseMocker
import play.api.libs.json.{JsString, JsNumber, JsObject}
import securesocial.core.{OAuth2Info, AuthenticationException}

class ProviderJsonResponseParserSpec extends Specification with ProviderResponseMocker {

  import securesocial.core.OAuth2Constants._

  val jsonResponseParser = new ProviderJsonResponseParser {}

  "ProviderJsonResponseParser" should {
    "build OAuth2Info" in {

      s"from {$AccessToken : '$testToken'}" in {
        val resp = response withJson JsObject(Vector(AccessToken -> JsString(testToken)))
        jsonResponseParser.buildInfo(resp) === OAuth2Info(accessToken = testToken)
      }

      s"from {$AccessToken : '$testToken', $ExpiresIn : '123456'}" in {
        val expiresValue = 123456
        val resp = response withJson JsObject(Vector(AccessToken -> JsString(testToken), ExpiresIn -> JsNumber(expiresValue)))
        jsonResponseParser.buildInfo(resp) === OAuth2Info(accessToken = testToken, expiresIn = Some(expiresValue))
      }

      s"from {$AccessToken : '$testToken', $ExpiresIn : '123456', $TokenType : '$testTokenType'}" in {
        val expiresValue = 123456
        val resp = response withJson JsObject(Vector(AccessToken -> JsString(testToken), ExpiresIn -> JsNumber(expiresValue), TokenType -> JsString(testTokenType)))
        jsonResponseParser.buildInfo(resp) === OAuth2Info(accessToken = testToken, expiresIn = Some(expiresValue), tokenType = Some(testTokenType))
      }

      s"from {$AccessToken : '$testToken', $ExpiresIn : '123456', $TokenType : '$testTokenType'}" in {
        val refreshToken = "123456asdff"
        val expiresValue = 123456
        val resp = response withJson JsObject(Vector(AccessToken -> JsString(testToken), ExpiresIn -> JsNumber(expiresValue), TokenType -> JsString(testTokenType), RefreshToken -> JsString(refreshToken)))
        jsonResponseParser.buildInfo(resp) === OAuth2Info(accessToken = testToken, expiresIn = Some(expiresValue), tokenType = Some(testTokenType), refreshToken = Some(refreshToken))
      }

    }
    "throw an exception" in {
      val responseWithoutAccessToken = response withJson JsObject(Vector(ExpiresIn -> JsNumber(123456)))
      jsonResponseParser.buildInfo(responseWithoutAccessToken) should throwA[AuthenticationException]
    }
  }

}
