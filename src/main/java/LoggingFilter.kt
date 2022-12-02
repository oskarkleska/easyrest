import io.restassured.filter.Filter
import io.restassured.filter.FilterContext
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class LoggingFilter(private val config: LoggingConfig) : Filter {
    private val log: Logger = LogManager.getLogger(this::class.java)

    @Synchronized
    override fun filter(
        request: FilterableRequestSpecification,
        responseSpec: FilterableResponseSpecification?,
        filterContext: FilterContext,
    ): Response? {
        val response = filterContext.next(request, responseSpec)
        val requestLogs = StringBuilder()
        if (config.logRequests) {
            requestLogs.append("\n*****\t\tREQUEST\t\t*****\n")
            if (config.logUri) {
                requestLogs.append("${ov(request.method)} ${ov(request.uri)}\n")
            }
            if (config.logHeaders) {
                val headers = request.headers.filter {it.name.lowercase() != "authorization"}
                requestLogs.append("Headers: $headers\n")
            }
            if (config.logCookies) {
                requestLogs.append("Cookies: ${ov(request.cookies)}\n")
            }
            if (config.logPayload) {
                if (ov(request.getBody()) != null) requestLogs.append("Body: ${ov(request.getBody())}\n")
                if (ov(request.formParams) != null) requestLogs.append("Form Params: ${ov(request.formParams)}\n")
                if (ov(request.multiPartParams) != null) requestLogs.append("MultiPart: ${ov(request.multiPartParams)}\n")
            }
        }
        val responseLogs = StringBuilder()
        if (config.logResponses) {
            responseLogs.append("\n*****\t\tRESPONSE\t\t*****\n")
            responseLogs.append("Status Line: ${response.statusLine()}\n")
            if (config.logTiming) {
                responseLogs.append("Time taken [ms]: ${response.time}\n")
            }
            if (config.logPayload
                && !response.contentType.contains("pdf")
                && (!response.contentType.contains("html") ||
                        (response.contentType.contains("html") && config.logHtmlResponse))
            ) {
                responseLogs.append("Body: ${response.asString()}")
            }
        }

        log.info("$requestLogs\n+++$responseLogs\n\n")
        return response
    }

    private fun ov(o: Any?): String? {
        return o?.toString()
    }
}