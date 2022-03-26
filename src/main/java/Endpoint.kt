import io.restassured.RestAssured.given
import io.restassured.http.Cookies
import io.restassured.http.Headers
import io.restassured.http.Method

data class Endpoint(
    val method: Method,
    val protocol: Protocol,
    val baseUri: String,
    var path: String? = null,
    var cookies: Cookies? = null,
    var headers: Headers? = null,
    var body: Any? = null,
    var queryParams: Map<String, Any>? = null,
    var requirements: Requirements? = null
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
            .then().log().all().and().extract().response()
        return EasyResponse(response, requirements)
    }

    fun setHeaders(headers: Headers): Endpoint {
        this.headers = headers
        return this
    }

    fun setCookies(cookies: Cookies): Endpoint {
        this.cookies = cookies
        return this
    }

    fun setPath(path: String): Endpoint {
        this.path = path
        return this
    }

    fun setQueryParams(queryParams: Map<String, Any>?) : Endpoint {
        this.queryParams = queryParams
        return this
    }

    fun setBody(body: Any) : Endpoint {
        this.body = body
        return this
    }

    fun overrideRequirements(requirements: Requirements?) : Endpoint {
        this.requirements = requirements
        return this
    }

    fun setParamsForPath(vararg params: String) {
        if(path==null) throw Exceptions.NoParamsException("No params in path to parse, they should begin with `$` sign")
        var paramsLeft = params.size
        var paramsCount = 0
        path!!.split("/").forEach{
            if(it[0].toString() == "$") {
                it = params[paramsCount]
                paramsCount++
            }
        }
        //todo fix this shit :this:
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
