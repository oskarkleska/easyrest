import io.restassured.filter.Filter
import io.restassured.filter.FilterContext
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class LoggingFilter : Filter {
    private val config = TestManager.getConfig().loggingConfig
    private val log: Logger = LogManager.getLogger(this::class.java)

    @Synchronized
    override fun filter(
        request: FilterableRequestSpecification,
        responseSpec: FilterableResponseSpecification?,
        filterContext: FilterContext
    ): Response? {
        val response = filterContext.next(request, responseSpec)
        val requestLogs = StringBuilder()
        if (config.logRequests) {
            requestLogs.append("\n*****\t\tREQUEST\t\t*****\n")
            if (config.logUri) {
                requestLogs.append("URI: ${ov(request.method)}\n")
            }
            if (config.logHeaders) {
                requestLogs.append("Headers: ${ov(request.headers)}\n")
            }
            if (config.logCookies) {
                requestLogs.append("Cookies: ${ov(request.cookies)}\n")
            }
            if (config.logPayload) {
                requestLogs.append("Body: ${ov(request.headers)}\n")
                requestLogs.append("Form Params: ${ov(request.headers)}\n")
                requestLogs.append("MultiPart: ${ov(request.headers)}\n")
            }
        }
        val responseLogs = StringBuilder()
        if (config.logResponses) {
            responseLogs.append("\n*****\t\tRESPONSE\t\t*****\n")
            responseLogs.append("Status Line: ${response.statusLine()}")
            if(config.logTiming) {
                responseLogs.append("Time taken [ms]: ${response.time}\n")
            }
            if(config.logPayload) {
                responseLogs.append("Body: ${response.asString()}")
            }
        }

        log.info("\n\n$requestLogs + $responseLogs\n")
        return response
    }

    private fun ov(o: Any?): String? {
        return o?.toString()
    }
}