package test.crudtest

import Utils.retry
import Utils.softAssert
import Utils.softAssertions
import io.restassured.RestAssured.given
import org.junit.jupiter.api.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ReferenceTest {
    private lateinit var id: String
    private lateinit var dashboardId: String
    private val newResource = RandomResource("Something", Random().nextInt(100), true)

    @BeforeAll
    fun getBrandNewDashboardId() {
        retry {
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
        println("Soft assertions: $softAssertions")
    }

    @Test
    @Order(1)
    fun createResource() {
        val resp = retry {
            val response = given()
                .baseUri("https://crudcrud.com/")
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
        val rsp = retry {
            val response = given()
                .baseUri("https://crudcrud.com/")
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
        retry {
            val response = given()
                .baseUri("https://crudcrud.com/")
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
        val rsp = retry {
            val response = given()
                .baseUri("https://crudcrud.com/")
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
        retry {
            val response = given()
                .baseUri("https://crudcrud.com/")
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
        retry {
            val response = given()
                .baseUri("https://crudcrud.com/")
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