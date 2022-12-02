package test.crudtest

import Exceptions
import Utils.softAssertions
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.junit.jupiter.api.*
import src.model.*
import stubs.IntegrationTests.stubDeleteResource
import stubs.IntegrationTests.stubGetDashboardId
import stubs.IntegrationTests.stubGetResource
import stubs.IntegrationTests.stubPost
import stubs.IntegrationTests.stubPutResource
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WireMockIntegrationTest {
    private lateinit var id: String
    private lateinit var dashboardId: String
    private val newResource = RandomResource("Something", Random().nextInt(100), true)
    private val log = LogManager.getLogger(this::class.java)

    init {
        ThreadContext.put("ROUTING_KEY", "example_test")
        WireMockServer(WireMockConfiguration.options().port(8080).enableBrowserProxying(false)).start()
    }

    @BeforeAll
    fun getBrandNewDashboardId() {
        stubGetDashboardId()
        dashboardId =
            WMCrudGetDashboard().positive().response.headers.find { it.name == "Set-Cookie" }?.value?.split(";")
                ?.find { it.startsWith("UniqueEndpointId") }?.split("=")?.get(1)
                ?: throw Exceptions.ArgumentNotFoundException("No dashboard Id found in response")
        assert(dashboardId == "123") { "Wrong dashboard id obtained, got $dashboardId" }
    }

    @AfterAll
    fun checkSoftAssertions() {
        if (softAssertions.size > 0) log.warn("Soft assertions: $softAssertions")
    }

    @Test
    @Order(1)
    fun createResource() {
        stubPost(dashboardId, newResource)
        val resp = WMCrudPost().positive(dashboardId, newResource).ccc()
        this.id = resp._id
    }

    @Test
    @Order(2)
    fun getResource() {
        stubGetResource(dashboardId, id)
        val rsp = WMCrudGet().positive(id, dashboardId).ccc()
        assertAll("Checking if resource is updated correctly",
            { assert(rsp.isTrue == newResource.isTrue) },
            { assert(rsp.name == newResource.name) },
            { assert(rsp.count == newResource.count) })
    }

    @Test
    @Order(3)
    fun putResource() {
        val updatedResource = newResource
        updatedResource.isTrue = false
        stubPutResource(dashboardId, id, updatedResource)
        WMCrudUpdate().positive(id, dashboardId, updatedResource).cc()
    }

    @Test
    @Order(4)
    fun getUpdatedResource() {
        stubGetResource(dashboardId, id)
        val rsp = WMCrudGet().positive(id, dashboardId).ccc()
        assert(!rsp.isTrue)
        assert(rsp.name == newResource.name)
        assert(rsp.count == newResource.count)
    }

    @Test
    @Order(5)
    fun deleteResource() {
        stubDeleteResource(dashboardId, id)
        WMCrudDelete().positive(id, dashboardId).cc()
    }

    @Test
    @Order(6)
    fun get404NoResource() {
        stubGetResource(dashboardId, id)
        WMCrudGet().notFound(id, dashboardId).cc()
    }
}