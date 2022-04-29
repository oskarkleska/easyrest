package src

import EndpointModel
import Protocol
import Requirements
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.http.Method.*

const val URI = "crudcrud.com/"
const val PATH = "api/@dashboardId"

class CrudGetDashboard : EndpointModel(
    method = GET,
    protocol = Protocol.HTTPS,
    baseUri = URI,
    requirements = Requirements(statusCode = 200, responseTime = 2000L)
)

class CrudPost : EndpointModel(
    method = POST,
    protocol = Protocol.HTTPS,
    baseUri = URI,
    path = "$PATH/resource",
    headers = Headers(listOf(Header("Content-Type", "application/json"))),
    requirements = Requirements(statusCode = 201, responseTime = 10L)
)

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
)

class CrudUpdate : EndpointModel(
    PUT,
    Protocol.HTTPS,
    URI,
    path = "$PATH/resource/@id",
    headers = Headers(listOf(Header("Content-Type", "application/json"))),
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
)

class CrudDelete : EndpointModel(
    DELETE,
    Protocol.HTTPS,
    URI,
    "$PATH/resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
)