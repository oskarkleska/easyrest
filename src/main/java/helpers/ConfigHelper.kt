package helpers

import EasyRestConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.apache.logging.log4j.LogManager
import java.io.File

object ConfigHelper {
    private const val DEFAULT_CONFIG_PATH: String = "config.yaml"
    private val log = LogManager.getLogger(this::class.java)

    fun getConfig(path: String): EasyRestConfig {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule.Builder().build())

        return try {
            mapper.readValue(File("src/main/resources/$path"), EasyRestConfig::class.java)
        } catch (exception: MissingKotlinParameterException) {
            log.info("Could not read YAML file! \n${exception.message}")
            getBaseConfig()
        }
    }

    fun findConfigPath(): String {
        // todo logic to find config path
        return "config.yaml"
    }

    private fun getBaseConfig(): EasyRestConfig {
        return getConfig(DEFAULT_CONFIG_PATH)
    }
}