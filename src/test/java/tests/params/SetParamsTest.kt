package tests.params

import Exceptions
import Returns
import io.restassured.http.Method
import models.Protocol
import models.ServiceModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.DefaultAsserter.assertEquals

class SetParamsTest {

    private val dummy = Returns<String>(ServiceModel(Protocol.HTTP, "asd").EndpointModel(Method.GET))

    @Test
    fun checkSettingParamsForPathWithMap() {
        assertEquals(
            "Setting params for path is broken",
            "asd/zxc",
            dummy.overridePath("asd/@p1").setParamsForPath(mapOf("p1" to "zxc")).model.getCurrentPath()
        )
        assertEquals(
            "Setting params for path is broken",
            "asd/zxc",
            dummy.overridePath("/asd/@p1").setParamsForPath(mapOf("p1" to "zxc")).model.getCurrentPath()
        )
        assertEquals(
            "Setting params for path is broken",
            "zxc/asd/qqq",
            dummy.overridePath("@p1/asd/@p2").setParamsForPath(mapOf("p1" to "zxc", "p2" to "qqq")).model.getCurrentPath()
        )
    }

    @Test
    fun checkIfReplaceIfExistsWorks() {
        assertThrows<Exceptions.StringNotFoundException>("p2 not found in asd/@p1") {
            dummy.overridePath("asd/@p1").setParamsForPath(mapOf("p2" to "zxc"))
        }
    }
}