import io.restassured.http.Method
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.DefaultAsserter.assertEquals

class UnitTest {

    private class DummyModel : EndpointModel(Method.GET, Protocol.HTTPS, "zzz")

    private val dummy = Endpoint<String>(DummyModel())

    @Test
    fun checkSettingParamsForPathWithVarargs() {
        assertEquals(
            "Setting params for path is broken",
            "asd/zxc",
            dummy.setPath("asd/@p1").setParamsForPath("zxc").model.path
        )
        assertEquals(
            "Setting params for path is broken",
            "asd/zxc",
            dummy.setPath("/asd/@p1").setParamsForPath("zxc").model.path
        )
        assertEquals(
            "Setting params for path is broken",
            "zxc/asd/qqq",
            dummy.setPath("@p1/asd/@p2").setParamsForPath("zxc", "qqq").model.path
        )
    }

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