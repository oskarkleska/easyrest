package helpers

import io.restassured.http.Method
import models.Protocol

object ReportingApi {

    private val testedServices: MutableSet<ServiceModelReport> = mutableSetOf()

    fun getAllTestedServices(): MutableSet<ServiceModelReport> {
        return testedServices
    }

    fun getAllTestedEndpoints(vararg serviceClass: Class<*>): MutableSet<EndpointModelReport> {
        val endpoints = mutableSetOf<EndpointModelReport>()
        testedServices.filter{serviceClass.any{c -> c.simpleName == it.name}}.forEach{endpoints.addAll(it.endpoints)}
        return endpoints
    }

    fun getAllTestedEndpoints(): MutableSet<EndpointModelReport> {
        val endpoints = mutableSetOf<EndpointModelReport>()
        testedServices.forEach{endpoints.addAll(it.endpoints)}
        return endpoints
    }

    fun registerNewService(service: ServiceModelReport) {
        testedServices.add(service)
    }

    fun registerNewEndpoint(serviceName: String, endpointModelReport: EndpointModelReport) {
        testedServices.find { it.name == serviceName }?.endpoints?.add(endpointModelReport)
            ?: throw IllegalStateException("Service $serviceName not yet registered")
    }

    data class ServiceModelReport(
        val name: String,
        val protocol: Protocol,
        val baseUri: String,
        val endpoints: MutableSet<EndpointModelReport>
    ) {
        override fun equals(other: Any?): Boolean {
            return if(other is ServiceModelReport) {
                other.baseUri == this.baseUri &&
                        other.protocol == this.protocol &&
                        other.name == this.name
            } else false
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + protocol.hashCode()
            result = 31 * result + baseUri.hashCode()
            return result
        }
    }

    data class EndpointModelReport(
        val name: String,
        val path: String?,
        val url: String,
        val method: Method,
        val queryParams: List<String>?,
        val headers: List<String>?
    )
}