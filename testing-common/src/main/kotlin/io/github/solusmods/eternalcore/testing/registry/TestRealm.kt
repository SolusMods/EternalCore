package io.github.solusmods.eternalcore.testing.registry

import io.github.solusmods.eternalcore.realm.api.Realm
import io.github.solusmods.eternalcore.realm.api.RealmInstance
import io.github.solusmods.eternalcore.realm.api.Type
import io.github.solusmods.eternalcore.stage.api.Stage
import net.minecraft.world.entity.LivingEntity

class TestRealm(
    override val baseHealth: Double = 10.0,
    override val baseQiRange: Pair<Float, Float> = Pair(0.0F, 0.0F),
    override val baseAttackDamage: Double = 5.0,
    override val baseAttackSpeed: Double = 1.0,
    override val knockBackResistance: Double = 1.0,
    override val jumpHeight: Double = 1.0,
    override val movementSpeed: Double = 1.0,
    override val coefficient: Double = 1.0,
) : Realm(Type.III) {
    override fun getNextBreakthroughs(
        instance: RealmInstance,
        living: LivingEntity
    ): MutableList<Realm?> {
        TODO("Not yet implemented")
    }

    override fun getPreviousBreakthroughs(
        instance: RealmInstance,
        living: LivingEntity
    ): MutableList<Realm?> {
        TODO("Not yet implemented")
    }

    override fun getDefaultBreakthrough(
        instance: RealmInstance,
        living: LivingEntity
    ): Realm? {
        TODO("Not yet implemented")
    }

    override fun getRealmStages(
        instance: RealmInstance,
        living: LivingEntity
    ): MutableList<Stage?> {
        TODO("Not yet implemented")
    }

    override fun passivelyFriendlyWith(
        instance: RealmInstance,
        entity: LivingEntity
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun canFly(
        instance: RealmInstance,
        living: LivingEntity
    ): Boolean {
        TODO("Not yet implemented")
    }
}