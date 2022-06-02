data class EasyRestConfig(
    val loggingConfig: LoggingConfig,
    val defaultRetryCount: Int,
    val defaultPollingDelay: Long,
)

data class LoggingConfig(
    val logRequests: Boolean,
    val logResponses: Boolean,
    val logHeaders: Boolean,
    val logCookies: Boolean,
    val logTiming: Boolean,
    val logPayload: Boolean,
    val logUri: Boolean,
    val logQueryParams: Boolean,
    val logHtmlResponse: Boolean
)
