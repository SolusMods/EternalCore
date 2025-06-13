package io.github.solusmods.eternalcore.spiritual_root.api

import dev.architectury.registry.registries.RegistrySupplier
import io.github.solusmods.eternalcore.element.api.Element
import lombok.Getter
import lombok.Setter
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.LivingEntity
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * Екземпляр Духовного Кореня, що представляє конкретну реалізацію [SpiritualRoot] для сутності.
 *
 *
 * Цей клас є контейнером для збереження стану розвитку конкретного Духовного Кореня у сутності.
 * Він містить всю інформацію про поточний прогрес культивації, включаючи:
 *
 *  * Рівень розвитку кореня (від I до X)
 *  * Накопичений досвід культивації
 *  * Силу (чистоту) кореня (0.0 - 1.0)
 *  * Статус активності для доступу до технік
 *  * Додаткові дані для розширення функціональності
 *
 *
 *
 *
 * Кожен екземпляр прив'язаний до конкретного типу [SpiritualRoot] через реєстр,
 * що забезпечує цілісність даних при серіалізації/десеріалізації.
 *
 *
 *
 * Система відстеження змін (dirty tracking) забезпечує ефективну синхронізацію
 * між сервером та клієнтом тільки при необхідності.
 *
 *
 * @author EternalCore Team
 * @version 1.0.4.5
 * @since 1.0
 * @see SpiritualRoot
 *
 * @see RootLevels
 */
open class SpiritualRootInstance(spiritualRoot: SpiritualRoot?) : Cloneable {
    //region Core Fields
    /**
     * Registry supplier for obtaining the spiritual root type.
     *
     *
     * Using RegistrySupplier ensures lazy loading and reference stability
     * during mod reloading.
     *
     */
    protected val spiritualRootRegistrySupplier: RegistrySupplier<SpiritualRoot?> = SpiritualRootAPI.spiritualRootRegistry!!
        .delegate(SpiritualRootAPI.spiritualRootRegistry!!.getId(spiritualRoot))

    /**
     * Strength (purity) of the spiritual root from 0.0 to 1.0.
     *
     *
     * Higher strength means:
     *
     *  * More efficient cultivation of corresponding element
     *  * More powerful techniques and abilities
     *  * Better affinity with elemental energy
     *  * Faster development progress
     *
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Setting strength affects cultivation efficiency
     * instance.setStrength(0.8f); // 80% strength
     *
     * // Calculate experience bonus based on strength
     * float baseExp = 100.0f;
     * float bonus = baseExp * instance.getStrength() * 0.5f;
     * instance.setExperience(instance.getExperience() + baseExp + bonus);
    `</pre> *
     */
    var strength = 0.0f
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Current development level of the spiritual root.
     *
     *
     * Levels progress from I to X, with each subsequent level requiring
     * exponentially more experience and unlocking new capabilities.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Check if player can use advanced techniques
     * if (instance.getLevel().getLevel() >= RootLevels.V.getLevel()) {
     * // Unlock powerful techniques at level V
     * player.sendSystemMessage(Component.literal("Advanced techniques unlocked!"));
     * }
     *
     * // Level-based damage calculation
     * float damage = 10.0f * (1 + instance.getLevel().getLevel() * 0.2f);
    `</pre> *
     */
    var level: RootLevels? = RootLevels.I
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Accumulated cultivation experience.
     *
     *
     * Experience is gained through meditation, technique usage, absorbing elemental
     * resources, and other cultivation activities. Automatic advancement occurs
     * when reaching threshold values.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Add experience and check for level up
     * float expGain = 25.0f;
     * instance.setExperience(instance.getExperience() + expGain);
     * instance.updateLevel(player);
     *
     * // Calculate progress to next level
     * float currentExp = instance.getExperience();
     * float requiredExp = instance.getLevel().getExperience();
     * float progress = currentExp / requiredExp;
    `</pre> *
     */
    var experience = 0.0f
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Whether techniques of this spiritual root are accessible.
     *
     *
     * Only active roots allow the use of corresponding techniques and provide
     * attribute bonuses. An entity may have multiple roots, but not all of
     * them are necessarily active simultaneously.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Activate root and apply bonuses
     * if (!instance.isActive()) {
     * instance.setActive(true);
     * instance.addAttributeModifiers(player);
     * player.sendSystemMessage(Component.literal("Spiritual root activated!"));
     * }
     *
     * // Check if techniques are available
     * if (instance.isActive()) {
     * // Allow technique usage
     * executeFireballTechnique(player, instance);
     * }
    `</pre> *
     */
    var active = false
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Purity of the spiritual root - quality indicator from 0.0 to 1.0.
     *
     *
     * High purity increases cultivation efficiency, breakthrough chances,
     * and reduces negative effects during cultivation.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Purity affects breakthrough success rate
     * float baseChance = 0.3f; // 30% base chance
     * float purityBonus = instance.getPurity() * 0.4f; // Up to 40% bonus
     * float finalChance = Math.min(baseChance + purityBonus, 0.9f);
     *
     * if (random.nextFloat() < finalChance) {
     * // Successful breakthrough
     * instance.setLevel(instance.getLevel().getNext());
     * }
    `</pre> *
     */
    var purity = 0f
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Additional data for functionality extension.
     *
     *
     * This tag can be used by subclasses or addons to store specific
     * information not covered by the base fields.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Store custom awakening data
     * CompoundTag customData = new CompoundTag();
     * customData.putString("awakening_location", "ancient_temple");
     * customData.putLong("awakening_time", System.currentTimeMillis());
     * customData.putBoolean("blessed_by_master", true);
     * instance.setTag(customData);
     *
     * // Retrieve custom data
     * CompoundTag tag = instance.getTag();
     * if (tag != null && tag.contains("blessed_by_master")) {
     * boolean blessed = tag.getBoolean("blessed_by_master");
     * if (blessed) {
     * // Apply special blessing effects
     * }
     * }
    `</pre> *
     */
    var tag: CompoundTag? = null

    /**
     * Flag indicating the need for synchronization with the client.
     *
     *
     * Automatically set when important instance data changes and reset
     * after successful synchronization.
     *
     */
    var dirty = false

    //endregion
    //region Constructors and Creation

    //endregion
    //region Basic Information Access Methods
    val spiritualRoot: SpiritualRoot?
        /**
         * Gets the spiritual root type for this instance.
         *
         *
         * Returns the base [SpiritualRoot] that defines the behavior
         * and characteristics of this instance.
         *
         *
         *
         * **Example Usage:**
         *
         * <pre>`SpiritualRootInstance instance = // ... get instance
         * SpiritualRoot rootType = instance.getSpiritualRoot();
         *
         * // Using type to check characteristics
         * RootLevels maxLevel = rootType.getMaxLevel();
         * RootType type = rootType.getType();
         *
         * // Check special properties
         * if (rootType.canAdvance(instance, player)) {
         * System.out.println("Root can advance to next level");
         * }
        `</pre> *
         *
         * @return The spiritual root type
         */
        get() = spiritualRootRegistrySupplier.get()

        val spiritualRootId: ResourceLocation?
        /**
         * Gets the identifier of the spiritual root type.
         *
         *
         * The identifier is used for serialization, registration,
         * and identification of the root type in the system.
         *
         *
         *
         * **Example Usage:**
         *
         * <pre>`SpiritualRootInstance instance = // ... get instance
         * ResourceLocation id = instance.getSpiritualRootId();
         *
         * // Using for type comparison
         * ResourceLocation fireRootId = new ResourceLocation("eternalcore", "fire_root");
         * if (id.equals(fireRootId)) {
         * System.out.println("This is a fire spiritual root");
         * }
         *
         * // Using in configuration
         * config.putString("player_root_type", id.toString());
         *
         * // Logging for diagnostics
         * logger.info("Activated root: {}", id);
        `</pre> *
         *
         * @return ResourceLocation identifier of the spiritual root type
         */
        get() = this.spiritualRootRegistrySupplier.id


    var displayName: MutableComponent? = null
        /**
         * Gets the localized name of the spiritual root for display.
         *
         *
         * The name is automatically localized according to the client's language
         * and can be used in the user interface.
         *
         *
         *
         * **Example Usage:**
         *
         * <pre>`SpiritualRootInstance instance = // ... get instance
         * MutableComponent displayName = instance.getDisplayName();
         *
         * // Display in chat
         * player.sendSystemMessage(Component.literal("Your spiritual root: ")
         * .append(displayName.withStyle(ChatFormatting.GOLD)));
         *
         * // Using in GUI
         * tooltip.add(Component.literal("Type: ").append(displayName));
         *
         * // Creating complex message
         * MutableComponent message = Component.literal("Root ")
         * .append(displayName.withStyle(ChatFormatting.AQUA))
         * .append(" reached level ")
         * .append(Component.literal(String.valueOf(instance.getLevel().getLevel()))
         * .withStyle(ChatFormatting.YELLOW));
        `</pre> *
         *
         * @return Localized component with the spiritual root name
         */
        get() = this.spiritualRoot!!.name
        set(value) {
            field = value
            markDirty()
        }

    var type: RootType? = null
        /**
         * Gets the type (category) of this spiritual root.
         *
         *
         * The type determines rarity, potential, and special characteristics of the root.
         *
         *
         *
         * **Example Usage:**
         *
         * <pre>`SpiritualRootInstance instance = // ... get instance
         * RootType type = instance.getType();
         *
         * // Setting bonuses based on type
         * float experienceMultiplier = switch (type) {
         * case COMMON -> 1.0f;
         * case RARE -> 1.2f;
         * case EPIC -> 1.5f;
         * case LEGENDARY -> 2.0f;
         * case MYTHIC -> 3.0f;
         * };
         *
         * // Determining display color
         * ChatFormatting color = switch (type) {
         * case COMMON -> ChatFormatting.WHITE;
         * case RARE -> ChatFormatting.BLUE;
         * case EPIC -> ChatFormatting.DARK_PURPLE;
         * case LEGENDARY -> ChatFormatting.GOLD;
         * case MYTHIC -> ChatFormatting.DARK_RED;
         * };
         *
         * // Check evolution availability
         * if (type.ordinal() >= RootType.EPIC.ordinal()) {
         * // Only epic and higher roots can evolve
         * SpiritualRoot evolved = instance.getFirstDegree(player);
         * }
        `</pre> *
         *
         * @return The spiritual root type
         * @see RootType
         */
        get() = this.spiritualRoot!!.type
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Checks if this spiritual root belongs to the specified tag.
     *
     *
     * Tags are used to group spiritual roots by certain criteria,
     * such as element, rarity, or origin.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Define tags
     * TagKey<SpiritualRoot> fireRoots = TagKey.create(
     * SpiritualRootAPI.getSpiritualRootRegistry().key(),
     * new ResourceLocation("eternalcore", "fire_element")
     * );
     * TagKey<SpiritualRoot> rareRoots = TagKey.create(
     * SpiritualRootAPI.getSpiritualRootRegistry().key(),
     * new ResourceLocation("eternalcore", "rare_roots")
     * );
     *
     * SpiritualRootInstance instance = // ... get instance
     *
     * // Check tag membership
     * if (instance.is(fireRoots)) {
     * // Apply fire effects
     * player.setSecondsOnFire(0); // Fire immunity
     *
     * // Bonuses in Nether
     * if (player.level().dimension() == Level.NETHER) {
     * instance.setExperience(instance.getExperience() + 10.0f);
     * }
     * }
     *
     * if (instance.is(rareRoots)) {
     * // Special effects for rare roots
     * player.addEffect(new MobEffectInstance(MobEffects.LUCK, 1200, 0));
     * }
     *
     * // Check root compatibility
     * TagKey<SpiritualRoot> conflictingRoots = TagKey.create(
     * SpiritualRootAPI.getSpiritualRootRegistry().key(),
     * new ResourceLocation("eternalcore", "water_element")
     * );
     *
     * if (instance.is(fireRoots)) {
     * // Check other player roots for conflicts
     * for (SpiritualRootInstance other : playerRoots) {
     * if (other.is(conflictingRoots)) {
     * // Conflict between fire and water
     * applyConflictPenalty(player);
     * }
     * }
     * }
    `</pre> *
     *
     * @param tag Tag to check
     * @return true if the root belongs to the tag
     */
    fun `is`(tag: TagKey<SpiritualRoot?>?): Boolean {
        return this.spiritualRootRegistrySupplier.`is`(tag)
    }

    //endregion
    //region Serialization Methods
    /**
     * Serializes the spiritual root instance to NBT format.
     *
     *
     * This method ensures saving of all necessary information
     * for complete restoration of the instance state. Includes base data
     * and calls [.serialize] for additional data.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`SpiritualRootInstance instance = // ... configured instance
     * instance.setLevel(RootLevels.V);
     * instance.setExperience(750.0f);
     * instance.setStrength(0.9f);
     * instance.setPurity(0.7f);
     * instance.setActive(true);
     *
     * // Serialize to NBT
     * CompoundTag nbt = instance.toNBT();
     *
     * // Save to world file
     * CompoundTag playerData = player.getPersistentData();
     * CompoundTag spiritualData = playerData.getCompound("spiritual_roots");
     * spiritualData.put("primary_root", nbt);
     * playerData.put("spiritual_roots", spiritualData);
     *
     * // Send to client via packet
     * FriendlyByteBuf buffer = PacketByteBufs.create();
     * buffer.writeNbt(nbt);
     * ServerPlayNetworking.send(serverPlayer, SYNC_ROOTS_PACKET, buffer);
     *
     * // Save to configuration
     * String nbtString = nbt.toString();
     * config.set("saved_root", nbtString);
     *
     * // Logging for debug
     * System.out.println("Saved root: " + nbt);
    `</pre> *
     *
     * @return CompoundTag with all instance data
     * @see .fromNBT
     * @see .serialize
     */
    open fun toNBT(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString(KEY, this.spiritualRootId.toString())
        serialize(nbt)
        return nbt
    }

    /**
     * Serializes specific instance data to NBT tag.
     *
     *
     * This method can be overridden by subclasses to save additional data.
     * The base implementation saves:
     *
     *  * Development level
     *  * Accumulated experience
     *  * Root strength
     *  * Root purity
     *  * Additional tags (if any)
     *
     *
     *
     *
     * **Example of overriding in subclass:**
     *
     * <pre>`public class AdvancedSpiritualRootInstance extends SpiritualRootInstance {
     * private String specialAbility;
     * private int meditationTime;
     * private List<String> unlockedTechniques = new ArrayList<>();
     *
     *
     * public CompoundTag serialize(CompoundTag nbt) {
     * // Call base serialization
     * super.serialize(nbt);
     *
     * // Add special data
     * if (specialAbility != null) {
     * nbt.putString("special_ability", specialAbility);
     * }
     * nbt.putInt("meditation_time", meditationTime);
     *
     * // Serialize technique list
     * if (!unlockedTechniques.isEmpty()) {
     * ListTag techniquesList = new ListTag();
     * for (String technique : unlockedTechniques) {
     * techniquesList.add(StringTag.valueOf(technique));
     * }
     * nbt.put("unlocked_techniques", techniquesList);
     * }
     *
     * return nbt;
     * }
     * }
     *
     * // Using with additional tags
     * SpiritualRootInstance instance = // ... create instance
     *
     * // Adding custom data
     * CompoundTag customData = new CompoundTag();
     * customData.putString("awakening_location", "ancient_temple");
     * customData.putLong("awakening_time", System.currentTimeMillis());
     * customData.putBoolean("blessed_by_master", true);
     * instance.setTag(customData);
     *
     * // Serialization will save all data
     * CompoundTag saved = instance.toNBT();
    `</pre> *
     *
     * @param nbt NBT tag for saving data
     * @return The same NBT tag with added data
     * @see .deserialize
     */
    open fun serialize(nbt: CompoundTag): CompoundTag {
        if (this.tag != null) nbt.put("tag", this.tag!!.copy())
        nbt.putInt(LEVEL_KEY, this.level!!.level)
        nbt.putFloat(EXPERIENCE_KEY, this.experience)
        nbt.putFloat(STRENGTH_KEY, this.strength)
        nbt.putFloat(PURITY_KEY, purity)
        return nbt
    }

    /**
     * Deserializes instance data from NBT tag.
     *
     *
     * Restores instance state from saved data. The method safely handles
     * missing fields by using default values.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Create NBT with saved data
     * CompoundTag savedData = new CompoundTag();
     * savedData.putString("spiritual_root", "eternalcore:earth_root");
     * savedData.putInt("level", 4);
     * savedData.putFloat("experience", 450.0f);
     * savedData.putFloat("strength", 0.7f);
     * savedData.putFloat("Purity", 0.8f);
     *
     * // Add custom tag data
     * CompoundTag customTag = new CompoundTag();
     * customTag.putString("origin", "mountain_peak");
     * savedData.put("tag", customTag);
     *
     * // Deserialize instance
     * SpiritualRootInstance restored = SpiritualRootInstance.fromNBT(savedData);
     *
     * // Verify restoration
     * System.out.println("Level: " + restored.getLevel()); // Level: IV
     * System.out.println("Experience: " + restored.getExperience()); // Experience: 450.0
     * System.out.println("Strength: " + restored.getStrength()); // Strength: 0.7
    `</pre> *
     *
     * @param tag NBT tag with saved data
     * @see .serialize
     */
    open fun deserialize(tag: CompoundTag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag")
        if (tag.contains(LEVEL_KEY)) this.level = RootLevels.Companion.byId(tag.getInt(LEVEL_KEY))
        if (tag.contains(EXPERIENCE_KEY)) this.experience = tag.getFloat(EXPERIENCE_KEY)
        if (tag.contains(PURITY_KEY)) this.purity = tag.getFloat(PURITY_KEY)
        if (tag.contains(STRENGTH_KEY)) this.strength = tag.getFloat(STRENGTH_KEY)
    }

    //endregion
    //region State Management Methods
    /**
     * Marks the instance as changed for synchronization with the client.
     *
     *
     * This method is automatically called when important parameters change
     * and signals the system about the need for data synchronization.
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Manual marking (usually not needed as setters do this automatically)
     * instance.markDirty();
     *
     * // Check if synchronization is needed
     * if (instance.isDirty()) {
     * // Send update packet to client
     * sendSyncPacket(player, instance);
     * instance.resetDirty(); // Reset after sync
     * }
     *
     * // Custom modification that requires manual dirty marking
     * CompoundTag customTag = instance.getTag();
     * if (customTag == null) {
     * customTag = new CompoundTag();
     * instance.setTag(customTag);
     * }
     * customTag.putString("last_meditation", "temple_ruins");
     * instance.markDirty(); // Manual marking required
    `</pre> *
     */
    fun markDirty() {
        this.dirty = true
    }

    /**
     * Скидає прапорець синхронізації після успішної передачі даних клієнту.
     *
     *
     * **УВАГА:** Цей метод призначений тільки для внутрішнього
     * використання системою синхронізації. Не викликайте його самостійно!
     *
     */
    @ApiStatus.Internal
    fun resetDirty() {
        this.dirty = false
    }


    //endregion
    //region Progression and Development Methods
    /**
     * Updates the spiritual root level based on accumulated experience.
     *
     *
     * Automatically advances the root to subsequent levels if sufficient
     * experience has been accumulated. The process continues until reaching
     * the maximum level or exhausting experience points.
     *
     *
     *
     * With each advancement, the [.onAdvance] method
     * is called to activate corresponding effects and events.
     *
     *
     *
     * **Level Progression Logic:**
     *
     *
     *  * Checks if current experience meets or exceeds level requirement
     *  * Advances to next level if conditions are met
     *  * Triggers advancement effects and notifications
     *  * Continues until max level or insufficient experience
     *  * Automatically marks instance as dirty for synchronization
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`// Standard level update after gaining experience
     * SpiritualRootInstance instance = // ... get instance
     * instance.setExperience(instance.getExperience() + 150.0f);
     * instance.updateLevel(player);
     *
     * // Check if level changed
     * RootLevels previousLevel = RootLevels.III;
     * if (instance.getLevel().getLevel() > previousLevel.getLevel()) {
     * player.sendSystemMessage(Component.literal("Spiritual root advanced to level ")
     * .append(Component.literal(instance.getLevel().name())
     * .withStyle(ChatFormatting.GOLD)));
     * }
     *
     * // Batch experience processing
     * float totalExp = calculateMeditationReward(player);
     * instance.setExperience(instance.getExperience() + totalExp);
     * instance.updateLevel(player);
     *
     * // Level progression with notifications
     * RootLevels startLevel = instance.getLevel();
     * instance.updateLevel(player);
     * RootLevels endLevel = instance.getLevel();
     *
     * if (endLevel.getLevel() > startLevel.getLevel()) {
     * int levelsGained = endLevel.getLevel() - startLevel.getLevel();
     * player.displayClientMessage(Component.literal("Advanced " + levelsGained + " levels!"), true);
     *
     * // Apply level-based rewards
     * player.giveExperiencePoints(levelsGained * 10);
     *
     * // Special effects for major milestones
     * if (endLevel == RootLevels.V) {
     * player.level().playSound(null, player.blockPosition(),
     * SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 2.0f);
     * }
     * }
    `</pre> *
     *
     *
     * **Performance Notes:**
     *
     *
     *  * Uses while loop to handle multiple level advances in single call
     *  * Efficiently processes large experience gains
     *  * Stops automatically at maximum level to prevent infinite loops
     *  * Triggers dirty marking for network synchronization
     *
     *
     * @param entity The entity whose spiritual root is developing
     * @see .onAdvance
     * @see .canAdvance
     * @see RootLevels.getExperience
     */
    fun updateLevel(entity: LivingEntity) {
        while (experience >= level!!.experience && level != this.spiritualRoot!!.maxLevel) {
            level = level!!.next
            onAdvance(entity)
        }
    }

    /**
     * Checks whether the spiritual root can advance to the next level.
     *
     *
     * Advancement conditions may include sufficient experience, availability of resources,
     * suitable environment, or completion of special tasks. The actual logic is delegated
     * to the specific [SpiritualRoot] implementation, allowing for custom advancement
     * requirements per root type.
     *
     *
     *
     * **Common Advancement Requirements:**
     *
     *
     *  * **Experience:** Sufficient cultivation experience accumulated
     *  * **Resources:** Special cultivation materials or elixirs
     *  * **Environment:** Specific locations with high spiritual energy
     *  * **Quests:** Completion of trials or spiritual tests
     *  * **Compatibility:** No conflicting spiritual roots active
     *  * **Time:** Minimum cultivation time requirements
     *  * **Mentorship:** Guidance from advanced cultivators
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`SpiritualRootInstance instance = // ... get instance
     * Player player = // ... get player
     *
     * // Basic advancement check
     * if (instance.canAdvance(player)) {
     * player.sendSystemMessage(Component.literal("Your spiritual root can advance!")
     * .withStyle(ChatFormatting.GREEN));
     *
     * // Show advancement requirements
     * RootLevels nextLevel = instance.getLevel().getNext();
     * float requiredExp = nextLevel.getExperience();
     * float currentExp = instance.getExperience();
     *
     * if (currentExp >= requiredExp) {
     * player.sendSystemMessage(Component.literal("Requirements met. Advancing...")
     * .withStyle(ChatFormatting.GOLD));
     * instance.updateLevel(player);
     * }
     * } else {
     * // Show why advancement is blocked
     * player.sendSystemMessage(Component.literal("Advancement requirements not met")
     * .withStyle(ChatFormatting.RED));
     * }
     *
     * // Pre-advancement validation in GUI
     * boolean canAdvance = instance.canAdvance(player);
     * Button advanceButton = Button.builder(Component.literal("Advance"))
     * .onPress(button -> {
     * if (instance.canAdvance(player)) {
     * instance.updateLevel(player);
     * }
     * })
     * .build();
     * advanceButton.active = canAdvance;
     *
     * // Check advancement prerequisites
     * if (instance.getLevel() == RootLevels.IV && instance.canAdvance(player)) {
     * // Special handling for major breakthrough
     * if (hasBreakthroughPill(player) && isInCultivationChamber(player)) {
     * // Allow advancement with special conditions
     * consumeBreakthroughPill(player);
     * instance.updateLevel(player);
     * player.sendSystemMessage(Component.literal("Major breakthrough achieved!")
     * .withStyle(ChatFormatting.LIGHT_PURPLE));
     * } else {
     * player.sendSystemMessage(Component.literal("Major breakthrough requires:")
     * .append("\n- Breakthrough Pill")
     * .append("\n- Cultivation Chamber"));
     * }
     * }
     *
     * // Scheduled advancement check
     * if (player.tickCount % 200 == 0) { // Every 10 seconds
     * if (instance.canAdvance(player) && instance.getExperience() >= instance.getLevel().getExperience()) {
     * // Auto-advance if conditions met
     * instance.updateLevel(player);
     * }
     * }
    `</pre> *
     *
     *
     * **Implementation Notes:**
     *
     *
     *  * Delegates to [SpiritualRoot.canAdvance]
     *  * Allows for root-specific advancement logic
     *  * Should be checked before calling [.updateLevel]
     *  * May perform expensive calculations, cache results when possible
     *
     *
     * @param entity The entity wishing to advance the root
     * @return true if advancement is possible, false otherwise
     * @see SpiritualRoot.canAdvance
     * @see .updateLevel
     * @see RootLevels.getNext
     */
    fun canAdvance(entity: LivingEntity): Boolean {
        return this.spiritualRoot!!.canAdvance(this, entity)
    }

    /**
     * Increases the strength of the spiritual root by the specified amount.
     *
     *
     * Delegates the call to the base spiritual root type to apply
     * specific strength enhancement logic. Root strength affects cultivation
     * efficiency, technique power, and elemental affinity.
     *
     *
     *
     * **Strength Enhancement Effects:**
     *
     *
     *  * **Cultivation Speed:** Higher strength increases experience gain rates
     *  * **Technique Power:** Amplifies damage and effectiveness of abilities
     *  * **Elemental Affinity:** Improves interaction with corresponding elements
     *  * **Breakthrough Chance:** Increases success rate for level advancement
     *  * **Resource Efficiency:** Reduces consumption of cultivation materials
     *  * **Stability:** Prevents cultivation deviation and accidents
     *
     *
     *
     * **Example Usage:**
     *
     * <pre>`SpiritualRootInstance instance = // ... get instance
     * Player player = // ... get player
     *
     * // Basic strength increase
     * float strengthGain = 0.1f; // 10% increase
     * instance.increaseStrength(player, strengthGain);
     *
     * // Strength gain from elixirs
     * ItemStack elixir = // ... strength elixir item
     * if (elixir.getItem() instanceof StrengthElixirItem strengthElixir) {
     * float bonus = strengthElixir.getStrengthBonus();
     * instance.increaseStrength(player, bonus);
     *
     * player.sendSystemMessage(Component.literal("Root strength increased by ")
     * .append(Component.literal(String.format("%.1f%%", bonus * 100))
     * .withStyle(ChatFormatting.GREEN)));
     * }
     *
     * // Meditation-based strength improvement
     * if (isMeditating(player)) {
     * float meditationBonus = calculateMeditationStrengthGain(player, instance);
     * if (meditationBonus > 0) {
     * instance.increaseStrength(player, meditationBonus);
     *
     * // Visual feedback
     * spawnMeditationParticles(player);
     * player.level().playSound(null, player.blockPosition(),
     * SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.5f);
     * }
     * }
     *
     * // Strength milestone rewards
     * float currentStrength = instance.getStrength();
     * instance.increaseStrength(player, 0.05f);
     * float newStrength = instance.getStrength();
     *
     * // Check for strength thresholds
     * if (currentStrength < 0.5f && newStrength >= 0.5f) {
     * // Reached 50% strength milestone
     * player.sendSystemMessage(Component.literal("Root purity milestone reached!")
     * .withStyle(ChatFormatting.GOLD));
     * player.giveExperiencePoints(50);
     * }
     *
     * if (currentStrength < 1.0f && newStrength >= 1.0f) {
     * // Perfect strength achieved
     * player.sendSystemMessage(Component.literal("Perfect root purity achieved!")
     * .withStyle(ChatFormatting.LIGHT_PURPLE));
     * player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
     * }
     *
     * // Strength-based technique unlocking
     * if (instance.getStrength() >= 0.8f) {
     * // Unlock powerful techniques at 80% strength
     * unlockAdvancedTechniques(player, instance);
     * }
     *
     * // Group cultivation strength sharing
     * if (isInCultivationGroup(player)) {
     * List<Player> groupMembers = getCultivationGroupMembers(player);
     * float sharedBonus = strengthGain * 0.1f; // 10% shared to group
     *
     * for (Player member : groupMembers) {
     * if (member != player) {
     * SpiritualRootInstance memberRoot = getSpiritualRoot(member);
     * if (memberRoot != null) {
     * memberRoot.increaseStrength(member, sharedBonus);
     * member.sendSystemMessage(Component.literal("Gained strength from group cultivation"));
     * }
     * }
     * }
     * }
    `</pre> *
     *
     *
     * **Balancing Considerations:**
     *
     *
     *  * Strength increases should be carefully balanced to prevent overpowering
     *  * Consider diminishing returns for very high strength values
     *  * Different root types may have different strength scaling
     *  * Strength loss mechanisms may be needed for game balance
     *
     *
     * @param living The entity whose root strength is being increased
     * @param amount The amount to increase strength by (typically 0.0-1.0 range)
     * @see SpiritualRoot.increaseStrength
     * @see .getStrength
     * @see .setStrength
     */
    fun increaseStrength(living: LivingEntity, amount: Float) {
        this.spiritualRoot!!.increaseStrength(this, living, amount)
    }

    //endregion
    //region МЕТОДИ ЕЛЕМЕНТІВ ТА ЕВОЛЮЦІЇ
    /**
     * Отримує елемент, пов'язаний з цим духовним коренем.
     *
     *
     * Елемент визначає тип елементальної енергії та впливає на
     * взаємодію з навколишнім середовищем і техніками. Кожен духовний
     * корінь може мати властивий йому елемент або бути нейтральним.
     *
     *
     * **Приклади елементів:**
     *
     *  * Вогонь - підвищує атаку та опір до вогню
     *  * Вода - покращує регенерацію та захист
     *  * Земля - збільшує міцність та стабільність
     *  * Повітря - надає швидкість та спритність
     *
     *
     * @param entity Сутність, для якої визначається елемент (не може бути null)
     * @return Елемент духовного кореня або null, якщо елемент не визначено
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see Element
     *
     * @see SpiritualRoot.getElement
     */
    fun getElement(entity: LivingEntity): Element? {
        return this.spiritualRoot!!.getElement(this, entity)
    }

    /**
     * Отримує перший ступінь еволюції цього духовного кореня.
     *
     *
     * Еволюція дозволяє корению розвинутися в більш потужну форму
     * з покращеними характеристиками та новими здібностями. Перший ступінь
     * зазвичай досягається через накопичення досвіду та виконання певних умов.
     *
     *
     * **Умови еволюції можуть включати:**
     *
     *  * Досягнення певного рівня
     *  * Накопичення достатньої кількості досвіду
     *  * Виконання спеціальних завдань або ритуалів
     *  * Отримання рідкісних матеріалів
     *
     *
     * @param living Сутність, для якої визначається еволюція (не може бути null)
     * @return Еволюціонований духовний корінь першого ступеня або null,
     * якщо еволюція недоступна або умови не виконані
     *
     * @throws IllegalArgumentException якщо living є null
     *
     * @see .getSecondDegree
     * @see SpiritualRoot.getFirstDegree
     */
    fun getFirstDegree(living: LivingEntity): SpiritualRoot? {
        return this.spiritualRoot!!.getFirstDegree(this, living)
    }

    /**
     * Отримує другий ступінь еволюції цього духовного кореня.
     *
     *
     * Другий ступінь представляє найвищий рівень еволюції з унікальними
     * здібностями та значно покращеними характеристиками. Це рідкісне досягнення,
     * яке потребує виключних зусиль та ресурсів від культиватора.
     *
     *
     * **Переваги другого ступеня:**
     *
     *  * Унікальні та потужні здібності
     *  * Значно підвищені модифікатори атрибутів
     *  * Ексклюзивні техніки культивації
     *  * Престиж та визнання серед інших культиваторів
     *
     *
     * @param living Сутність, для якої визначається еволюція другого ступеня (не може бути null)
     * @return Еволюціонований духовний корінь другого ступеня або null,
     * якщо еволюція недоступна або не досягнуто необхідних умов
     *
     * @throws IllegalArgumentException якщо living є null
     *
     * @see .getFirstDegree
     * @see SpiritualRoot.getSecondDegree
     */
    fun getSecondDegree(living: LivingEntity): SpiritualRoot? {
        return this.spiritualRoot!!.getSecondDegree(this, living)
    }

    /**
     * Отримує попередній ступінь еволюції цього духовного кореня.
     *
     *
     * Дозволяє отримати посилання на попередню форму духовного кореня
     * в ланцюгу еволюції. Корисно для відстеження прогресу та можливості
     * регресії в разі необхідності.
     *
     *
     * **Використання:**
     *
     *  * Відстеження історії еволюції
     *  * Механізми регресії або скидання
     *  * Порівняння характеристик різних ступенів
     *
     *
     * @param living Сутність, для якої визначається попередній ступінь (не може бути null)
     * @return Попередній ступінь духовного кореня або null, якщо це базова форма
     *
     * @throws IllegalArgumentException якщо living є null
     *
     * @see SpiritualRoot.getPreviousDegree
     */
    fun getPreviousDegree(living: LivingEntity): SpiritualRoot? {
        return this.spiritualRoot!!.getPreviousDegree(this, living)
    }

    /**
     * Отримує протилежний духовний корінь для поточного.
     *
     *
     * Протилежні корені мають конфліктуючі елементальні властивості
     * та можуть створювати проблеми при одночасному культивуванні. Знання
     * протилежного кореня важливе для розуміння потенційних конфліктів
     * та стратегічного планування розвитку.
     *
     *
     * **Приклади протилежностей:**
     *
     *  * Вогонь ↔ Вода
     *  * Земля ↔ Повітря
     *  * Світло ↔ Темрява
     *  * Порядок ↔ Хаос
     *
     *
     *
     * **Наслідки конфлікту:**
     *
     *  * Зменшення ефективності культивації
     *  * Ризик внутрішньої нестабільності
     *  * Можливість бойових переваг проти носіїв протилежних коренів
     *
     *
     * @param entity Сутність, для якої визначається протилежний корінь (не може бути null)
     * @return Протилежний духовний корінь або null, якщо протилежність не визначена
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see Element
     *
     * @see SpiritualRoot.getOpposite
     */
    fun getOpposite(entity: LivingEntity): SpiritualRoot? {
        return this.spiritualRoot!!.getOpposite(this, entity)
    }

    //endregion
    //region МЕТОДИ УПРАВЛІННЯ АТРИБУТАМИ
    /**
     * Застосовує модифікатори атрибутів цього духовного кореня до сутності.
     *
     *
     * Викликається при активації кореня для надання бонусів до характеристик.
     * Модифікатори можуть включати збільшення сили, швидкості, здоров'я тощо.
     * Ефект залежить від типу духовного кореня та його поточного рівня розвитку.
     *
     *
     * **Типи модифікаторів:**
     *
     *  * **Адитивні** - додають фіксоване значення
     *  * **Мультиплікативні** - множать на коефіцієнт
     *  * **Процентні** - додають відсоток від базового значення
     *
     *
     *
     * **Атрибути що можуть змінюватися:**
     *
     *  * Максимальне здоров'я
     *  * Сила атаки
     *  * Швидкість руху
     *  * Броня та стійкість
     *  * Регенерація
     *  * Удача
     *
     *
     *
     * **Важливо:** Цей метод має бути збалансований з [.removeAttributeModifiers]
     * для правильного управління життєвим циклом модифікаторів.
     *
     * @param entity Сутність, до якої застосовуються модифікатори (не може бути null)
     *
     * @throws IllegalArgumentException якщо entity є null
     * @throws IllegalStateException якщо духовний корінь не ініціалізований
     *
     * @see .removeAttributeModifiers
     * @see AttributeModifier
     *
     * @see SpiritualRoot.addAttributeModifiers
     */
    fun addAttributeModifiers(entity: LivingEntity) {
        this.spiritualRoot!!.addAttributeModifiers(this, entity)
    }

    /**
     * Видаляє модифікатори атрибутів цього духовного кореня від сутності.
     *
     *
     * Викликається при деактивації кореня або його заміні для видалення
     * раніше застосованих бонусів. Забезпечує чистоту стану атрибутів сутності
     * та запобігає накопиченню застарілих модифікаторів.
     *
     *
     * **Сценарії використання:**
     *
     *  * Деактивація духовного кореня
     *  * Заміна одного кореня на інший
     *  * Еволюція до наступного ступеня
     *  * Тимчасове припинення ефектів
     *  * Скидання системи культивації
     *
     *
     *
     * **Безпека операції:**
     *
     *  * Метод є ідемпотентним - безпечний для повторних викликів
     *  * Не впливає на модифікатори від інших джерел
     *  * Автоматично обробляє відсутні модифікатори
     *
     *
     * @param entity Сутність, від якої видаляються модифікатори (не може бути null)
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see .addAttributeModifiers
     * @see AttributeModifier
     *
     * @see SpiritualRoot.removeAttributeModifiers
     */
    fun removeAttributeModifiers(entity: LivingEntity) {
        this.spiritualRoot!!.removeAttributeModifiers(this, entity)
    }

    //endregion
    //region МЕТОДИ ПОДІЙ ЖИТТЄВОГО ЦИКЛУ
    /**
     * Викликається при першому отриманні цього духовного кореня сутністю.
     *
     *
     * Дозволяє виконати ініціалізацію, надати початкові бонуси
     * або активувати спеціальні ефекти при отриманні кореня. Це ключова
     * подія в життєвому циклі духовного кореня, яка встановлює початковий стан.
     *
     *
     * **Типові дії при активації:**
     *
     *  * Застосування початкових модифікаторів атрибутів
     *  * Розблокування базових здібностей
     *  * Ініціалізація внутрішніх параметрів
     *  * Відображення повідомлень гравцю
     *  * Запуск візуальних та звукових ефектів
     *  * Синхронізація стану з клієнтом
     *
     *
     *
     * **Послідовність викликів:**
     *
     *  1. Створення екземпляра духовного кореня
     *  1. Виклик `onAdd()`
     *  1. Застосування початкових ефектів
     *  1. Синхронізація з клієнтом
     *
     *
     *
     * **Примітка:** Цей метод викликається лише один раз для кожного
     * екземпляра кореня. При еволюції створюється новий екземпляр.
     *
     * @param living Сутність, яка отримала духовний корінь (не може бути null)
     *
     * @throws IllegalArgumentException якщо living є null
     * @throws IllegalStateException якщо корінь вже був активований
     *
     * @see .onAdvance
     * @see .addAttributeModifiers
     * @see SpiritualRoot.onAdd
     */
    fun onAdd(living: LivingEntity) {
        this.spiritualRoot!!.onAdd(this, living)
    }

    /**
     * Викликається при просуванні духовного кореня на наступний рівень.
     *
     *
     * Дозволяє виконати дії, специфічні для просування: покращення
     * модифікаторів, розблокування здібностей, візуальні ефекти тощо.
     * Це важлива подія, яка відзначає прогрес культиватора.
     *
     *
     * **Дії при просуванні:**
     *
     *  * **Оновлення модифікаторів:** Покращення існуючих бонусів
     *  * **Нові здібності:** Розблокування додаткових можливостей
     *  * **Візуальні ефекти:** Частинки, свічення, анімації
     *  * **Звукові ефекти:** Звуки досягнення та прогресу
     *  * **Повідомлення:** Інформування гравця про досягнення
     *  * **Статистика:** Оновлення досягнень та рекордів
     *
     *
     *
     * **Умови просування:**
     *
     *  * Накопичення достатньої кількості досвіду
     *  * Досягнення порогових значень характеристик
     *  * Виконання спеціальних завдань або ритуалів
     *  * Отримання рідкісних матеріалів для просування
     *
     *
     *
     * **Примітка:** Метод викликається кожного разу при збільшенні рівня,
     * включаючи автоматичні просування через накопичення досвіду.
     *
     * @param living Сутність, чий духовний корінь просувається (не може бути null)
     *
     * @throws IllegalArgumentException якщо living є null
     * @throws IllegalStateException якщо корінь не готовий до просування
     *
     * @see .onAdd
     * @see .onAddExperience
     * @see .getLevel
     * @see SpiritualRoot.onAdvance
     */
    fun onAdvance(living: LivingEntity) {
        this.spiritualRoot!!.onAdvance(this, living)
    }

    /**
     * Викликається при отриманні досвіду для цього духовного кореня.
     *
     *
     * Дозволяє додати спеціальну логіку обробки досвіду, такі як
     * бонусні ефекти або додаткові нарахування. Цей метод забезпечує
     * гнучкість в системі прогресу культивації.
     *
     *
     * **Можливості обробки досвіду:**
     *
     *  * **Бонусні множники:** Збільшення досвіду за певних умов
     *  * **Розподіл досвіду:** Передача частини іншим аспектам
     *  * **Спеціальні ефекти:** Візуальні індикатори прогресу
     *  * **Додаткові нарахування:** Бонуси від елементів або подій
     *  * **Обмеження:** Контроль максимальної швидкості прогресу
     *
     *
     *
     * **Джерела досвіду:**
     *
     *  * Медитація та духовні практики
     *  * Бої та перемоги над ворогами
     *  * Споживання спеціальних предметів
     *  * Взаємодія з елементальними джерелами
     *  * Виконання квестів та завдань
     *
     *
     *
     * **Важлиᵽво:** Цей метод викликається до фактичного додавання досвіду,
     * що дозволяє модифікувати кількість або додати додаткові ефекти.
     *
     * @param entity Сутність, яка отримує досвід культивації (не може бути null)
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see .getExperience
     * @see .onAdvance
     * @see SpiritualRoot.onAddExperience
     */
    fun onAddExperience(entity: LivingEntity, amount: Float) {
        this.spiritualRoot!!.onAddExperience(this, entity, amount)
    }

    //endregion
    //region СИСТЕМНІ МЕТОДИ
    /**
     * Створює повну копію цього екземпляра духовного кореня.
     *
     *
     * Копіюються всі дані, включаючи рівень, досвід, силу та додаткові теги.
     * Стан синхронізації (dirty) також копіюється для забезпечення коректної
     * роботи мережевої синхронізації.
     *
     *
     * **Скопійовані дані:**
     *
     *  * **Базові властивості:** рівень, досвід, сила
     *  * **NBT теги:** додаткові дані та налаштування
     *  * **Стан синхронізації:** dirty flag
     *  * **Посилання на реєстр:** зв'язок з типом кореня
     *
     *
     *
     * **Використання:**
     *
     *  * Створення резервних копій перед еволюцією
     *  * Тимчасові модифікації без впливу на оригінал
     *  * Передача стану між різними контекстами
     *  * Відновлення після помилок
     *
     *
     *
     * **Важливо:** Клон є повністю незалежним - зміни в ньому
     * не впливають на оригінальний екземпляр і навпаки.
     *
     * @return Незалежна копія екземпляра з усіма скопійованими даними
     *
     * @throws AssertionError якщо клонування не підтримується (не повинно траплятися)
     *
     * @see .equals
     * @see .hashCode
     */
    public override fun clone(): SpiritualRootInstance {
        try {
            val clone = super.clone() as SpiritualRootInstance
            clone.dirty = this.dirty
            if (this.tag != null) clone.tag = this.tag!!.copy()
            clone.level = this.level
            clone.experience = this.experience
            return clone
        } catch (e: CloneNotSupportedException) {
            throw AssertionError("Клонування повинно бути підтримане", e)
        }
    }

    /**
     * Перевіряє рівність цього екземпляра з іншим об'єктом.
     *
     *
     * Два екземпляри вважаються рівними, якщо вони посилаються на
     * той же тип духовного кореня в тому ж реєстрі. Рівень, досвід та інші
     * змінні властивості не враховуються при порівнянні.
     *
     *
     * **Критерії рівності:**
     *
     *  * **Ідентичність посилань:** `this == o`
     *  * **Тип об'єкта:** Обидва мають бути `SpiritualRootInstance`
     *  * **ID духовного кореня:** Однаковий ідентифікатор
     *  * **Ключ реєстру:** Той самий реєстр духовних коренів
     *
     *
     *
     * **Не враховується при порівнянні:**
     *
     *  * Поточний рівень розвитку
     *  * Накопичений досвід
     *  * NBT теги з додатковими даними
     *  * Стан синхронізації (dirty flag)
     *
     *
     *
     * Цей підхід дозволяє ідентифікувати екземпляри одного типу
     * незалежно від їх поточного стану розвитку.
     *
     * @param o Об'єкт для порівняння (може бути null)
     * @return `true`, якщо об'єкти представляють той самий тип духовного кореня
     *
     * @see .hashCode
     * @see Object.equals
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val instance = o as SpiritualRootInstance
        return this.spiritualRootId == instance.spiritualRootId &&
                spiritualRootRegistrySupplier.getRegistryKey() == instance.spiritualRootRegistrySupplier.getRegistryKey()
    }

    /**
     * Обчислює хеш-код для цього екземпляра.
     *
     *
     * Хеш-код базується на ідентифікаторі духовного кореня та ключі реєстру.
     *
     *
     * @return Хеш-код екземпляра
     */
    override fun hashCode(): Int {
        return Objects.hash(this.spiritualRootId, spiritualRootRegistrySupplier.getRegistryKey())
    } //endregion

    companion object {
        //region NBT Serialization Constants
        /** NBT key for storing the main spiritual root identifier  */
        const val KEY: String = "spiritual_root"

        /** NBT key for storing the root development level  */
        const val LEVEL_KEY: String = "level"

        /** NBT key for storing the accumulated experience  */
        const val EXPERIENCE_KEY: String = "experience"

        /** NBT key for storing the root strength  */
        const val STRENGTH_KEY: String = "strength"

        /** NBT key for storing the root purity  */
        const val PURITY_KEY: String = "Purity"

        /**
         * Creates a spiritual root instance from NBT data.
         *
         *
         * This method is used to restore saved spiritual roots from world files
         * or when transferring data between server and client.
         *
         *
         *
         * The NBT tag must contain all necessary data created by the [.toNBT] method.
         *
         *
         *
         * **Example Usage:**
         *
         * <pre>`// Creating NBT data for saving
         * CompoundTag nbt = new CompoundTag();
         * nbt.putString("spiritual_root", "eternalcore:fire_root");
         * nbt.putInt("level", 3);
         * nbt.putFloat("experience", 250.5f);
         * nbt.putFloat("strength", 0.8f);
         * nbt.putFloat("Purity", 0.6f);
         *
         * // Restoring instance from NBT
         * try {
         * SpiritualRootInstance restored = SpiritualRootInstance.fromNBT(nbt);
         * System.out.println("Restored root: " + restored.getDisplayName().getString());
         * System.out.println("Level: " + restored.getLevel());
         * System.out.println("Experience: " + restored.getExperience());
         * } catch (NullPointerException e) {
         * System.err.println("Could not find root in registry: " + e.getMessage());
         * }
         *
         * // Restoring from world file
         * CompoundTag worldData = player.getPersistentData().getCompound("spiritual_roots");
         * if (worldData.contains("primary_root")) {
         * SpiritualRootInstance primaryRoot = SpiritualRootInstance.fromNBT(
         * worldData.getCompound("primary_root")
         * );
         * }
        `</pre> *
         *
         * @param tag NBT tag with serialized instance data
         * @return Restored spiritual root instance
         * @throws NullPointerException if no spiritual root found in registry with the specified identifier
         * @see .toNBT
         */
        @Throws(NullPointerException::class)
        fun fromNBT(tag: CompoundTag?): SpiritualRootInstance {
            val location = ResourceLocation.tryParse(tag!!.getString(KEY))
            val spiritualRoot = SpiritualRootAPI.spiritualRootRegistry!!.get(location)
            if (spiritualRoot == null) {
                throw NullPointerException("No spiritualRoot found for location: " + location)
            }
            val instance = spiritualRoot.createDefaultInstance()
            instance.deserialize(tag)
            return instance
        }
    }
}