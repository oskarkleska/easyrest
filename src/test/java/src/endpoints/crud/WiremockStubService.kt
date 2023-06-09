package src.endpoints.crud

import EasyResponse
import Returns
import models.Protocol
import models.Requirements
import io.restassured.http.Method.*
import models.ServiceModel
import tests.crudtest.RandomResourceResponse

private const val URI = "localhost:8080"
private const val PATH = "api/@dashboardId"

class WiremockStubService: ServiceModel(protocol = Protocol.HTTP, baseUri = URI) {


    inner class GetDashboard : EndpointModel(
        method = GET,
        requirements = Requirements(statusCode = 200, responseTime = 2000L)
    ) {
        fun positive() = Returns<Unit>(this).cc()
    }

    inner class CreateNewResource : EndpointModel(
        method = POST,
        path = "$PATH/resource",
        headers = mutableMapOf("Content-Type" to "application/json"),
        requirements = Requirements(statusCode = 201, responseTime = 300L)
    ) {
        private val positive = Returns<RandomResourceResponse>(this)

        fun positive(dashboardId: String, body: Any): RandomResourceResponse {
            return positive.setParamsForPath(mapOf("dashboardId" to dashboardId))
                .setBody(body).ccc()
        }
    }

    inner class GetResourceById : EndpointModel(
        GET,
        path = "$PATH/resource/@id",
        requirements = Requirements(statusCode = 200, responseTime = 300L)
    ) {
        private val positive = Returns<RandomResourceResponse>(this)

        fun positive(id: String, dashboardId: String): RandomResourceResponse {
            return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId)).ccc()
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

    inner class UpdateResource : EndpointModel(
        PUT,
        path = "$PATH/resource/@id",
        headers = mutableMapOf("Content-Type" to "application/json"),
        requirements = Requirements(statusCode = 200, responseTime = 1000L)
    ) {
        private val positive = Returns<Unit>(this)

        fun positive(id: String, dashboardId: String, body: Any): EasyResponse {
            return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId)).setBody(body).cc()
        }
    }

    inner class DeleteResource : EndpointModel(
        DELETE,
        "$PATH/resource/@id",
        requirements = Requirements(statusCode = 200, responseTime = 1000L)
    ) {
        private val positive = Returns<Unit>(this)

        fun positive(id: String, dashboardId: String): EasyResponse {
            return positive.setParamsForPath(mapOf("id" to id, "dashboardId" to dashboardId)).cc()
        }
    }
}