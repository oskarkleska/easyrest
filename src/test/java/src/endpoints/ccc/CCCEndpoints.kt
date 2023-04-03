package src.endpoints.ccc

import Returns
import EndpointModel
import Protocol
import Requirements
import io.restassured.http.Method.*
import tests.callcheckandcast.SimpleResponseForCasting
import tests.callcheckandcast.WrongResponseForCasting

private const val URI = "localhost:8080/"
private const val PATH = "ccc"

class GetSimpleResponse : EndpointModel(
    method = GET,
    protocol = Protocol.HTTP,
    baseUri = URI,
    path = PATH,
    headers = mutableMapOf("Accept" to "application/json"),
    requirements = Requirements(statusCode = 200, responseTime = 2000L)
) {
    fun go() = Returns<SimpleResponseForCasting>(this).ccc()
    fun failCasting() = Returns<WrongResponseForCasting>(this).ccc(retryLimit = 1)

}

class Retries : EndpointModel(
    method = GET,
    protocol = Protocol.HTTP,
    baseUri = URI,
    path = "$PATH/retries",
    requirements = Requirements(statusCode = 200)
) {
    fun go() = Returns<Unit>(this).cc(interval = 0L, retryLimit = 3)
    fun failCode404() = Returns<Unit>(this).cc(interval = 0L, retryLimit = 1)
    fun failTime() =
        Returns<Unit>(this)
            .overrideRequirements(Requirements(404, responseTime = 1L))
            .cc(interval = 0L, retryLimit = 1)
    fun overridenReqsHPs() =
        Returns<Unit>(this)
            .setPath("$PATH/retries/200to201")
            .overrideRequirements(Requirements(201))
            .cc(interval = 0L, retryLimit = 3)
}