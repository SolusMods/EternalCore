package io.github.solusmods.eternalcore;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.config.RealmEntry;
import io.github.solusmods.eternalcore.config.SpiritualRootEntry;
import io.github.solusmods.eternalcore.config.StageEntry;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerConfigs {

    @ExpectPlatform
    public static RealmEntry getRealmConfig(AbstractRealm realm) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static StageEntry getStageConfig(AbstractStage stage) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static SpiritualRootEntry getSpiritualRootConfig(AbstractSpiritualRoot root) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }
}
