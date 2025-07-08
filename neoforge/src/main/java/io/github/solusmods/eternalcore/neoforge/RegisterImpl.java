package io.github.solusmods.eternalcore.neoforge;

import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import static io.github.solusmods.eternalcore.neoforge.EternalCoreNeoForge.*;

public class RegisterImpl {
    private static final List<Holder<? extends AbstractSpiritualRoot>> SPIRITUAL_ROOT_REGISTRY = new CopyOnWriteArrayList<>();
    private static final List<Holder<? extends AbstractRealm>> REALM_REGISTRY = new CopyOnWriteArrayList<>();
    private static final List<Holder<? extends AbstractStage>> STAGE_REGISTRY = new CopyOnWriteArrayList<>();
    private static final List<Holder<? extends ElementType>> ELEMENT_REGISTRY = new CopyOnWriteArrayList<>();


    public static <T extends AbstractSpiritualRoot> Holder<T> registerToSpiritualRoot(Holder<T> holder){
        SPIRITUAL_ROOT_REGISTRY.add(holder);
        return holder;
    }

    public static <T extends AbstractRealm> Holder<T> registerToRealm(Holder<T> holder){
        REALM_REGISTRY.add(holder);
        return holder;
    }

    public static <T extends AbstractStage> Holder<T> registerToStage(Holder<T> holder){
        STAGE_REGISTRY.add(holder);
        return holder;
    }

    public static <T extends ElementType> Holder<T> registerToElement(Holder<T> holder){
        ELEMENT_REGISTRY.add(holder);
        return holder;
    }

    @NotNull
    public static <T extends AbstractRealm> Holder<T> registerRealm(String modId, String id, Supplier<T> supplier) {
        return (Holder<T>) registerToRealm(REALMS.register(id, supplier));
    }

    @NotNull
    public static <T extends AbstractStage> Holder<T> registerStage(String modId, String id, Supplier<T> supplier) {
        return (Holder<T>) registerToStage(STAGES.register(id, supplier));
    }

    @NotNull
    public static <T extends ElementType> Holder<T> registerElementType(String modId, String id, Supplier<T> supplier) {
        return (Holder<T>) registerToElement(ELEMENT_TYPES.register(id, supplier));
    }

    @NotNull
    public static <T extends AbstractSpiritualRoot> Holder<T> registerSpiritualRoot(String modId, String id, Supplier<T> supplier) {
        return (Holder<T>) registerToSpiritualRoot(SPIRITUAL_ROOTS.register(id, supplier));
    }

    public static void init(){}
}
