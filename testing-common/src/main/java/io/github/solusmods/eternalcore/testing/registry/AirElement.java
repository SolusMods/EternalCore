package io.github.solusmods.eternalcore.testing.registry;

import io.github.solusmods.eternalcore.element.api.Element;
import io.github.solusmods.eternalcore.element.api.ElementType;
import io.github.solusmods.eternalcore.realm.api.Realm;
import lombok.AllArgsConstructor;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;


public class AirElement extends Element {
    public AirElement() {
        super(ElementType.AIR);
    }

}
