package io.github.solusmods.eternalcore.network.api.util

import lombok.Synchronized

open class Changeable<T> protected constructor(private val original: T?) {
    private var value: T?

    init {
        this.value = original
    }

    @Synchronized
    fun get(): T? {
        return value
    }

    @Synchronized
    fun set(value: T?) {
        this.value = value
    }

    val isPresent: Boolean
        get() = value != null

    val isEmpty: Boolean
        get() = value == null

    fun hasChanged(): Boolean {
        if (original == null) return value != null
        return original != value
    }

    companion object {
        @JvmStatic
        fun <T> of(value: T?): Changeable<T?> {
            return Changeable<T?>(value)
        }
    }
}
