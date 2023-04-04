package tests.callcheckandcast

import Utils.softAssertions
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.*
import src.model.*
import src.endpoints.ccc.GetSimpleResponse
import src.endpoints.ccc.Retries
import stubs.callcheckandcast.CCCStubs.stubGetCCC
import stubs.callcheckandcast.CCCStubs.stubGetWithRetries
import stubs.callcheckandcast.CCCStubs.stubGetWithRetriesForOverridenRequirements
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
    fun retriesHP() {
        stubGetWithRetries()
        Retries().go()
    }

    @Test
    fun retriesFailOnCode() {
        stubGetWithRetries()
        assertThrows<AssertionError>("Wrong status code calling GET localhost:8080/ccc/retries, expected 200 but got 404"){
            Retries().failCode404()
        }
    }

    @Test
    fun retriesFailOnResponseTime() {
        stubGetWithRetries()
        softAssertions.removeAll{true}
        Retries().failTime()
        assert(softAssertions.size == 1)
        assert(softAssertions[0].startsWith("Too long response time calling GET localhost:8080/ccc/retries, expected 1ms"))
        softAssertions.removeAt(0)
    }

    @Test
    fun overrideRequirementsAndPathWorksForAllRequestsInOneCC() {
        stubGetWithRetriesForOverridenRequirements()
        stubGetWithRetries()
        val retries = Retries()
        retries.overridenReqsHPs()
        retries.go() // todo fix issue with permanent overriding model of an existing endpoint definition
        // so far overriding is fixed for requirements and path.
        // to fix: headers, body, cookies, queryParams, formParams
    }
}