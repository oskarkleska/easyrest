import helpers.ConfigHelper
import helpers.ConfigHelper.findConfigPath

object TestManager {

    private lateinit var config: EasyRestConfig

    fun getConfig(): EasyRestConfig {
        val path = findConfigPath()
        if (!this::config.isInitialized) {
            config = ConfigHelper.getConfig(path)
        }
        return config
    }
}