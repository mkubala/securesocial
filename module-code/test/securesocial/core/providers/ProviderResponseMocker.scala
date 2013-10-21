package securesocial.core.providers

import play.api.libs.ws.Response
import play.api.libs.json.JsValue
import org.specs2.mock.Mockito

trait ProviderResponseMocker extends Mockito {

  val testToken = "e72e16c7e42f292c6912e7710c838347ae178b4a"
  val testTokenType = "bearer"

  def response = mock[Response]

  implicit class ResponseMockBuilder(responseMock: Response) {
    def withBody(body: String): Response = {
      responseMock.body returns body
      responseMock
    }

    def withJson(json: JsValue): Response = {
      responseMock.json returns json
      responseMock
    }
  }

}
