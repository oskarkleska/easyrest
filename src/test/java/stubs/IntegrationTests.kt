package stubs

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.google.gson.Gson
import org.apache.http.HttpHeaders.CONTENT_TYPE
import test.crudtest.RandomResource
import test.crudtest.RandomResourceResponse
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

object IntegrationTests {
    private val resourceList: ArrayList<RandomResourceResponse> = arrayListOf()

    fun stubGetDashboardId(): StubMapping = stubFor(
        get(urlEqualTo("/")).willReturn(
            aResponse().withHeader("Set-Cookie", "UniqueEndpointId=123")
        )
    )

    fun stubPost(dashboardId: String, resource: RandomResource): StubMapping {
        val id = resourceList.add(resource)
        val objToReturn = resourceList.find { it._id == id } ?: throw NoSuchElementException("Problem")
        return stubFor(
            post(urlMatching("^/api/$dashboardId/resource$")).willReturn(
                aResponse()
                    .withBody(toJson(objToReturn))
                    .withStatus(201)
                    .withHeader(CONTENT_TYPE, "application/json")
            )
        )
    }

    fun stubGetResource(dashboardId: String, id: String): StubMapping {
        val objToReturn = resourceList.find { it._id == id }

        return stubFor(
            get(urlMatching("^/api/$dashboardId/resource/$id$")).willReturn(
                if (objToReturn == null) {
                    aResponse()
                        .withStatus(404)
                } else {
                    aResponse()
                        .withBody(toJson(objToReturn))
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                }
            )
        )
    }

    fun stubPutResource(dashboardId: String, id: String, updatedResource: RandomResource): StubMapping {
        resourceList.update(updatedResource, id)
        return stubFor(
            put(urlMatching("^/api/$dashboardId/resource/$id$")).willReturn(
                aResponse().withStatus(200)
            )
        )
    }

    fun stubDeleteResource(dashboardId: String, id: String): StubMapping {
        resourceList.removeIf { it._id == id } ?: throw NoSuchElementException("Element not found")
        return stubFor(
            delete(urlMatching("^/api/$dashboardId/resource/$id$")).willReturn(
                aResponse().withStatus(200)
            )
        )
    }

    private fun ArrayList<RandomResourceResponse>.add(resource: RandomResource): String {
        val id = UUID.randomUUID().toString()
        resourceList.add(
            RandomResourceResponse(
                name = resource.name,
                count = resource.count,
                isTrue = resource.isTrue,
                _id = id
            )
        )
        return id
    }

    private fun ArrayList<RandomResourceResponse>.update(resource: RandomResource, id: String) {
        val element = resourceList.find { it._id == id } ?: return
        element.count = resource.count
        element.isTrue = resource.isTrue
        element.name = resource.name
        resourceList.removeIf { it._id == id }
        resourceList.add(element)
    }
    private fun toJson(obj: Any) = Gson().toJson(obj)

}