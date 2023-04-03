package stubs.callcheckandcast

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.google.gson.Gson
import tests.callcheckandcast.SimpleResponseForCasting
import tests.crudtest.RandomResourceResponse
import kotlin.collections.ArrayList

object CCCStubs {
    private val resourceList: ArrayList<RandomResourceResponse> = arrayListOf()

    fun stubGetCCC(): StubMapping = stubFor(
        get(urlEqualTo("/ccc")).willReturn(
            aResponse()
                .withBody(toJson(SimpleResponseForCasting("uniqueId", "uniqueName", 123)))
                .withStatus(200)
        )
    )

    fun stubGetWithRetries() {
        stubFor(
            get(urlEqualTo("/ccc/retries")).inScenario("Retries hp")
                .whenScenarioStateIs(STARTED)
                .willReturn(
                    aResponse()
                        .withStatus(404)
                )
                .willSetStateTo("FirstFailed")
        )
        stubFor(
            get(urlEqualTo("/ccc/retries")).inScenario("Retries hp")
                .whenScenarioStateIs("FirstFailed")
                .willReturn(
                    aResponse()
                        .withStatus(404)
                )
                .willSetStateTo("SecondFailed")
        )
        stubFor(
            get(urlEqualTo("/ccc/retries")).inScenario("Retries hp")
                .whenScenarioStateIs("SecondFailed")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )
    }
    private fun toJson(obj: Any) = Gson().toJson(obj)

}