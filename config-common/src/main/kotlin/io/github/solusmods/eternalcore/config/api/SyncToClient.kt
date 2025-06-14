package io.github.solusmods.eternalcore.config.api

/**
 * Marks a [CoreConfig] class as syncable from server to client.
 * When a player joins a multiplayer server, configs with this annotation will be automatically synced with the server's config.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class SyncToClient 