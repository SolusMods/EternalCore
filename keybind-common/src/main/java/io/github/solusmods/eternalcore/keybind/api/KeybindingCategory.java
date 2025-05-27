package io.github.solusmods.eternalcore.keybind.api;

import io.github.solusmods.eternalcore.keybind.ModuleConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class KeybindingCategory {
    private final String name;

    public String getCategoryString() {
        return String.format("%s.category.%s", ModuleConstants.MOD_ID, this.name);
    }
}
