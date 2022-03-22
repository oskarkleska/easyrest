import io.restassured.RestAssured.given
import io.restassured.http.Cookies
import io.restassured.http.Headers
import io.restassured.http.Method
import io.restassured.response.Response

data class Endpoint(
    val method: Method,
    val protocol: Protocol,
    val baseUri: String,
    val path: String = "",
    val cookies: Cookies = Cookies(),
    val headers: Headers = Headers(),
    val body: String = "",
    val queryParams: Map<String,Any> = mapOf()) {

    fun call(): Response {
        return given()
            .baseUri(protocol.protocolPart + baseUri)
            .log().all()
            .headers(headers)
            .cookies(cookies)
            .queryParams(queryParams)
            .body(body)
            .request(method, path)
    }
}

enum class Protocol(val protocolPart: String) {
    HTTP("http://"), HTTPS("https://")
}
