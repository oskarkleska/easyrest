import io.restassured.response.Response
import net.pwall.json.schema.JSONSchema
import org.junit.jupiter.api.Assertions.assertAll

data class EasyResponse(val response: Response, val requirements: Requirements?) {

    fun validate(): EasyResponse {
        if (requirements == null) return this
        assertAll(
            "Checking if response meets specification",
            {
                assert(
                    if (requirements.statusCode == null) {
                        true
                    } else {
                        response.statusCode == requirements.statusCode
                    }
                ) { "Wrong status code, expected ${requirements.statusCode} but got ${response.statusCode}" }
            },
            {
                assert(
                    if (requirements.responseTime == null) {
                        true
                    } else {
                        response.time <= requirements.responseTime
                    },
                ) { "Too long response time, expected ${requirements.responseTime} but got ${response.time}" }
            },
            {
                assert(
                    if (requirements.schemaFile != null) {
                        JSONSchema.parseFile(requirements.schemaFile).validate(response.body.print())
                    } else {
                        true
                    }
                ) { "Schema does not meet requirements, check response" }
            })
        return this
    }

    fun <T: Any> andCastAs(clazz: Class<T>) : T {
        return this.response.`as`(clazz)
    }
}