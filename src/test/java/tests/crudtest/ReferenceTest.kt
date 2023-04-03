package tests.crudtest

import Utils.retry
import Utils.softAssert
import Utils.softAssertions
import io.restassured.RestAssured.given
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.*
import stubs.crudtest.CrudStubs
import tests.BaseWiremockTest
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ReferenceTest : BaseWiremockTest() {
    private lateinit var id: String
    private lateinit var dashboardId: String
    private val newResource = RandomResource("Something", Random().nextInt(100), true)
    private val log = LogManager.getLogger(this::class.java)

    @BeforeAll
    fun getBrandNewDashboardId() {
        CrudStubs.stubGetDashboardId()
        retry(3, 1000) {
            dashboardId = given()
                .baseUri("https://crudcrud.com/")
                .get()
                .then()
                .statusCode(200)
                .and()
                .extract().response().headers.find { it.name == "Set-Cookie" }?.value?.split(";")
                ?.find { it.startsWith("UniqueEndpointId") }?.split("=")
                ?.get(1) ?: throw Exceptions.ArgumentNotFoundException("No dashboard Id found in response")
        }
    }

    @AfterAll
    fun checkSoftAssertions() {
        if(softAssertions.size > 0)
        log.info("Soft assertions: $softAssertions")
    }

    @Test
    @Order(1)
    fun createResource() {
        CrudStubs.stubPost(dashboardId, newResource)
        val resp = retry(3, 1000)  {
            val response = given()
                .baseUri("http://localhost:8080/")
                .contentType("application/json")
                .body(newResource)
                .post("api/$dashboardId/resource")
                .then()
                .statusCode(201)
                .and()
                .extract()
                .response()

            softAssert("Too long response time") {response.time <= 1000L}
            response.`as`(RandomResourceResponse::class.java)
        }
        this.id = resp._id
    }

    @Test
    @Order(2)
    fun getResource() {
        CrudStubs.stubGetResource(dashboardId, id)
        val rsp = retry(3, 1000)  {
            val response = given()
                .baseUri("http://localhost:8080/")
                .get("api/$dashboardId/resource/$id")
                .then()
                .statusCode(200)
                .and()
                .extract()
                .response()

            softAssert("Too long response time") {response.time <= 1000L}
            response.`as`(RandomResourceResponse::class.java)
        }

        assert(rsp.isTrue == newResource.isTrue)
        assert(rsp.name == newResource.name)
        assert(rsp.count == newResource.count)
    }

    @Test
    @Order(3)
    fun putResource() {
        val updatedResource = newResource
        updatedResource.isTrue = false
        CrudStubs.stubPutResource(dashboardId, id, updatedResource)
        retry(3, 1000)  {
            val response = given()
                .baseUri("http://localhost:8080/")
                .contentType("application/json")
                .body(updatedResource)
                .put("api/$dashboardId/resource/$id")
                .then()
                .statusCode(200)
                .and()
                .extract()
                .response()

            softAssert("Too long response time") {response.time <= 1000L}
        }
    }

    @Test
    @Order(4)
    fun getUpdatedResource() {
        CrudStubs.stubGetResource(dashboardId, id)
        val rsp = retry(3, 1000)  {
            val response = given()
                .baseUri("http://localhost:8080/")
                .get("api/$dashboardId/resource/$id")
                .then()
                .statusCode(200)
                .and()
                .extract()
                .response()

            softAssert("Too long response time") {response.time <= 1000L}
            response.`as`(RandomResourceResponse::class.java)
        }

        assert(!rsp.isTrue)
        assert(rsp.name == newResource.name)
        assert(rsp.count == newResource.count)
    }

    @Test
    @Order(5)
    fun deleteResource() {
        CrudStubs.stubDeleteResource(dashboardId, id)
        retry(3, 1000)  {
            val response = given()
                .baseUri("http://localhost:8080/")
                .delete("api/$dashboardId/resource/$id")
                .then()
                .statusCode(200)
                .and()
                .extract()
                .response()

            softAssert("Too long response time") {response.time <= 1000L}
        }
    }

    @Test
    @Order(6)
    fun get404NoResource() {
        CrudStubs.stubGetResource(dashboardId, id)
        retry(3, 1000)  {
            val response = given()
                .baseUri("http://localhost:8080/")
                .get("api/$dashboardId/resource/$id")
                .then()
                .statusCode(404)
                .and()
                .extract()
                .response()

            softAssert("Too long response time") {response.time <= 1000L}
        }
    }
}