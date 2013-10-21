package securesocial.core.providers

import securesocial.core.{AuthenticationException, OAuth2Info}

class GitHubProviderSpec extends OAuth2ProviderSpec {

  "GitHubProvider" should {
    "parse response query string" in new ProviderTestCase {
      val gitHubProvider = new GitHubProvider(application(GitHubProvider.GitHub))

      val expectedOAuth2Info = OAuth2Info(
        accessToken = testToken,
        tokenType = Some(testTokenType)
      )

      val fullResponse = response withBody s"access_token=$testToken&scope=user%2Cgist&token_type=$testTokenType"
      gitHubProvider.buildInfo(fullResponse) === expectedOAuth2Info

      val shortResponse = response withBody s"access_token=$testToken&token_type=$testTokenType"
      gitHubProvider.buildInfo(shortResponse) === expectedOAuth2Info

      val reversedOrderResponse = response withBody s"token_type=$testTokenType&access_token=$testToken"
      gitHubProvider.buildInfo(reversedOrderResponse) === expectedOAuth2Info

    }

    "throw exception for response query string without access_token" in new ProviderTestCase {
      val gitHubProvider = new GitHubProvider(application(GitHubProvider.GitHub))

      gitHubProvider.buildInfo(response withBody "") must throwA[AuthenticationException]
    }

  }

}
