package io.github.solusmods.eternalcore.fabric;

import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RegisterImpl {
    @NotNull
    public static <T extends AbstractRealm> Holder<T> registerRealm(String modId, String id, Supplier<T> supplier) {
        return null;
    }

    @NotNull
    public static <T extends AbstractStage> Holder<T> registerStage(String modId, String id, Supplier<T> supplier) {
        return null;
    }

    @NotNull
    public static <T extends ElementType> Holder<T> registerElementType(String modId, String id, Supplier<T> supplier) {
        return null;
    }

    @NotNull
    public static <T extends AbstractSpiritualRoot> Holder<T> registerSpiritualRoot(String modId, String id, Supplier<T> supplier) {
        return null;
    }
}
