package io.github.solusmods.eternalcore.stage.api.entity;

public enum ProjectileHitResult {
    DEFAULT, // Hit, damage + possibly continue
    HIT, // Hit + damage
    HIT_NO_DAMAGE, // Hit
    PASS // Pass through
}
