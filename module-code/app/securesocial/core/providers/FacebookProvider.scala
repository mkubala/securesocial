/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package securesocial.core.providers

import play.api.{Application, Logger}
import securesocial.core._
import play.api.libs.ws.WS
import securesocial.core.providers.responseParsing.ProviderQueryStringResponseParser
import securesocial.core.OAuth2Constants._
import securesocial.core.IdentityId
import scala.Some
import play.api.libs.json.JsObject
import securesocial.core.AuthenticationException

/**
 * A Facebook Provider
 */
class FacebookProvider(application: Application) extends OAuth2Provider(application) with ProviderQueryStringResponseParser {
  val MeApi = "https://graph.facebook.com/me?fields=name,first_name,last_name,picture,email&return_ssl_resources=1&access_token="
  val Error = "error"
  val Message = "message"
  val Type = "type"
  val Id = "id"
  val FirstName = "first_name"
  val LastName = "last_name"
  val Name = "name"
  val Picture = "picture"
  val Email = "email"
  val Data = "data"
  val Url = "url"
  val Expires = "expires"

  protected val fieldsRegex = FacebookProvider.fieldsRegex

  override def id = FacebookProvider.Facebook

  def fillProfile(user: SocialUser) = {
    val accessToken = user.oAuth2Info.get.accessToken
    val call = WS.url(MeApi + accessToken).get()

    try {
      val response = awaitResult(call)
      val me = response.json
      (me \ Error).asOpt[JsObject] match {
        case Some(error) =>
          val message = (error \ Message).as[String]
          val errorType = (error \ Type).as[String]
          Logger.error(
            "[securesocial] error retrieving profile information from Facebook. Error type: %s, message: %s".
              format(errorType, message)
          )
          throw new AuthenticationException()
        case _ =>
          val userId = (me \ Id).as[String]
          val name = (me \ Name).as[String]
          val firstName = (me \ FirstName).as[String]
          val lastName = (me \ LastName).as[String]
          val picture = (me \ Picture)
          val avatarUrl = (picture \ Data \ Url).asOpt[String]
          val email = (me \ Email).as[String]

          user.copy(
            identityId = IdentityId(userId, id),
            firstName = firstName,
            lastName = lastName,
            fullName = name,
            avatarUrl = avatarUrl,
            email = Some(email)
          )
      }
    } catch {
      case e: Exception => {
        Logger.error("[securesocial] error retrieving profile information from Facebook", e)
        throw new AuthenticationException()
      }
    }
  }

}

object FacebookProvider {
  val Facebook = "facebook"

  import ProviderQueryStringResponseParser._

  val fieldsRegex = referenceFieldsRegex + (ExpiresIn -> s""".*expires=([0-9]*).*""".r)
}
