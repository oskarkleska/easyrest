package helpers

import EasyRestConfig
import LoggingConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

object ConfigHelper {
    private val log = LogManager.getLogger(this::class.java)
    private val mapper = ObjectMapper(YAMLFactory())

    init {
        mapper.registerModule(KotlinModule.Builder().build())
    }

    fun getConfig(path: String): EasyRestConfig {
        return try {
            mapper.readValue(File("src/main/resources/$path"), EasyRestConfig::class.java)
        } catch (exception: MissingKotlinParameterException) {
            log.warn("Could not read YAML file! \n${exception.message}")
            getBaseConfig()
        } catch (exception: FileNotFoundException) {
            log.warn("Could not find $path file, getting base config")
            getBaseConfig()
        }
    }

    fun findConfigPath(): String {
        return "config.yaml"
    }

    private fun getBaseConfig(): EasyRestConfig {
        return EasyRestConfig(
            LoggingConfig(
                logRequests = true,
                logResponses = true,
                logHeaders = true,
                logCookies = false,
                logTiming = true,
                logPayload = true,
                logUri = true,
                logQueryParams = true,
                logHtmlResponse = false
            ),
            defaultRetryCount = 5,
            defaultPollingDelay = 1000
        )
    }
}