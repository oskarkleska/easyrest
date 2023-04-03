package tests

import com.github.tomakehurst.wiremock.client.WireMock
import org.apache.logging.log4j.ThreadContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import src.model.WireMockInstance

open class BaseWiremockTest {

    init {
        ThreadContext.put("ROUTING_KEY", "example_test")
    }

    @BeforeEach
    fun resetScenarios() {
        WireMock.resetAllScenarios()
    }

    companion object {
        @JvmStatic
        @AfterAll
        fun shutDownServer() {
            WireMockInstance.stopServer()
        }

        @JvmStatic
        @BeforeAll
        fun startServer() {
            WireMockInstance.startServer()
        }
    }
}