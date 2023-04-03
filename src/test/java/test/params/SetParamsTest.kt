package test.params

import EndpointModel
import Exceptions
import Protocol
import Returns
import io.restassured.http.Method
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.DefaultAsserter.assertEquals

class SetParamsTest {

    private class DummyModel : EndpointModel(Method.GET, Protocol.HTTPS, "zzz")

    private val dummy = Returns<String>(DummyModel())

    @Test
    fun checkSettingParamsForPathWithMap() {
        assertEquals(
            "Setting params for path is broken",
            "asd/zxc",
            dummy.setPath("asd/@p1").setParamsForPath(mapOf("p1" to "zxc")).model.path
        )
        assertEquals(
            "Setting params for path is broken",
            "asd/zxc",
            dummy.setPath("/asd/@p1").setParamsForPath(mapOf("p1" to "zxc")).model.path
        )
        assertEquals(
            "Setting params for path is broken",
            "zxc/asd/qqq",
            dummy.setPath("@p1/asd/@p2").setParamsForPath(mapOf("p1" to "zxc", "p2" to "qqq")).model.path
        )
    }

    @Test
    fun checkIfReplaceIfExistsWorks() {
        assertThrows<Exceptions.StringNotFoundException>("p2 not found in asd/@p1") {
            dummy.setPath("asd/@p1").setParamsForPath(mapOf("p2" to "zxc"))
        }
    }
}