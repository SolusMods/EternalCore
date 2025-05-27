package io.github.solusmods.eternalcore.testing.client;

import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.keybind.api.EternalKeybinding;
import io.github.solusmods.eternalcore.keybind.api.KeybindingCategory;
import io.github.solusmods.eternalcore.keybind.api.KeybindingManager;
import io.github.solusmods.eternalcore.realm.api.RealmAPI;
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootAPI;
import io.github.solusmods.eternalcore.stage.api.StageAPI;
import io.github.solusmods.eternalcore.testing.EternalCoreTesting;
import io.github.solusmods.eternalcore.testing.ModuleConstants;
import net.minecraft.resources.ResourceLocation;

public class KeybindingTest {
    public static void init() {
        KeybindingCategory category = KeybindingCategory.of("test.category");
        KeybindingManager.register(
                new EternalKeybinding("eternalcore.keybinding.test_press", category, () -> {
                    EternalCoreTesting.LOG.info("Pressed");
                    RealmAPI.realmBreakthroughPacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "test_realm_breakthrough"));
                }),
                new EternalKeybinding("eternalcore.keybinding.test_press", category, () -> {
                    EternalCoreTesting.LOG.info("Pressed");
                    StageAPI.stageBreakthroughPacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "test_stage_breakthrough"));
                }),
                new EternalKeybinding("eternalcore.keybinding.test_press", category, () -> {
                    EternalCoreTesting.LOG.info("Pressed");
                    SpiritualRootAPI.spiritualRootAdvancePacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "test_spiritual_root_advance"));
                }),
                new EternalKeybinding("eternalcore.keybinding.ability", category,
                        () -> AbilityAPI.abilityActivationPacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID,
                                "test_ability"), 0, 0),
                        duration -> AbilityAPI.abilityReleasePacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID,
                                "test_ability"), 0, 0, (int) (duration / 50))),
                new EternalKeybinding("eternalcore.keybinding.ability_2", category,
                        () -> AbilityAPI.abilityActivationPacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID,
                                "test_ability"), 1, 1),
                        duration -> AbilityAPI.abilityReleasePacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID,
                                "test_ability"), 1, 1, (int) (duration / 50))),
                new EternalKeybinding("eternalcore.keybinding.ability_toggle", category,
                        () -> AbilityAPI.abilityTogglePacket(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "test_ability")))
        );
    }
}
