package src.model

import E
import EndpointModel
import Protocol
import Requirements
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.http.Method.*
import test.crudtest.RandomResourceResponse

const val URI = "crudcrud.com/"
const val PATH = "api/@dashboardId"

class CrudGetDashboard : EndpointModel(
    method = GET,
    protocol = Protocol.HTTPS,
    baseUri = URI,
    requirements = Requirements(statusCode = 200, responseTime = 2000L)
) {
    val positive = E<Unit>(this)
}

class CrudPost : EndpointModel(
    method = POST,
    protocol = Protocol.HTTPS,
    baseUri = URI,
    path = "$PATH/resource",
    headers = Headers(listOf(Header("Content-Type", "application/json"))),
    requirements = Requirements(statusCode = 201, responseTime = 10L)
) {
    private val positive = E<RandomResourceResponse>(this)

    fun positive(dashboardId: String, body:Any): E<RandomResourceResponse> {
        return positive.setParamsForPath(mapOf("dashboardId" to dashboardId))
            .setBody(body)
    }
}

class CrudGetAll : EndpointModel(
    GET,
    Protocol.HTTPS,
    URI,
    path = "$PATH/@resource",
    requirements = Requirements(statusCode = 200, responseTime = 10L)
)

class CrudGet : EndpointModel(
    GET,
    Protocol.HTTPS,
    URI,
    path = "$PATH/resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 10L)
) {
    private val positive = E<RandomResourceResponse>(this)

    fun positive(id: String, dashboardId: String): E<RandomResourceResponse> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
    }
    fun notFound(id: String, dashboardId: String): E<Unit> {
        return E<Unit>(this)
            .setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
            .overrideRequirements(
                Requirements(statusCode = 404, responseTime = 1000L)
            )
    }
}

class CrudUpdate : EndpointModel(
    PUT,
    Protocol.HTTPS,
    URI,
    path = "$PATH/resource/@id",
    headers = Headers(listOf(Header("Content-Type", "application/json"))),
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
) {
    private val positive = E<Unit>(this)

    fun positive(id: String, dashboardId: String, body: Any) : E<Unit> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId)).setBody(body)
    }
}

class CrudDelete : EndpointModel(
    DELETE,
    Protocol.HTTPS,
    URI,
    "$PATH/resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
) {
    private val positive = E<Unit>(this)

    fun positive(id: String, dashboardId: String) : E<Unit> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
    }
}