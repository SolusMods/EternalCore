package io.github.solusmods.eternalcore.config

import com.electronwill.nightconfig.toml.TomlWriter
import io.github.solusmods.eternalcore.config.api.CoreConfig
import io.github.solusmods.eternalcore.config.api.SyncToClient

class ConfigRegistry {

    companion object {
        val CONFIGS: MutableMap<Class<out CoreConfig?>?, CoreConfig> =
            mutableMapOf()
        @JvmStatic
        /**
         * Retrieves a registered config instance by class type.
         *
         *
         *
         * @param configClass The config class.
         * @return The instance of the requested config, or null if not registered.
         */
        fun <T : CoreConfig?> getConfig(configClass: Class<T?>): T? {
            return configClass.cast(CONFIGS[configClass])
        }
        @JvmStatic
        val configSyncData: MutableMap<String?, String?>
            /**
             * Serializes all syncable configs into a map for server-to-client transmission.
             *
             *
             *
             * @return A map containing serialized TOML configs.
             */
            get() {
                val configData: MutableMap<String?, String?> =
                    HashMap()
                CONFIGS.forEach { (clazz: Class<out CoreConfig?>?, config: CoreConfig?) ->
                    if (!clazz!!.isAnnotationPresent(SyncToClient::class.java)) return@forEach
                    config!!.load()
                    configData.put(
                        clazz.getSimpleName(),
                        (TomlWriter()).writeToString(config.config)
                    )
                }
                return configData
            }
        /**
         * Serializes a specific config for syncing.
         *
         *
         *
         * @param configClass The class of the config to sync.
         * @return A map containing the serialized TOML data for the requested config.
         */
        @JvmStatic
        fun getConfigSyncData(configClass: Class<out CoreConfig?>): MutableMap<String?, String?> {
            val configData: MutableMap<String?, String?> = HashMap()
            if (!CONFIGS.containsKey(configClass)) return configData
            if (!configClass.isAnnotationPresent(SyncToClient::class.java)) return configData

            val config: CoreConfig = CONFIGS.get(configClass)!!
            config.load()
            configData.put(configClass.getSimpleName(), (TomlWriter()).writeToString(config.config))
            return configData
        }

        /**
         * Reloads all registered configurations from disk.
         */
        @JvmStatic
        fun loadConfigSyncData() {
            for (config in CONFIGS.values) {
                config.load()
            }
        }

        /**
         * Registers a new configuration and loads it.
         *
         *
         *
         * @param configInstance The config instance to register.
         */
        @JvmStatic
        fun registerConfig(configInstance: CoreConfig) {
            CONFIGS.put(configInstance.javaClass, configInstance)
            configInstance.load()
        }



        /**
         * Saves all registered configurations to disk.
         */
        @JvmStatic
        fun saveAllConfigs() {
            for (config in CONFIGS.values) {
                config.save()
            }
        }

        /**
         * Loads config data from a synced client-server map.
         * Only applies to configs annotated with [SyncToClient].
         *
         *
         *
         * @param map The config data received from the server.
         */
        @JvmStatic
        fun loadConfigSyncData(map: MutableMap<String?, String?>?) {
            if (map == null || map.isEmpty()) return
            CONFIGS.forEach { (clazz: Class<out CoreConfig?>?, config: CoreConfig?) ->
                if (!map.containsKey(clazz!!.getSimpleName())) return@forEach
                if (!clazz.isAnnotationPresent(SyncToClient::class.java)) return@forEach
                config!!.loadFromString(map[clazz.getSimpleName()])
            }
        }
    }








}
