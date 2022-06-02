import helpers.ConfigHelper

object TestManager {

    private lateinit var config: EasyRestConfig

    fun getConfig(): EasyRestConfig {
        if (!this::config.isInitialized) {
            config = ConfigHelper.getConfig(ConfigHelper.findConfigPath())
        }
        return config
    }
}