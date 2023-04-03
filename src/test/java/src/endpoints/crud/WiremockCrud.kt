package src.endpoints.crud

import EasyResponse
import Returns
import EndpointModel
import Protocol
import Requirements
import io.restassured.http.Method.*
import tests.crudtest.RandomResourceResponse

private const val URI = "localhost:8080"
private const val PATH = "api/@dashboardId"

class WMCrudGetDashboard : EndpointModel(
    method = GET,
    protocol = Protocol.HTTP,
    baseUri = URI,
    requirements = Requirements(statusCode = 200, responseTime = 2000L)
) {
    fun positive() = Returns<Unit>(this).cc()
}

class WMCrudPost : EndpointModel(
    method = POST,
    protocol = Protocol.HTTP,
    baseUri = URI,
    path = "$PATH/resource",
    headers = mutableMapOf("Content-Type" to "application/json"),
    requirements = Requirements(statusCode = 201, responseTime = 300L)
) {
    private val positive = Returns<RandomResourceResponse>(this)

    fun positive(dashboardId: String, body:Any): Returns<RandomResourceResponse> {
        return positive.setParamsForPath(mapOf("dashboardId" to dashboardId))
            .setBody(body)
    }
}

class WMCrudGetAll : EndpointModel(
    GET,
    Protocol.HTTP,
    URI,
    path = "$PATH/@resource",
    requirements = Requirements(statusCode = 200, responseTime = 300L)
)

class WMCrudGet : EndpointModel(
    GET,
    Protocol.HTTP,
    URI,
    path = "$PATH/resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 300L)
) {
    private val positive = Returns<RandomResourceResponse>(this)

    fun positive(id: String, dashboardId: String): Returns<RandomResourceResponse> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
    }
    fun notFound(id: String, dashboardId: String): EasyResponse {
        return Returns<Unit>(this)
            .setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
            .overrideRequirements(
                Requirements(statusCode = 404, responseTime = 1000L)
            )
            .cc()
    }
}

class WMCrudUpdate : EndpointModel(
    PUT,
    Protocol.HTTP,
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

class WMCrudDelete : EndpointModel(
    DELETE,
    Protocol.HTTP,
    URI,
    "$PATH/resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
) {
    private val positive = Returns<Unit>(this)

    fun positive(id: String, dashboardId: String) : Returns<Unit> {
        return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId))
    }
}