import Utils.softAssert
import io.restassured.response.Response
import models.ServiceModel
import net.pwall.json.schema.JSONSchema
import org.junit.jupiter.api.Assertions.assertAll

data class EasyResponse(val response: Response, val model: ServiceModel.EndpointModel) {

    fun validate(): EasyResponse {
        val requirements = model.getCurrentRequirements() ?: return this
        assertAll(
            { // Check status code.
                assert(
                    if (requirements.statusCode == null) {
                        true
                    } else {
                        response.statusCode == requirements.statusCode
                    }
                ) {
                    "Wrong status code calling ${getEndpointPattern()}, expected ${requirements.statusCode} but got ${response.statusCode}"
                }
            },
            { // Check schema file
                assert(
                    if (requirements.schemaFile != null) JSONSchema.parseFile(requirements.schemaFile)
                        .validate(response.body.print()) else true
                ) { "Schema of ${getEndpointPattern()} does not meet requirements, check response" }
            },
            { // Soft assert on response time
                softAssert("Too long response time calling ${getEndpointPattern()}, expected ${requirements.responseTime}ms but got ${response.time}ms") {
                    if (requirements.responseTime == null)
                        true
                    else
                        response.time <= requirements.responseTime
                }
            }
        )
        model.resetModel()
        return this
    }

    fun <T : Any> andCastAs(clazz: Class<T>): T {
        return try {
            this.response.`as`(clazz)
        } catch (e: Exception) {
            throw Exceptions.ResponseCastException("Response cannot be cast to ${clazz.name}")
        }
    }

    private fun getCalledEndpoint(): String {
        val params = if (model.queryParams.isNullOrEmpty()) "" else "?${model.queryParams.toString()}"
        return "${model.method} ${model.getBaseUri()}${model.getCurrentPath()}$params"
    }

    fun getEndpointPattern(): String {
        val params = if (model.queryParamsPattern.isNullOrEmpty()) "" else "?${model.queryParamsPattern}"
        return "${model.method} ${model.getBaseUri()}${model.getCurrentPathPattern()}$params"
    }
}