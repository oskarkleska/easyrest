package src.model

import Returns
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
    val positive = Returns<Unit>(this)
}

class CrudPost : EndpointModel(
    method = POST,
    protocol = Protocol.HTTPS,
    baseUri = URI,
    path = "$PATH/resource",
    headers = mutableMapOf("Content-Type" to "application/json"),
    requirements = Requirements(statusCode = 201, responseTime = 10L)
) {
    private val positive = Returns<RandomResourceResponse>(this)

    fun positive(dashboardId: String, body:Any): Returns<RandomResourceResponse> {
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
    private val positive = Returns<RandomResourceResponse>(this)

    fun positive(id: String, dashboardId: String): Returns<RandomResourceResponse> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
    }
    fun notFound(id: String, dashboardId: String): Returns<Unit> {
        return Returns<Unit>(this)
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
    headers = mutableMapOf("Content-Type" to "application/json"),
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
) {
    private val positive = Returns<Unit>(this)

    fun positive(id: String, dashboardId: String, body: Any) : Returns<Unit> {
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
    private val positive = Returns<Unit>(this)

    fun positive(id: String, dashboardId: String) : Returns<Unit> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
    }
}