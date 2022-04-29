package src

import EndpointModel
import Protocol
import Requirements
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.http.Method.*

const val URI = "crudcrud.com/"
const val PATH = "api/b64d0555d7984077b6a49b76c860d466"

class CrudPost : EndpointModel(
    method = POST,
    protocol = Protocol.HTTPS,
    baseUri = URI,
    path = "$PATH/resource",
    headers = Headers(listOf(Header("Content-Type", "application/json"))),
    requirements = Requirements(statusCode = 201, responseTime = 1000L)
)

class CrudGetAll : EndpointModel(
    GET,
    Protocol.HTTPS,
    URI,
    "$PATH/@resource",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
)

class CrudGet : EndpointModel(
    GET,
    Protocol.HTTPS,
    URI,
    "$PATH/@resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
)

class CrudUpdate : EndpointModel(
    PUT,
    Protocol.HTTPS,
    URI,
    "$PATH/@resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
)

class CrudDelete : EndpointModel(
    DELETE,
    Protocol.HTTPS,
    URI,
    "$PATH/@resource/@id",
    requirements = Requirements(statusCode = 200, responseTime = 1000L)
)