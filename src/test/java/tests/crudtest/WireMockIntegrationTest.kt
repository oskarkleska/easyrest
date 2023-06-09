package tests.crudtest

import Exceptions
import Utils.softAssertions
import com.github.tomakehurst.wiremock.client.WireMock.*
import helpers.ReportingApi
import helpers.ReportingApi.prettyPrint
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import src.endpoints.crud.*
import src.model.*
import stubs.crudtest.CrudStubs.stubDeleteResource
import stubs.crudtest.CrudStubs.stubGetDashboardId
import stubs.crudtest.CrudStubs.stubGetResource
import stubs.crudtest.CrudStubs.stubPost
import stubs.crudtest.CrudStubs.stubPutResource
import tests.BaseWiremockTest
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WireMockIntegrationTest : BaseWiremockTest() {
    private lateinit var id: String
    private lateinit var dashboardId: String
    private val newResource = RandomResource("Something", Random().nextInt(100), true)
    private val log = LogManager.getLogger(this::class.java)

    @BeforeAll
    fun getBrandNewDashboardId() {
        stubGetDashboardId()
        dashboardId =
            WiremockStubService().GetDashboard().positive().response.headers.find { it.name == "Set-Cookie" }?.value?.split(";")
                ?.find { it.startsWith("UniqueEndpointId") }?.split("=")?.get(1)
                ?: throw Exceptions.ArgumentNotFoundException("No dashboard Id found in response")
    }

    @AfterAll
    fun checkSoftAssertions() {
        if (softAssertions.size > 0) log.warn("Soft assertions: $softAssertions")
        ReportingApi.getAllTestedServices().prettyPrint()
    }

    @Test
    @Order(1)
    fun createResource() {
        stubPost(dashboardId, newResource)
        val resp = WiremockStubService().CreateNewResource().positive(dashboardId, newResource)
        this.id = resp._id
    }

    @Test
    @Order(2)
    fun getResource() {
        stubGetResource(dashboardId, id)
        val rsp = WiremockStubService().GetResourceById().positive(id, dashboardId)
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
        WiremockStubService().UpdateResource().positive(id, dashboardId, updatedResource)
    }

    @Test
    @Order(4)
    fun getUpdatedResource() {
        stubGetResource(dashboardId, id)
        val rsp = WiremockStubService().GetResourceById().positive(id, dashboardId)
        assert(!rsp.isTrue)
        assert(rsp.name == newResource.name)
        assert(rsp.count == newResource.count)
    }

    @Test
    @Order(5)
    fun deleteResource() {
        stubDeleteResource(dashboardId, id)
        WiremockStubService().DeleteResource().positive(id, dashboardId)
    }

    @Test
    @Order(6)
    fun get404NoResource() {
        stubGetResource(dashboardId, id)
        WiremockStubService().GetResourceById().notFound(id, dashboardId)
    }

    @Test
    @Order(7)
    fun verifyReportingApi() {
        val services = ReportingApi.getAllTestedServices()
        assertTrue(services.toList().find{it.name == WiremockStubService::class.java.simpleName}!!.endpoints.size == 5, "Something went wrong with calculating amount of endpoints")
        val allTestedEndpoints = ReportingApi.getAllTestedEndpoints()
        val filteredEndpoints = ReportingApi.getAllTestedEndpoints(WiremockStubService::class.java)
        assertTrue(allTestedEndpoints == filteredEndpoints, "Tested endpoints in reporting api do not match")
    }
}