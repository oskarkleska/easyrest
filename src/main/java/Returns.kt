import Utils.putAllNotDuplicate
import Utils.replaceIfExists
import Utils.retry
import helpers.HttpConfig
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.Cookies
import io.restassured.parsing.Parser
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class Returns<ReturnedType : Any>(
    val model: EndpointModel,
    val loggingConfig: LoggingConfig = TestManager.getConfig().loggingConfig,
    val httpConfig: HttpConfig = HttpConfig(),
    var classToken: Class<ReturnedType>,
) {
    private lateinit var response: Response
    private lateinit var rsp: RequestSpecification
    private val log: Logger = LogManager.getLogger(this::class.java)

    init {
        RestAssured.defaultParser = Parser.JSON
    }
    companion object {

        inline operator fun <reified ReturnedType : Any> invoke(
            model: EndpointModel,
        ): Returns<ReturnedType> = Returns(
            model = model,
            classToken = ReturnedType::class.java
        )

        inline operator fun <reified ReturnedType : Any> invoke(
            model: EndpointModel,
            httpConfig: HttpConfig,
        ): Returns<ReturnedType> = Returns(
            model = model,
            httpConfig = httpConfig,
            classToken = ReturnedType::class.java
        )

        inline operator fun <reified ReturnedType : Any> invoke(
            model: EndpointModel,
            loggingConfig: LoggingConfig,
        ): Returns<ReturnedType> = Returns(
            model = model,
            loggingConfig = loggingConfig,
            classToken = ReturnedType::class.java
        )

        inline operator fun <reified ReturnedType : Any> invoke(
            model: EndpointModel,
            httpConfig: HttpConfig,
            loggingConfig: LoggingConfig,
        ): Returns<ReturnedType> = Returns(
            model = model,
            httpConfig = httpConfig,
            loggingConfig = loggingConfig,
            classToken = ReturnedType::class.java
        )
    }

    inner class Validation(val returnedObject: ReturnedType, val response: Response)

    /**
     * Call, check & cast.
     * Retries call, validation and casting according to config.
     */
    fun ccc(
        retryLimit: Int = TestManager.getConfig().defaultRetryCount,
        interval: Long = TestManager.getConfig().defaultPollingDelay,
    ): ReturnedType {
        if (classToken == Unit::class.java || classToken == Void::class.java) {
            throw IllegalCallerException("Response cant be cast onto Unit or Void class. Try calling cc() method.")
        }
        return cc(retryLimit, interval).andCastAs(classToken)
    }

    fun waitFor(
        timeout: Long = 60000,
        interval: Long = 2000,
        validate: (x: Validation) -> Boolean,
    ): ReturnedType {
        if (timeout <= 0 || interval <= 0 || interval > timeout) throw IllegalArgumentException("Timeout and interval must be positive values with interval smaller or equal to timeout")
        var easyResponse: EasyResponse
        val times = timeout / interval
        for (i in 0..times) {
            try {
                easyResponse = call().validate()
                val responseObject = easyResponse.andCastAs(classToken)
                assert(
                    validate(
                        Validation(
                            responseObject,
                            easyResponse.response
                        )
                    )
                ) { "Polling condition not met on endpoint ${easyResponse.getEndpointPattern()} after ${i+1 * interval / 1000} seconds" }
                return responseObject
            } catch (e: AssertionError) {
                if (i + 1 < times) {
                    log.info("Expectations not met: ${e.localizedMessage}")
                    Thread.sleep(interval)
                } else throw e
            } catch (t: Throwable) {
                log.error("Unexpected exception:\n ${t.localizedMessage}\n${t.stackTraceToString()}")
                throw t
            }
        }
        throw Error("This should not happen! Polling issue")
    }

    fun getResponse(): Response {
        if (!this::response.isInitialized) {
            throw Exceptions.EndpointNotCalledYetException()
        }
        return response
    }

    private fun call(): EasyResponse {
        rsp = given().filter(LoggingFilter(loggingConfig))
        loadHttpConfig()
        loadRequestSpecification()
        response = rsp
            .request(model.method, model.path ?: "")
            .then().extract().response()
        return EasyResponse(response, model)
    }

    private fun loadHttpConfig() {
        rsp.urlEncodingEnabled(httpConfig.urlEncodingEnabled)
    }

    private fun loadRequestSpecification() {
        if (model.headers != null) rsp.headers(model.headers)
        if (model.body != null) rsp.body(model.body)
        if (model.cookies != null) rsp.cookies(model.cookies)
        if (model.queryParams != null) rsp.queryParams(model.queryParams)
        if (model.formParams != null) rsp.formParams(model.formParams)
        rsp.baseUri(model.protocol.protocolPart + model.baseUri)
    }

    /**
     * Call & check
     */
    fun cc(
        retryLimit: Int = TestManager.getConfig().defaultRetryCount,
        interval: Long = TestManager.getConfig().defaultPollingDelay,
    ): EasyResponse {
        return retry(retryLimit, interval) {
            call().validate()
        }
    }

    fun setHeaders(headers: MutableMap<String, Any>): Returns<ReturnedType> {
        this.model.headers = headers
        return this
    }


    fun addHeaders(headers: MutableMap<String, Any>): Returns<ReturnedType> {
        if (this.model.headers == null) {
            this.model.headers = headers
        } else {
            this.model.headers!!.putAllNotDuplicate(headers)
        }
        return this
    }

    fun overrideHeaders(headers: MutableMap<String, Any>): Returns<ReturnedType> {
        for (header in headers) {
            this.model.headers?.replace(header.key, header.value)
        }
        return this
    }

    fun setCookies(cookies: Cookies): Returns<ReturnedType> {
        this.model.cookies = cookies
        return this
    }

    fun setPath(path: String): Returns<ReturnedType> {
        this.model.path = path
        return this
    }

    fun setQueryParams(queryParams: MutableMap<String, Any>?): Returns<ReturnedType> {
        this.model.queryParams = queryParams
        return this
    }

    fun setFormParams(mutableMap: MutableMap<String, Any>) {
        this.model.formParams = mutableMap
    }

    fun setBody(body: Any): Returns<ReturnedType> {
        this.model.body = body
        return this
    }

    fun overrideRequirements(requirements: Requirements?): Returns<ReturnedType> {
        this.model.setTempRequirements(requirements)
        return this
    }

    fun setParamsForPath(params: Map<String, String>): Returns<ReturnedType> {
        if (model.path.isNullOrEmpty()) throw Exceptions.NoParamsException("Path is null")
        var newPath: String = model.path!!
        if (newPath[0] == '/') newPath = newPath.substring(1, newPath.length)
        params.forEach { (t, u) -> newPath = newPath.replace("@", "").replaceIfExists(t, u) }
        model.path = newPath
        return this
    }
}
