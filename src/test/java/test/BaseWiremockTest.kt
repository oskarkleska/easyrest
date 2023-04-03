package test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.logging.log4j.ThreadContext

open class BaseWiremockTest {
    init {
        ThreadContext.put("ROUTING_KEY", "example_test")
        WireMockServer(WireMockConfiguration.options().port(8080).enableBrowserProxying(false)).start()
    }
}