package io.github.solusmods.eternalcore.api.qi_energy;

import io.github.solusmods.eternalcore.impl.qi_energy.QiEnergyStorage;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public interface QiEnergies {

    /**
     * Повертає мапу всіх елементів за їх ідентифікаторами.
     */
    Map<ResourceLocation, ElementalQiEnergy> getElementalQiEnergies();

    /**
     * Повертає колекцію здобутих елементів.
     */
    Collection<ElementalQiEnergy> getObtainedQiEnergies();

    /**
     * Оновлює екземпляр елемента.
     *
     * @param amount
     */
    void addQi(ResourceLocation qiEnergyId, double amount);


    /**
     * Застосовує дію до кожного елемента.
     *
     * @param action Функція з параметрами (ElementsStorage, ElementalInstance)
     */
    void forEachQiEnergy(BiConsumer<QiEnergyStorage, ElementalQiEnergy> action);

    /**
     * Забути елемент за його ідентифікатором.
     *
     * @param qiEnergyId Ідентифікатор елемента
     * @param amount
     */
    void consumeQi(ResourceLocation qiEnergyId, double amount);

    double getQi(ResourceLocation qiEnergyId);

    /**
     * Позначає структуру як змінену.
     */
    void markDirty();
}
