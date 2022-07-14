import Utils.replaceIfExists
import Utils.retry
import io.restassured.RestAssured.given
import io.restassured.http.Cookies
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

open class E<ReturnedType : Any>(
    val model: EndpointModel,
    var classToken: Class<ReturnedType>
) {
    private lateinit var response: Response
    private var rsp: RequestSpecification = given().filter(LoggingFilter())

    init {
        if (model.headers != null) rsp.headers(model.headers)
        if (model.body != null) rsp.body(model.body)
        if (model.cookies != null) rsp.cookies(model.cookies)
        if (model.queryParams != null) rsp.queryParams(model.queryParams)
        rsp.baseUri(model.protocol.protocolPart + model.baseUri)
    }

    companion object {
        inline operator fun <reified ReturnedType : Any> invoke(
            model: EndpointModel
        ): E<ReturnedType> = E(
            model = model,
            classToken = ReturnedType::class.java
        )
    }

    /**
     * Call, check & cast.
     * Retries call, validation and casting according to config.
     */
    fun ccc(): ReturnedType {
        if (classToken == Unit::class.java || classToken == Void::class.java) {
            throw IllegalCallerException("Response cant be cast onto Unit or Void class. Try calling cc() method.")
        }
        return cc().andCastAs(classToken)
    }

    fun getResponse(): Response {
        if (!this::response.isInitialized) {
            throw Exceptions.EndpointNotCalledYetException()
        }
        return response
    }

    fun call(): EasyResponse {
        response = rsp
            .request(model.method, model.path ?: "")
            .then().extract().response()
        return EasyResponse(response, model.requirements, model)
    }

    fun cc(): EasyResponse {
        return retry {
            call().validate()
        }
    }

    fun setHeaders(headers: Headers): E<ReturnedType> {
        this.model.headers = headers
        return this
    }

    fun overrideHeader(header: Header): E<ReturnedType> {
        this.model.headers?.removeAll { it.hasSameNameAs(header) }
        this.model.headers?.asList()?.add(header)
        return this
    }

    fun setCookies(cookies: Cookies): E<ReturnedType> {
        this.model.cookies = cookies
        return this
    }

    fun setPath(path: String): E<ReturnedType> {
        this.model.path = path
        return this
    }

    fun setQueryParams(queryParams: Map<String, Any>?): E<ReturnedType> {
        this.model.queryParams = queryParams
        return this
    }

    fun setBody(body: Any): E<ReturnedType> {
        this.model.body = body
        rsp.body(this.model.body)
        return this
    }

    fun overrideRequirements(requirements: Requirements?): E<ReturnedType> {
        this.model.requirements = requirements
        return this
    }

    fun setParamsForPath(vararg params: String): E<ReturnedType> {
        if (model.path == null) throw Exceptions.NoParamsException("Path is null")
        var paramsCount = 0
        var newPath = ""
        model.path!!.split("/").filter { it.isNotEmpty() }.forEach {
            if (newPath.isNotEmpty()) newPath += "/"
            if (it[0].toString() == "@") {
                try {
                    newPath += params[paramsCount]
                } catch (e: IndexOutOfBoundsException) {
                    throw Exception("Insufficient amount of params passed to set for path $model.path, only got ${params.size} params\n + $e")
                }
                paramsCount++
            } else {
                newPath += it
            }
        }
        model.path = newPath
        return this
    }

    fun setParamsForPath(params: Map<String, String>): E<ReturnedType> {
        if (model.path.isNullOrEmpty()) throw Exceptions.NoParamsException("Path is null")
        var newPath: String = model.path!!
        if (newPath[0] == "/".toCharArray()[0]) newPath = newPath.substring(1, newPath.length)
        params.forEach { (t, u) -> newPath = newPath.replace("@", "").replaceIfExists(t, u) }
        model.path = newPath
        return this
    }
}
