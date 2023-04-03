package src.model

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

object WireMockInstance {
    private lateinit var server: WireMockServer

    fun startServer() {
        if(this::server.isInitialized.not()) {
            server = WireMockServer(WireMockConfiguration.options().port(8080).enableBrowserProxying(false))
        }
        if(server.isRunning.not()) server.start()
    }

    fun stopServer(){
        if(server.isRunning) server.stop()
    }

}