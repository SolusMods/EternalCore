package io.github.solusmods.eternalcore.keybind.api

import com.mojang.blaze3d.platform.InputConstants
import lombok.Getter
import net.minecraft.client.KeyMapping

class EternalKeybinding(
    langKey: String?,
    defaultKey: InputConstants.Key,
    category: KeybindingCategory,
    action: KeyBindingAction,
    release: KeyBindingRelease? = null
) : KeyMapping(langKey, defaultKey.type, defaultKey.value, category.categoryString) {

    val action: KeyBindingAction?

    val release: Runnable?

    /**
     * Creates a Keybinding which handles the given action automatically.
     *
     * @param langKey    Translation String
     * @param defaultKey Default Key
     * @param category   Category
     * @param action     Action when pressed
     */
    constructor(langKey: String?, defaultKey: Int, category: KeybindingCategory, action: KeyBindingAction) : this(
        langKey,
        InputConstants.Type.KEYSYM.getOrCreate(defaultKey),
        category,
        action,
        null
    )

    /**
     * Creates a Keybinding without a default key.
     *
     * @param langKey  Translation String
     * @param category Category
     * @param action   Action when pressed.
     */
    constructor(langKey: String?, category: KeybindingCategory, action: KeyBindingAction) : this(
        langKey,
        InputConstants.UNKNOWN.value,
        category,
        action,
        null
    )

    /**
     * Creates a Keybinding without a default key.
     *
     * @param langKey  Translation String
     * @param category Category
     * @param action   Action when pressed.
     * @param release  Action when released
     */
    constructor(
        langKey: String?,
        category: KeybindingCategory,
        action: KeyBindingAction,
        release: KeyBindingRelease?
    ) : this(langKey, InputConstants.UNKNOWN.value, category, action, release)

    /**
     * Creates a Keybinding which handles the given action automatically.
     *
     * @param langKey    Translation String
     * @param defaultKey Default Key
     * @param category   Category
     * @param action     Action when pressed
     * @param release    Action when released
     */
    constructor(
        langKey: String?,
        defaultKey: Int,
        category: KeybindingCategory,
        action: KeyBindingAction,
        release: KeyBindingRelease?
    ) : this(langKey, InputConstants.Type.KEYSYM.getOrCreate(defaultKey), category, action, release)

    /**
     * Creates a Keybinding which handles the given action automatically.
     *
     * @param langKey    Translation String
     * @param defaultKey Default Key
     * @param category   Category
     * @param action     Action when pressed
     * @param release    Action when released
     */
    /**
     * Creates a Keybinding which handles the given action automatically.
     *
     * @param langKey    Translation String
     * @param defaultKey Default Key
     * @param category   Category
     * @param action     Action when pressed
     */
    init {
        if (release == null) {
            this.action = action
            this.release = null
        } else {
            this.action = KeyBindingAction {
                if (!PRESSED_KEYBINDINGS.containsKey(this)) {
                    PRESSED_KEYBINDINGS.put(this, System.currentTimeMillis())
                    action.onPress()
                }
            }
            this.release = Runnable {
                if (PRESSED_KEYBINDINGS.containsKey(this)) {
                    val start: Long = PRESSED_KEYBINDINGS.remove(this)!!
                    val end = System.currentTimeMillis()
                    release.onRelease(end - start)
                }
            }
        }
    }

    fun interface KeyBindingAction {
        fun onPress()
    }

    fun interface KeyBindingRelease {
        /**
         * @param duration in milliseconds
         */
        fun onRelease(duration: Long)
    }

    companion object {
        private val PRESSED_KEYBINDINGS = HashMap<EternalKeybinding?, Long?>()
    }
}
