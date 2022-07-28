package helpers

import EasyRestConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

object ConfigHelper {
    private const val DEFAULT_CONFIG_PATH: String = "/config.yaml"
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
        // todo logic to find config path
        return "confxxig.yaml"
    }

    private fun getBaseConfig(): EasyRestConfig {
        return mapper.readValue(
            File(this::class.java.classLoader.getResource(DEFAULT_CONFIG_PATH).toURI()),
//            File(this::class.java.getResource(DEFAULT_CONFIG_PATH).toExternalForm()),
            EasyRestConfig::class.java
        )
    }
}