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
    private val pathPattern = path
    private var tempPathPattern: String? = pathPattern
    val queryParamsPattern = queryParams
    private var tempRequirements: Requirements? = requirements
    private var tempPath: String? = path
    fun setTempRequirements(requirements: Requirements?) {
        this.tempRequirements = requirements
    }
    fun setTempPath(path: String?) {
        this.tempPath = path
        this.tempPathPattern = path
    }

    fun getCurrentRequirements(): Requirements? {
        return if (tempRequirements != null) tempRequirements else requirements
    }

    fun resetModel() {
        this.tempRequirements = requirements
        this.tempPath = path
    }

    fun getCurrentPath(): String? {
        return if(tempPath != null) tempPath else path
    }

    fun getCurrentPathPattern(): String? {
        return if(tempPathPattern != null) tempPathPattern else pathPattern
    }
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
