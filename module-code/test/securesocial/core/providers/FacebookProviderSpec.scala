package securesocial.core.providers

import securesocial.core.{AuthenticationException, OAuth2Info}

class FacebookProviderSpec extends OAuth2ProviderSpec {

  "FacebookProvider" should {
    "parse response query string" in new ProviderTestCase {
      val facebookProvider = new FacebookProvider(application(FacebookProvider.Facebook))

      val expectedOAuth2Info = OAuth2Info(
        accessToken = testToken
      )

      val shortResponse = response withBody s"access_token=$testToken"
      facebookProvider.buildInfo(shortResponse) === expectedOAuth2Info

      val fullResponse = response withBody s"access_token=$testToken&expires=123"
      facebookProvider.buildInfo(fullResponse) === expectedOAuth2Info.copy(expiresIn = Some(123))

      val reversedFullResponse = response withBody s"expires=123&access_token=$testToken"
      facebookProvider.buildInfo(reversedFullResponse) === expectedOAuth2Info.copy(expiresIn = Some(123))

    }

    "throw exception for response query string without access_token" in new ProviderTestCase {
      val facebookProvider = new FacebookProvider(application(FacebookProvider.Facebook))

      facebookProvider.buildInfo(response withBody "") must throwA[AuthenticationException]
    }

  }

}
