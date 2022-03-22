import io.restassured.RestAssured.given
import io.restassured.http.Cookies
import io.restassured.http.Headers
import io.restassured.http.Method
import java.io.File

data class Endpoint(
    val method: Method,
    val protocol: Protocol,
    val baseUri: String,
    var path: String? = null,
    var cookies: Cookies? = null,
    var headers: Headers? = null,
    var body: String? = null,
    var queryParams: Map<String, Any>? = null,
    val requirements: Requirements? = null
) {

    fun call(): EasyResponse {
        val response = given()
            .baseUri(protocol.protocolPart + baseUri)
            .log().all()
            .headers(headers ?: Headers())
            .cookies(cookies?: Cookies())
            .queryParams(queryParams ?: mapOf<String, Any>())
            .body(body ?: "")
            .request(method, path ?: "")
        return EasyResponse(response, requirements)
    }
}

data class Requirements(
    val statusCode: Int? = null,
    val schemaFile: String? = null,
    val responseTime: Long? = null
)

enum class Protocol(val protocolPart: String) {
    HTTP("http://"), HTTPS("https://")
}
