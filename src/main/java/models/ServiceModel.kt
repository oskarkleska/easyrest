package models

import helpers.ReportingApi
import io.restassured.http.Cookies
import io.restassured.http.Method

open class ServiceModel(
    val protocol: Protocol, val baseUri: String
) {

    init {
        ReportingApi.registerNewService(
            ReportingApi.ServiceModelReport(
                name = this::class.java.simpleName,
                protocol = this.protocol,
                baseUri = this.baseUri,
                endpoints = mutableSetOf()
            )
        )
    }

    open inner class EndpointModel(
        val method: Method,
        var path: String? = null,
        var cookies: Cookies? = null,
        var headers: MutableMap<String, Any>? = null,
        var body: Any? = null,
        var queryParams: MutableMap<String, Any>? = null,
        var formParams: MutableMap<String, Any>? = null,
        var requirements: Requirements? = null,
    ) {
        private val pathPattern = path
        private var tempPathPattern: String? = pathPattern
        val queryParamsPattern = queryParams
        val serviceName: String = this@ServiceModel::class.java.simpleName
        private var tempRequirements: Requirements? = requirements
        private var tempPath: String? = path


        fun setTempRequirements(requirements: Requirements?) {
            this.tempRequirements = requirements
        }

        fun setTempPath(path: String?) {
            this.tempPath = path
            this.tempPathPattern = path
        }

        fun getBaseUri(): String {
            return baseUri
        }

        fun getProtocol(): Protocol {
            return protocol
        }

        fun getCurrentRequirements(): Requirements? {
            return if (tempRequirements != null) tempRequirements else requirements
        }

        fun resetModel() {
            this.tempRequirements = requirements
            this.tempPath = path
        }

        fun getCurrentPath(): String? {
            return if (tempPath != null) tempPath else path
        }

        fun getCurrentPathPattern(): String? {
            return if (tempPathPattern != null) tempPathPattern else pathPattern
        }

        fun getPathPattern(): String? {
            return pathPattern
        }
    }
}


