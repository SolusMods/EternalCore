package io.github.solusmods.eternalcore;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@UtilityClass
public class Register {

    /**
     * Реєструє духовний корінь гравця з заданими параметрами.
     *
     * <p>Духовні корені є специфічною механікою EternalCraft, що визначає
     * базові здібності та характеристики персонажа. Кожен духовний корінь
     * надає унікальні бонуси та можливості для розвитку.</p>
     *
     * @param modId    Унікальний ідентифікатор модифікації, з якою пов'язаний цей духовний корінь.
     *                 Повинен бути валідним mod ID.
     * @param id       Унікальний ідентифікатор духовного кореня в межах модифікації.
     *                 Рекомендується використовувати описові назви (наприклад, "fire_root").
     * @param supplier Постачальник, який створює екземпляр {@link AbstractSpiritualRoot}.
     *                 Не може бути {@code null}.
     * @return {@link Holder}, що містить зареєстрований духовний корінь.
     * Ніколи не повертає {@code null}.
     * @throws AssertionError           якщо метод викликається без відповідної платформенної реалізації
     * @throws IllegalArgumentException якщо будь-який з параметрів є недійсним
     * @see AbstractSpiritualRoot
     * @see Holder
     * @since 1.0
     */
    @ExpectPlatform
    public static @NotNull <T extends AbstractSpiritualRoot> Holder<T> registerSpiritualRoot(String modId, String id, Supplier<T> supplier) {
        throw new AssertionError();
    }

    /**
     * Реєструє сферу культивації з заданими параметрами.
     *
     * <p>Сфери представляють рівні духовного розвитку в системі культивації.
     * Кожна сфера має свої унікальні здібності, вимоги для досягнення та
     * впливає на загальну силу персонажа.</p>
     *
     * @param modId    Унікальний ідентифікатор модифікації, з якою пов'язана ця сфера.
     *                 Повинен бути валідним mod ID.
     * @param id       Унікальний ідентифікатор сфери в межах модифікації.
     *                 Рекомендується використовувати послідовні назви (наприклад, "foundation_realm").
     * @param supplier Постачальник, який створює екземпляр {@link AbstractRealm}.
     *                 Не може бути {@code null}.
     * @return {@link Holder}, що містить зареєстровану сферу.
     * Ніколи не повертає {@code null}.
     * @throws AssertionError           якщо метод викликається без відповідної платформенної реалізації
     * @throws IllegalArgumentException якщо будь-який з параметрів є недійсним
     * @see AbstractRealm
     * @see Holder
     * @since 1.0
     */
    @ExpectPlatform
    public static @NotNull <T extends AbstractRealm> Holder<T> registerRealm(String modId, String id, Supplier<T> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static @NotNull <T extends AbstractStage> Holder<T> registerStage(String modId, String id, Supplier<T> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static @NotNull <T extends ElementType> Holder<T> registerElementType(String modId, String id, Supplier<T> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void init() {}
}
