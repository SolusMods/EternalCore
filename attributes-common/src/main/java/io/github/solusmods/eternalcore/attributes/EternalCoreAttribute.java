package io.github.solusmods.eternalcore.attributes;

import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributes;
import io.github.solusmods.eternalcore.attributes.impl.network.EternalCoreAttributeNetwork;

public class EternalCoreAttribute {

    public static void init(){
        EternalCoreAttributeNetwork.init();
        EternalCoreAttributes.init();
        EternalCoreAttributeRegister.init();
    }
}
