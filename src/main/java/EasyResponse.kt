import Utils.softAssert
import io.restassured.response.Response
import net.pwall.json.schema.JSONSchema
import org.junit.jupiter.api.Assertions.assertAll

data class EasyResponse(val response: Response, val model: EndpointModel) {

    fun validate(): EasyResponse {
        if (model.requirements == null) {
            return this
        }
        assertAll(
            { // Check status code.
                assert(
                    if (model.requirements!!.statusCode == null) {
                        true
                    } else {
                        response.statusCode == model.requirements!!.statusCode
                    }
                ) {
                    "Wrong status code calling ${getEndpointPattern()}, expected ${model.requirements!!.statusCode} but got ${response.statusCode}"
                }
            },
            { // Check schema file
                assert(
                    if (model.requirements!!.schemaFile != null) {
                        JSONSchema.parseFile(model.requirements!!.schemaFile!!).validate(response.body.print())
                    } else {
                        true
                    }
                ) { "Schema of ${getEndpointPattern()} does not meet requirements, check response" }
            },
            { // Soft assert on response time
                softAssert("Too long response time calling ${getEndpointPattern()}, expected ${model.requirements!!.responseTime}ms but got ${response.time}ms") {
                    if (model.requirements!!.responseTime == null)
                        true
                    else
                        response.time <= model.requirements!!.responseTime!!
                }
            }
        )
        return this
    }

    fun <T : Any> andCastAs(clazz: Class<T>): T {
        return this.response.`as`(clazz)
    }

    private fun getCalledEndpoint(): String {
        val params = if (model.queryParams.isNullOrEmpty()) "" else "?${model.queryParams.toString()}"
        return "${model.method} ${model.baseUri}${model.path}$params"
    }

    fun getEndpointPattern(): String {
        val params = if (model.queryParamsPattern.isNullOrEmpty()) "" else "?${model.queryParamsPattern}"
        return "${model.method} ${model.baseUri}${model.pathPattern}$params"
    }
}