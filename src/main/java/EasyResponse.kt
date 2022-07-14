import Utils.softAssert
import io.restassured.response.Response
import net.pwall.json.schema.JSONSchema
import org.junit.jupiter.api.Assertions.assertAll

data class EasyResponse(val response: Response, val requirements: Requirements?, val model: EndpointModel) {

    fun validate(): EasyResponse {
        if (requirements == null) return this
        assertAll(
            "Checking if response to ${getCalledEndpoint()} meets specification",
            {
                assert(
                    if (requirements.statusCode == null) {
                        true
                    } else {
                        response.statusCode == requirements.statusCode
                    }
                ) { "Wrong status code calling ${getCalledEndpoint()}, expected ${requirements.statusCode} but got ${response.statusCode}" }
            },
            {
                assert(
                    if (requirements.schemaFile != null) {
                        JSONSchema.parseFile(requirements.schemaFile).validate(response.body.print())
                    } else {
                        true
                    }
                ) { "Schema of ${getCalledEndpoint()} does not meet requirements, check response" }
            },
            {
                softAssert("Too long response time calling ${getCalledEndpoint()}, expected ${requirements.responseTime}ms but got ${response.time}ms") {
                    if (requirements.responseTime == null)
                        true
                    else
                        response.time <= requirements.responseTime
                }
            }
        )
        return this
    }

    fun <T : Any> andCastAs(clazz: Class<T>): T {
        return this.response.`as`(clazz)
    }

    private fun getCalledEndpoint() : String {
        return "${model.method} ${model.baseUri}${model.path}"
    }
}