import io.restassured.http.Cookies
import io.restassured.http.Headers
import io.restassured.http.Method

open class EndpointModel(
    val method: Method,
    val protocol: Protocol,
    val baseUri: String,
    var path: String? = null,
    var cookies: Cookies? = null,
    var headers: Headers? = null,
    var body: Any? = null,
    var queryParams: Map<String, Any>? = null,
    var requirements: Requirements? = null
)

data class Requirements(
    val statusCode: Int? = null,
    val schemaFile: String? = null,
    val responseTime: Long? = null
)

enum class Protocol(val protocolPart: String) {
    HTTP("http://"), HTTPS("https://")
}
