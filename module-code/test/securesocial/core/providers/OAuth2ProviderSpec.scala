package securesocial.core.providers

import org.specs2.specification.Scope
import securesocial.core.OAuth2Settings._
import play.api.test.FakeApplication
import org.specs2.mutable.Specification

trait OAuth2ProviderSpec extends Specification with ProviderResponseMocker {

  private val emptyConfiguration = Map(
    AuthorizationUrl -> "",
    AccessTokenUrl -> "",
    ClientId -> "",
    ClientSecret -> ""
  )

  trait ProviderTestCase extends Scope {

    def application(providerId: String, configuration: Map[String, _ <: Any] = Map.empty) = {
      val propertyKeyPrefix = s"securesocial.$providerId."
      val additionalConfiguration: Map[String, _ <: Any] = (emptyConfiguration ++ configuration).map({
        case (key, value) => (propertyKeyPrefix + key, value)
      })
      FakeApplication(additionalConfiguration = additionalConfiguration)
    }

  }

}
