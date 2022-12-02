import io.restassured.http.Cookies
import io.restassured.http.Method

open class EndpointModel(
    val method: Method,
    val protocol: Protocol,
    val baseUri: String,
    var path: String? = null,
    var cookies: Cookies? = null,
    var headers: MutableMap<String, Any>? = null,
    var body: Any? = null,
    var queryParams: MutableMap<String, Any>? = null,
    var formParams: MutableMap<String, Any>? = null,
    var requirements: Requirements? = null,
) {
    val pathPattern = path
    val queryParamsPattern = queryParams
}

data class Requirements(
    val statusCode: Int? = null,
    val schemaFile: String? = null,
    val responseTime: Long? = null,
)

fun code(code: Int) = Requirements(statusCode = code)

enum class Protocol(val protocolPart: String) {
    HTTP("http://"), HTTPS("https://")
}
