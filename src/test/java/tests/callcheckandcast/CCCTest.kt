package tests.callcheckandcast

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.*
import src.model.*
import src.endpoints.ccc.GetSimpleResponse
import src.endpoints.ccc.Retries
import stubs.callcheckandcast.CCCStubs.stubGetCCC
import stubs.callcheckandcast.CCCStubs.stubGetWithRetries
import tests.BaseWiremockTest
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CCCTest : BaseWiremockTest(){

    @Test
    fun castResponseToClassHP() {
        stubGetCCC()
        val simpleResponse = GetSimpleResponse().go()
        assert(simpleResponse.id == "uniqueId")
        assert(simpleResponse.name == "uniqueName")
        assert(simpleResponse.number == 123)
    }

    @Test
    fun castResponseToClassFail() {
        stubGetCCC()
        assertThrows<Exceptions.ResponseCastException>("Response cannot be cast to WrongResponseForCasting"){
            GetSimpleResponse().failCasting()
        }
    }

    @Test
    fun checkSchemaHP() {

    }

    @Test
    fun checkSchemaFail() {

    }

    @Test
    fun checkResponseTimeHP() {

    }

    @Test
    fun checkResponseTimeFail() {

    }

    @Test
    fun retriesHP() {
        stubGetWithRetries()
        Retries().go()
    }

    @Test
    fun retriesFail() {
        stubGetWithRetries()
        assertThrows<AssertionError>("Wrong status code calling GET localhost:8080/ccc/retries, expected 200 but got 404"){
            Retries().fail()
        }
    }
}