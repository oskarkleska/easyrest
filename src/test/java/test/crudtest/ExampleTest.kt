package test.crudtest

import E
import Requirements
import Utils.softAssertions
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.junit.jupiter.api.*
import src.model.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ExampleTest {
    private lateinit var id: String
    private lateinit var dashboardId: String
    private val newResource = RandomResource("Something", Random().nextInt(100), true)
    private val log = LogManager.getLogger(this::class.java)

    init {
        ThreadContext.put("ROUTING_KEY", "example_test")
    }

    @BeforeAll
    fun getBrandNewDashboardId() {
        dashboardId = E<Unit>(CrudGetDashboard())
            .cc()
            .response
            .headers
            .find { it.name == "Set-Cookie" }
            ?.value
            ?.split(";")
            ?.find { it.startsWith("UniqueEndpointId") }
            ?.split("=")
            ?.get(1) ?: throw Exceptions.ArgumentNotFoundException("No dashboard Id found in response")
    }

    @AfterAll
    fun checkSoftAssertions() {
        if (softAssertions.size > 0)
            log.info("Soft assertions: $softAssertions")
    }

    @Test
    @Order(1)
    fun createResource() {
        val resp = E<RandomResourceResponse>(CrudPost())
            .setParamsForPath(mapOf("dashboardId" to dashboardId))
            .setBody(newResource)
            .ccc()
        this.id = resp._id
    }

    @Test
    @Order(2)
    fun getResource() {
        val rsp =
            E<RandomResourceResponse>(CrudGet())
                .setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
                .ccc()
        assert(rsp.isTrue == newResource.isTrue)
        assert(rsp.name == newResource.name)
        assert(rsp.count == newResource.count)
    }

    @Test
    @Order(3)
    fun putResource() {
        val updatedResource = newResource
        updatedResource.isTrue = false
        E<Unit>(CrudUpdate()).setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId)).setBody(updatedResource)
            .cc()
    }

    @Test
    @Order(4)
    fun getUpdatedResource() {
        val rsp =
            E<RandomResourceResponse>(CrudGet())
                .setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
                .ccc()
        assert(!rsp.isTrue)
        assert(rsp.name == newResource.name)
        assert(rsp.count == newResource.count)
    }

    @Test
    @Order(5)
    fun deleteResource() {
        E<Unit>(CrudDelete()).setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId)).cc()
    }

    @Test
    @Order(6)
    fun get404NoResource() {
        E<RandomResourceResponse>(CrudGet()).setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
            .overrideRequirements(
                Requirements(statusCode = 404, responseTime = 1000L)
            )
            .cc()
    }
}