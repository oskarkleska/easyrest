import Utils.replaceIfExists
import Utils.retry
import io.restassured.RestAssured.given
import io.restassured.http.Cookies
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.http.Method
import io.restassured.specification.RequestSpecification
import java.util.*

abstract class Endpoint(
    private val method: Method,
    protocol: Protocol,
    baseUri: String,
    var path: String? = null,
    var cookies: Cookies? = null,
    var headers: Headers? = null,
    var body: Any? = null,
    private var queryParams: Map<String, Any>? = null,
    private var requirements: Requirements? = null,
) {
    private var rsp: RequestSpecification = given()

    init {
        if (headers != null) rsp.headers(headers)
        if (body != null) rsp.body(body)
        if (cookies != null) rsp.cookies(cookies)
        if (queryParams != null) rsp.queryParams(queryParams)
        if (Random().nextBoolean()) rsp.log().all() // TODO: add config for logging
        rsp.baseUri(protocol.protocolPart + baseUri)
    }


    /**
     * Call, check & cast.
     * Retries call, validation and casting according to config.
     */
    fun <T:Any> ccc(clazz: Class<T>): T {
        return retry {
            call().validate().andCastAs(clazz)
        }
    }

    fun call(): EasyResponse {
        val response = rsp
            .request(method, path ?: "")
            .then().log().all().and().extract().response()
        return EasyResponse(response, requirements)
    }

    fun callAndValidate(): EasyResponse {
        return retry {
            call().validate()
        }
    }

    fun setHeaders(headers: Headers): Endpoint {
        this.headers = headers
        return this
    }

    fun overrideHeader(header: Header): Endpoint {
        this.headers?.removeAll { it.hasSameNameAs(header) }
        this.headers?.asList()?.add(header)
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

    fun setQueryParams(queryParams: Map<String, Any>?): Endpoint {
        this.queryParams = queryParams
        return this
    }

    fun setBody(body: Any): Endpoint {
        this.body = body
        return this
    }

    fun overrideRequirements(requirements: Requirements?): Endpoint {
        this.requirements = requirements
        return this
    }

    fun setParamsForPath(vararg params: String): Endpoint {
        if (path == null) throw Exceptions.NoParamsException("Path is null")
        var paramsCount = 0
        var newPath = ""
        path!!.split("/").filter { it.isNotEmpty() }.forEach {
            if (newPath.isNotEmpty()) newPath += "/"
            if (it[0].toString() == "@") {
                try {
                    newPath += params[paramsCount]
                } catch (e: IndexOutOfBoundsException) {
                    throw Exception("Insufficient amount of params passed to set for path $path, only got ${params.size} params\n + $e")
                }
                paramsCount++
            } else {
                newPath += it
            }
        }
        path = newPath
        return this
    }

    fun setParamsForPath(params: Map<String, String>): Endpoint {
        if (path.isNullOrEmpty()) throw Exceptions.NoParamsException("Path is null")
        var newPath: String = path!!
        if (newPath[0] == "/".toCharArray()[0]) newPath = newPath.substring(1, newPath.length)
        params.forEach { (t, u) -> newPath = newPath.replace("@", "").replaceIfExists(t, u) }
        path = newPath
        return this
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
