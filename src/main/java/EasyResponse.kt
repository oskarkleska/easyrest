import io.restassured.response.Response
import net.pwall.json.schema.JSONSchema
import org.junit.jupiter.api.Assertions.assertAll

data class EasyResponse(val response: Response, val requirements: Requirements?) {

    fun validate() {
        if (requirements == null) throw RuntimeException("No requirements to validate!")
        assertAll(
            "Checking if response meets specification",
            {
                assert(
                    if (requirements.statusCode == null) {
                        true
                    } else {
                        response.statusCode == requirements.statusCode
                    }
                )
            },
            {
                assert(
                    if (requirements.responseTime == null) {
                        true
                    } else {
                        response.time <= requirements.responseTime)
                    }
                )
            },
            {
                assert(
                    if (requirements.schemaFile != null) {
                        JSONSchema.parseFile(requirements.schemaFile).validate(response.body.print())
                    } else {
                        true
                    }
                )
            })
    }
}