package test.callcheckandcast

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.*
import src.model.*
import src.model.ccc.GetSimpleResponse
import stubs.callcheckandcast.CCCStubs.stubGetCCC
import test.BaseWiremockTest
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
}