data class EasyRestConfig(
    val loggingConfig: LoggingConfig = LoggingConfig(),
    val defaultRetryCount: Int = 5,
    val defaultPollingDelay: Long = 500L,
)

data class LoggingConfig(
    val logRequests: Boolean = true,
    val logResponses: Boolean = true,
    val logHeaders: Boolean = true,
    val logCookies: Boolean = false,
    val logTiming: Boolean = true,
    val logPayload: Boolean = true,
    val logUri: Boolean = true,
    val logQueryParams: Boolean = true,
    val logHtmlResponse: Boolean = false
)
