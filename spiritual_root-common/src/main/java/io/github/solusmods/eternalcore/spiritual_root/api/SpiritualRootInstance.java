package io.github.solusmods.eternalcore.spiritual_root.api;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.element.api.Element;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Екземпляр Духовного Кореня, що представляє конкретну реалізацію {@link SpiritualRoot} для сутності.
 * <p>
 * Цей клас є контейнером для збереження стану розвитку конкретного Духовного Кореня у сутності.
 * Він містить всю інформацію про поточний прогрес культивації, включаючи:
 * <ul>
 *     <li>Рівень розвитку кореня (від I до X)</li>
 *     <li>Накопичений досвід культивації</li>
 *     <li>Силу (чистоту) кореня (0.0 - 1.0)</li>
 *     <li>Статус активності для доступу до технік</li>
 *     <li>Додаткові дані для розширення функціональності</li>
 * </ul>
 * </p>
 * <p>
 * Кожен екземпляр прив'язаний до конкретного типу {@link SpiritualRoot} через реєстр,
 * що забезпечує цілісність даних при серіалізації/десеріалізації.
 * </p>
 * <p>
 * Система відстеження змін (dirty tracking) забезпечує ефективну синхронізацію
 * між сервером та клієнтом тільки при необхідності.
 * </p>
 *
 * @author EternalCore Team
 * @version 1.0.4.5
 * @since 1.0
 * @see SpiritualRoot
 * @see RootLevels
 */
@Getter
@Setter
public class SpiritualRootInstance implements Cloneable {

    //region NBT Serialization Constants

    /** NBT key for storing the main spiritual root identifier */
    public static final String KEY = "spiritual_root";

    /** NBT key for storing the root development level */
    public static final String LEVEL_KEY = "level";

    /** NBT key for storing the accumulated experience */
    public static final String EXPERIENCE_KEY = "experience";

    /** NBT key for storing the root strength */
    public static final String STRENGTH_KEY = "strength";

    /** NBT key for storing the root purity */
    public static final String PURITY_KEY = "Purity";

    //endregion

    //region Core Fields

    /**
     * Registry supplier for obtaining the spiritual root type.
     * <p>
     * Using RegistrySupplier ensures lazy loading and reference stability
     * during mod reloading.
     * </p>
     */
    protected final RegistrySupplier<SpiritualRoot> spiritualRootRegistrySupplier;

    /**
     * Strength (purity) of the spiritual root from 0.0 to 1.0.
     * <p>
     * Higher strength means:
     * <ul>
     *     <li>More efficient cultivation of corresponding element</li>
     *     <li>More powerful techniques and abilities</li>
     *     <li>Better affinity with elemental energy</li>
     *     <li>Faster development progress</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Setting strength affects cultivation efficiency
     * instance.setStrength(0.8f); // 80% strength
     *
     * // Calculate experience bonus based on strength
     * float baseExp = 100.0f;
     * float bonus = baseExp * instance.getStrength() * 0.5f;
     * instance.setExperience(instance.getExperience() + baseExp + bonus);
     * }</pre>
     */
    private float strength = 0.0F;

    /**
     * Current development level of the spiritual root.
     * <p>
     * Levels progress from I to X, with each subsequent level requiring
     * exponentially more experience and unlocking new capabilities.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Check if player can use advanced techniques
     * if (instance.getLevel().getLevel() >= RootLevels.V.getLevel()) {
     *     // Unlock powerful techniques at level V
     *     player.sendSystemMessage(Component.literal("Advanced techniques unlocked!"));
     * }
     *
     * // Level-based damage calculation
     * float damage = 10.0f * (1 + instance.getLevel().getLevel() * 0.2f);
     * }</pre>
     */
    private RootLevels level = RootLevels.I;

    /**
     * Accumulated cultivation experience.
     * <p>
     * Experience is gained through meditation, technique usage, absorbing elemental
     * resources, and other cultivation activities. Automatic advancement occurs
     * when reaching threshold values.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Add experience and check for level up
     * float expGain = 25.0f;
     * instance.setExperience(instance.getExperience() + expGain);
     * instance.updateLevel(player);
     *
     * // Calculate progress to next level
     * float currentExp = instance.getExperience();
     * float requiredExp = instance.getLevel().getExperience();
     * float progress = currentExp / requiredExp;
     * }</pre>
     */
    private float experience = 0.0F;

    /**
     * Whether techniques of this spiritual root are accessible.
     * <p>
     * Only active roots allow the use of corresponding techniques and provide
     * attribute bonuses. An entity may have multiple roots, but not all of
     * them are necessarily active simultaneously.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Activate root and apply bonuses
     * if (!instance.isActive()) {
     *     instance.setActive(true);
     *     instance.addAttributeModifiers(player);
     *     player.sendSystemMessage(Component.literal("Spiritual root activated!"));
     * }
     *
     * // Check if techniques are available
     * if (instance.isActive()) {
     *     // Allow technique usage
     *     executeFireballTechnique(player, instance);
     * }
     * }</pre>
     */
    private boolean active = false;

    /**
     * Purity of the spiritual root - quality indicator from 0.0 to 1.0.
     * <p>
     * High purity increases cultivation efficiency, breakthrough chances,
     * and reduces negative effects during cultivation.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Purity affects breakthrough success rate
     * float baseChance = 0.3f; // 30% base chance
     * float purityBonus = instance.getPurity() * 0.4f; // Up to 40% bonus
     * float finalChance = Math.min(baseChance + purityBonus, 0.9f);
     *
     * if (random.nextFloat() < finalChance) {
     *     // Successful breakthrough
     *     instance.setLevel(instance.getLevel().getNext());
     * }
     * }</pre>
     */
    private float purity;

    /**
     * Additional data for functionality extension.
     * <p>
     * This tag can be used by subclasses or addons to store specific
     * information not covered by the base fields.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Store custom awakening data
     * CompoundTag customData = new CompoundTag();
     * customData.putString("awakening_location", "ancient_temple");
     * customData.putLong("awakening_time", System.currentTimeMillis());
     * customData.putBoolean("blessed_by_master", true);
     * instance.setTag(customData);
     *
     * // Retrieve custom data
     * CompoundTag tag = instance.getTag();
     * if (tag != null && tag.contains("blessed_by_master")) {
     *     boolean blessed = tag.getBoolean("blessed_by_master");
     *     if (blessed) {
     *         // Apply special blessing effects
     *     }
     * }
     * }</pre>
     */
    @Nullable
    private CompoundTag tag = null;

    /**
     * Flag indicating the need for synchronization with the client.
     * <p>
     * Automatically set when important instance data changes and reset
     * after successful synchronization.
     * </p>
     */
    @Getter
    private boolean dirty = false;

    //endregion

    //region Constructors and Creation

    /**
     * Creates a new spiritual root instance for the specified type.
     * <p>
     * The constructor initializes the instance with base values:
     * <ul>
     *     <li>Level I (initial)</li>
     *     <li>Zero experience</li>
     *     <li>Zero strength</li>
     *     <li>Inactive state</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRoot waterRoot = SpiritualRootAPI.getSpiritualRootRegistry()
     *     .get(new ResourceLocation("eternalcore", "water_root"));
     * SpiritualRootInstance instance = new SpiritualRootInstance(waterRoot);
     *
     * // Instance will have:
     * // - level = RootLevels.I
     * // - experience = 0.0f
     * // - strength = 0.0f
     * // - active = false
     * }</pre>
     *
     * @param spiritualRoot The spiritual root type for creating the instance
     */
    protected SpiritualRootInstance(SpiritualRoot spiritualRoot) {
        this.spiritualRootRegistrySupplier = SpiritualRootAPI.getSpiritualRootRegistry()
                .delegate(SpiritualRootAPI.getSpiritualRootRegistry().getId(spiritualRoot));
    }

    /**
     * Creates a spiritual root instance from NBT data.
     * <p>
     * This method is used to restore saved spiritual roots from world files
     * or when transferring data between server and client.
     * </p>
     * <p>
     * The NBT tag must contain all necessary data created by the {@link #toNBT()} method.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Creating NBT data for saving
     * CompoundTag nbt = new CompoundTag();
     * nbt.putString("spiritual_root", "eternalcore:fire_root");
     * nbt.putInt("level", 3);
     * nbt.putFloat("experience", 250.5f);
     * nbt.putFloat("strength", 0.8f);
     * nbt.putFloat("Purity", 0.6f);
     *
     * // Restoring instance from NBT
     * try {
     *     SpiritualRootInstance restored = SpiritualRootInstance.fromNBT(nbt);
     *     System.out.println("Restored root: " + restored.getDisplayName().getString());
     *     System.out.println("Level: " + restored.getLevel());
     *     System.out.println("Experience: " + restored.getExperience());
     * } catch (NullPointerException e) {
     *     System.err.println("Could not find root in registry: " + e.getMessage());
     * }
     *
     * // Restoring from world file
     * CompoundTag worldData = player.getPersistentData().getCompound("spiritual_roots");
     * if (worldData.contains("primary_root")) {
     *     SpiritualRootInstance primaryRoot = SpiritualRootInstance.fromNBT(
     *         worldData.getCompound("primary_root")
     *     );
     * }
     * }</pre>
     *
     * @param tag NBT tag with serialized instance data
     * @return Restored spiritual root instance
     * @throws NullPointerException if no spiritual root found in registry with the specified identifier
     * @see #toNBT()
     */
    public static SpiritualRootInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(KEY));
        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(location);
        if (spiritualRoot == null) {
            throw new NullPointerException("No spiritualRoot found for location: " + location);
        }
        SpiritualRootInstance instance = spiritualRoot.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    //endregion

    //region Basic Information Access Methods

    /**
     * Gets the spiritual root type for this instance.
     * <p>
     * Returns the base {@link SpiritualRoot} that defines the behavior
     * and characteristics of this instance.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... get instance
     * SpiritualRoot rootType = instance.getSpiritualRoot();
     *
     * // Using type to check characteristics
     * RootLevels maxLevel = rootType.getMaxLevel();
     * RootType type = rootType.getType();
     *
     * // Check special properties
     * if (rootType.canAdvance(instance, player)) {
     *     System.out.println("Root can advance to next level");
     * }
     * }</pre>
     *
     * @return The spiritual root type
     */
    public SpiritualRoot getSpiritualRoot() {
        return spiritualRootRegistrySupplier.get();
    }

    /**
     * Gets the identifier of the spiritual root type.
     * <p>
     * The identifier is used for serialization, registration,
     * and identification of the root type in the system.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... get instance
     * ResourceLocation id = instance.getSpiritualRootId();
     *
     * // Using for type comparison
     * ResourceLocation fireRootId = new ResourceLocation("eternalcore", "fire_root");
     * if (id.equals(fireRootId)) {
     *     System.out.println("This is a fire spiritual root");
     * }
     *
     * // Using in configuration
     * config.putString("player_root_type", id.toString());
     *
     * // Logging for diagnostics
     * logger.info("Activated root: {}", id);
     * }</pre>
     *
     * @return ResourceLocation identifier of the spiritual root type
     */
    public ResourceLocation getSpiritualRootId() {
        return this.spiritualRootRegistrySupplier.getId();
    }

    /**
     * Gets the localized name of the spiritual root for display.
     * <p>
     * The name is automatically localized according to the client's language
     * and can be used in the user interface.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... get instance
     * MutableComponent displayName = instance.getDisplayName();
     *
     * // Display in chat
     * player.sendSystemMessage(Component.literal("Your spiritual root: ")
     *     .append(displayName.withStyle(ChatFormatting.GOLD)));
     *
     * // Using in GUI
     * tooltip.add(Component.literal("Type: ").append(displayName));
     *
     * // Creating complex message
     * MutableComponent message = Component.literal("Root ")
     *     .append(displayName.withStyle(ChatFormatting.AQUA))
     *     .append(" reached level ")
     *     .append(Component.literal(String.valueOf(instance.getLevel().getLevel()))
     *         .withStyle(ChatFormatting.YELLOW));
     * }</pre>
     *
     * @return Localized component with the spiritual root name
     */
    public MutableComponent getDisplayName() {
        return this.getSpiritualRoot().getName();
    }

    /**
     * Gets the type (category) of this spiritual root.
     * <p>
     * The type determines rarity, potential, and special characteristics of the root.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... get instance
     * RootType type = instance.getType();
     *
     * // Setting bonuses based on type
     * float experienceMultiplier = switch (type) {
     *     case COMMON -> 1.0f;
     *     case RARE -> 1.2f;
     *     case EPIC -> 1.5f;
     *     case LEGENDARY -> 2.0f;
     *     case MYTHIC -> 3.0f;
     * };
     *
     * // Determining display color
     * ChatFormatting color = switch (type) {
     *     case COMMON -> ChatFormatting.WHITE;
     *     case RARE -> ChatFormatting.BLUE;
     *     case EPIC -> ChatFormatting.DARK_PURPLE;
     *     case LEGENDARY -> ChatFormatting.GOLD;
     *     case MYTHIC -> ChatFormatting.DARK_RED;
     * };
     *
     * // Check evolution availability
     * if (type.ordinal() >= RootType.EPIC.ordinal()) {
     *     // Only epic and higher roots can evolve
     *     SpiritualRoot evolved = instance.getFirstDegree(player);
     * }
     * }</pre>
     *
     * @return The spiritual root type
     * @see RootType
     */
    public RootType getType() {
        return this.getSpiritualRoot().getType();
    }

    /**
     * Checks if this spiritual root belongs to the specified tag.
     * <p>
     * Tags are used to group spiritual roots by certain criteria,
     * such as element, rarity, or origin.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Define tags
     * TagKey<SpiritualRoot> fireRoots = TagKey.create(
     *     SpiritualRootAPI.getSpiritualRootRegistry().key(),
     *     new ResourceLocation("eternalcore", "fire_element")
     * );
     * TagKey<SpiritualRoot> rareRoots = TagKey.create(
     *     SpiritualRootAPI.getSpiritualRootRegistry().key(),
     *     new ResourceLocation("eternalcore", "rare_roots")
     * );
     *
     * SpiritualRootInstance instance = // ... get instance
     *
     * // Check tag membership
     * if (instance.is(fireRoots)) {
     *     // Apply fire effects
     *     player.setSecondsOnFire(0); // Fire immunity
     *
     *     // Bonuses in Nether
     *     if (player.level().dimension() == Level.NETHER) {
     *         instance.setExperience(instance.getExperience() + 10.0f);
     *     }
     * }
     *
     * if (instance.is(rareRoots)) {
     *     // Special effects for rare roots
     *     player.addEffect(new MobEffectInstance(MobEffects.LUCK, 1200, 0));
     * }
     *
     * // Check root compatibility
     * TagKey<SpiritualRoot> conflictingRoots = TagKey.create(
     *     SpiritualRootAPI.getSpiritualRootRegistry().key(),
     *     new ResourceLocation("eternalcore", "water_element")
     * );
     *
     * if (instance.is(fireRoots)) {
     *     // Check other player roots for conflicts
     *     for (SpiritualRootInstance other : playerRoots) {
     *         if (other.is(conflictingRoots)) {
     *             // Conflict between fire and water
     *             applyConflictPenalty(player);
     *         }
     *     }
     * }
     * }</pre>
     *
     * @param tag Tag to check
     * @return true if the root belongs to the tag
     */
    public boolean is(TagKey<SpiritualRoot> tag) {
        return this.spiritualRootRegistrySupplier.is(tag);
    }

    //endregion

    //region Serialization Methods

    /**
     * Serializes the spiritual root instance to NBT format.
     * <p>
     * This method ensures saving of all necessary information
     * for complete restoration of the instance state. Includes base data
     * and calls {@link #serialize(CompoundTag)} for additional data.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... configured instance
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
     * }</pre>
     *
     * @return CompoundTag with all instance data
     * @see #fromNBT(CompoundTag)
     * @see #serialize(CompoundTag)
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(KEY, this.getSpiritualRootId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Serializes specific instance data to NBT tag.
     * <p>
     * This method can be overridden by subclasses to save additional data.
     * The base implementation saves:
     * <ul>
     *     <li>Development level</li>
     *     <li>Accumulated experience</li>
     *     <li>Root strength</li>
     *     <li>Root purity</li>
     *     <li>Additional tags (if any)</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Example of overriding in subclass:</strong>
     * </p>
     * <pre>{@code
     * public class AdvancedSpiritualRootInstance extends SpiritualRootInstance {
     *     private String specialAbility;
     *     private int meditationTime;
     *     private List<String> unlockedTechniques = new ArrayList<>();
     *
     *     @Override
     *     public CompoundTag serialize(CompoundTag nbt) {
     *         // Call base serialization
     *         super.serialize(nbt);
     *
     *         // Add special data
     *         if (specialAbility != null) {
     *             nbt.putString("special_ability", specialAbility);
     *         }
     *         nbt.putInt("meditation_time", meditationTime);
     *
     *         // Serialize technique list
     *         if (!unlockedTechniques.isEmpty()) {
     *             ListTag techniquesList = new ListTag();
     *             for (String technique : unlockedTechniques) {
     *                 techniquesList.add(StringTag.valueOf(technique));
     *             }
     *             nbt.put("unlocked_techniques", techniquesList);
     *         }
     *
     *         return nbt;
     *     }
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
     * }</pre>
     *
     * @param nbt NBT tag for saving data
     * @return The same NBT tag with added data
     * @see #deserialize(CompoundTag)
     */
    public CompoundTag serialize(CompoundTag nbt) {
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        nbt.putInt(LEVEL_KEY, this.level.getLevel());
        nbt.putFloat(EXPERIENCE_KEY, this.experience);
        nbt.putFloat(STRENGTH_KEY, this.strength);
        nbt.putFloat(PURITY_KEY, purity);
        return nbt;
    }

    /**
     * Deserializes instance data from NBT tag.
     * <p>
     * Restores instance state from saved data. The method safely handles
     * missing fields by using default values.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Create NBT with saved data
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
     * }</pre>
     *
     * @param tag NBT tag with saved data
     * @see #serialize(CompoundTag)
     */
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        if (tag.contains(LEVEL_KEY)) this.level = RootLevels.byId(tag.getInt(LEVEL_KEY));
        if (tag.contains(EXPERIENCE_KEY)) this.experience = tag.getFloat(EXPERIENCE_KEY);
        if (tag.contains(PURITY_KEY)) this.purity = tag.getFloat(PURITY_KEY);
        if (tag.contains(STRENGTH_KEY)) this.strength = tag.getFloat(STRENGTH_KEY);
    }

    //endregion

    //region State Management Methods

    /**
     * Marks the instance as changed for synchronization with the client.
     * <p>
     * This method is automatically called when important parameters change
     * and signals the system about the need for data synchronization.
     * </p>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Manual marking (usually not needed as setters do this automatically)
     * instance.markDirty();
     *
     * // Check if synchronization is needed
     * if (instance.isDirty()) {
     *     // Send update packet to client
     *     sendSyncPacket(player, instance);
     *     instance.resetDirty(); // Reset after sync
     * }
     *
     * // Custom modification that requires manual dirty marking
     * CompoundTag customTag = instance.getTag();
     * if (customTag == null) {
     *     customTag = new CompoundTag();
     *     instance.setTag(customTag);
     * }
     * customTag.putString("last_meditation", "temple_ruins");
     * instance.markDirty(); // Manual marking required
     * }</pre>
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * Скидає прапорець синхронізації після успішної передачі даних клієнту.
     * <p>
     * <strong>УВАГА:</strong> Цей метод призначений тільки для внутрішнього
     * використання системою синхронізації. Не викликайте його самостійно!
     * </p>
     */
    @ApiStatus.Internal
    public void resetDirty() {
        this.dirty = false;
    }

    /**
     * Встановлює рівень розвитку духовного кореня.
     * <p>
     * Автоматично позначає екземпляр як змінений для синхронізації.
     * </p>
     *
     * @param level Новий рівень розвитку
     */
    public void setLevel(RootLevels level) {
        this.level = level;
        markDirty();
    }

    /**
     * Встановлює накопичений досвід культивації.
     * <p>
     * Автоматично позначає екземпляр як змінений для синхронізації.
     * </p>
     *
     * @param experience Нове значення досвіду
     */
    public void setExperience(float experience) {
        this.experience = experience;
        markDirty();
    }

    /**
     * Встановлює силу (чистоту) духовного кореня.
     * <p>
     * Автоматично позначає екземпляр як змінений для синхронізації.
     * Значення обмежується діапазоном 0.0 - 1.0.
     * </p>
     *
     * @param strength Нове значення сили (0.0 - 1.0)
     */
    public void setStrength(float strength) {
        this.strength = strength;
        markDirty();
    }

    public void setPurity(float purity) {
        this.purity = purity;
        markDirty();
    }
    //endregion

    //region Progression and Development Methods

    /**
     * Updates the spiritual root level based on accumulated experience.
     * <p>
     * Automatically advances the root to subsequent levels if sufficient
     * experience has been accumulated. The process continues until reaching
     * the maximum level or exhausting experience points.
     * </p>
     * <p>
     * With each advancement, the {@link #onAdvance(LivingEntity)} method
     * is called to activate corresponding effects and events.
     * </p>
     * <p>
     * <strong>Level Progression Logic:</strong>
     * </p>
     * <ul>
     *     <li>Checks if current experience meets or exceeds level requirement</li>
     *     <li>Advances to next level if conditions are met</li>
     *     <li>Triggers advancement effects and notifications</li>
     *     <li>Continues until max level or insufficient experience</li>
     *     <li>Automatically marks instance as dirty for synchronization</li>
     * </ul>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * // Standard level update after gaining experience
     * SpiritualRootInstance instance = // ... get instance
     * instance.setExperience(instance.getExperience() + 150.0f);
     * instance.updateLevel(player);
     *
     * // Check if level changed
     * RootLevels previousLevel = RootLevels.III;
     * if (instance.getLevel().getLevel() > previousLevel.getLevel()) {
     *     player.sendSystemMessage(Component.literal("Spiritual root advanced to level ")
     *         .append(Component.literal(instance.getLevel().name())
     *             .withStyle(ChatFormatting.GOLD)));
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
     *     int levelsGained = endLevel.getLevel() - startLevel.getLevel();
     *     player.displayClientMessage(Component.literal("Advanced " + levelsGained + " levels!"), true);
     *
     *     // Apply level-based rewards
     *     player.giveExperiencePoints(levelsGained * 10);
     *
     *     // Special effects for major milestones
     *     if (endLevel == RootLevels.V) {
     *         player.level().playSound(null, player.blockPosition(),
     *             SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 2.0f);
     *     }
     * }
     * }</pre>
     * <p>
     * <strong>Performance Notes:</strong>
     * </p>
     * <ul>
     *     <li>Uses while loop to handle multiple level advances in single call</li>
     *     <li>Efficiently processes large experience gains</li>
     *     <li>Stops automatically at maximum level to prevent infinite loops</li>
     *     <li>Triggers dirty marking for network synchronization</li>
     * </ul>
     *
     * @param entity The entity whose spiritual root is developing
     * @see #onAdvance(LivingEntity)
     * @see #canAdvance(LivingEntity)
     * @see RootLevels#getExperience()
     */
    public void updateLevel(LivingEntity entity) {
        while (experience >= level.getExperience() && level != getSpiritualRoot().getMaxLevel()) {
            level = level.getNext();
            onAdvance(entity);
        }
    }

    /**
     * Checks whether the spiritual root can advance to the next level.
     * <p>
     * Advancement conditions may include sufficient experience, availability of resources,
     * suitable environment, or completion of special tasks. The actual logic is delegated
     * to the specific {@link SpiritualRoot} implementation, allowing for custom advancement
     * requirements per root type.
     * </p>
     * <p>
     * <strong>Common Advancement Requirements:</strong>
     * </p>
     * <ul>
     *     <li><strong>Experience:</strong> Sufficient cultivation experience accumulated</li>
     *     <li><strong>Resources:</strong> Special cultivation materials or elixirs</li>
     *     <li><strong>Environment:</strong> Specific locations with high spiritual energy</li>
     *     <li><strong>Quests:</strong> Completion of trials or spiritual tests</li>
     *     <li><strong>Compatibility:</strong> No conflicting spiritual roots active</li>
     *     <li><strong>Time:</strong> Minimum cultivation time requirements</li>
     *     <li><strong>Mentorship:</strong> Guidance from advanced cultivators</li>
     * </ul>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... get instance
     * Player player = // ... get player
     *
     * // Basic advancement check
     * if (instance.canAdvance(player)) {
     *     player.sendSystemMessage(Component.literal("Your spiritual root can advance!")
     *         .withStyle(ChatFormatting.GREEN));
     *
     *     // Show advancement requirements
     *     RootLevels nextLevel = instance.getLevel().getNext();
     *     float requiredExp = nextLevel.getExperience();
     *     float currentExp = instance.getExperience();
     *
     *     if (currentExp >= requiredExp) {
     *         player.sendSystemMessage(Component.literal("Requirements met. Advancing...")
     *             .withStyle(ChatFormatting.GOLD));
     *         instance.updateLevel(player);
     *     }
     * } else {
     *     // Show why advancement is blocked
     *     player.sendSystemMessage(Component.literal("Advancement requirements not met")
     *         .withStyle(ChatFormatting.RED));
     * }
     *
     * // Pre-advancement validation in GUI
     * boolean canAdvance = instance.canAdvance(player);
     * Button advanceButton = Button.builder(Component.literal("Advance"))
     *     .onPress(button -> {
     *         if (instance.canAdvance(player)) {
     *             instance.updateLevel(player);
     *         }
     *     })
     *     .build();
     * advanceButton.active = canAdvance;
     *
     * // Check advancement prerequisites
     * if (instance.getLevel() == RootLevels.IV && instance.canAdvance(player)) {
     *     // Special handling for major breakthrough
     *     if (hasBreakthroughPill(player) && isInCultivationChamber(player)) {
     *         // Allow advancement with special conditions
     *         consumeBreakthroughPill(player);
     *         instance.updateLevel(player);
     *         player.sendSystemMessage(Component.literal("Major breakthrough achieved!")
     *             .withStyle(ChatFormatting.LIGHT_PURPLE));
     *     } else {
     *         player.sendSystemMessage(Component.literal("Major breakthrough requires:")
     *             .append("\n- Breakthrough Pill")
     *             .append("\n- Cultivation Chamber"));
     *     }
     * }
     *
     * // Scheduled advancement check
     * if (player.tickCount % 200 == 0) { // Every 10 seconds
     *     if (instance.canAdvance(player) && instance.getExperience() >= instance.getLevel().getExperience()) {
     *         // Auto-advance if conditions met
     *         instance.updateLevel(player);
     *     }
     * }
     * }</pre>
     * <p>
     * <strong>Implementation Notes:</strong>
     * </p>
     * <ul>
     *     <li>Delegates to {@link SpiritualRoot#canAdvance(SpiritualRootInstance, LivingEntity)}</li>
     *     <li>Allows for root-specific advancement logic</li>
     *     <li>Should be checked before calling {@link #updateLevel(LivingEntity)}</li>
     *     <li>May perform expensive calculations, cache results when possible</li>
     * </ul>
     *
     * @param entity The entity wishing to advance the root
     * @return true if advancement is possible, false otherwise
     * @see SpiritualRoot#canAdvance(SpiritualRootInstance, LivingEntity)
     * @see #updateLevel(LivingEntity)
     * @see RootLevels#getNext()
     */
    public boolean canAdvance(LivingEntity entity) {
        return this.getSpiritualRoot().canAdvance(this, entity);
    }

    /**
     * Increases the strength of the spiritual root by the specified amount.
     * <p>
     * Delegates the call to the base spiritual root type to apply
     * specific strength enhancement logic. Root strength affects cultivation
     * efficiency, technique power, and elemental affinity.
     * </p>
     * <p>
     * <strong>Strength Enhancement Effects:</strong>
     * </p>
     * <ul>
     *     <li><strong>Cultivation Speed:</strong> Higher strength increases experience gain rates</li>
     *     <li><strong>Technique Power:</strong> Amplifies damage and effectiveness of abilities</li>
     *     <li><strong>Elemental Affinity:</strong> Improves interaction with corresponding elements</li>
     *     <li><strong>Breakthrough Chance:</strong> Increases success rate for level advancement</li>
     *     <li><strong>Resource Efficiency:</strong> Reduces consumption of cultivation materials</li>
     *     <li><strong>Stability:</strong> Prevents cultivation deviation and accidents</li>
     * </ul>
     * <p>
     * <strong>Example Usage:</strong>
     * </p>
     * <pre>{@code
     * SpiritualRootInstance instance = // ... get instance
     * Player player = // ... get player
     *
     * // Basic strength increase
     * float strengthGain = 0.1f; // 10% increase
     * instance.increaseStrength(player, strengthGain);
     *
     * // Strength gain from elixirs
     * ItemStack elixir = // ... strength elixir item
     * if (elixir.getItem() instanceof StrengthElixirItem strengthElixir) {
     *     float bonus = strengthElixir.getStrengthBonus();
     *     instance.increaseStrength(player, bonus);
     *
     *     player.sendSystemMessage(Component.literal("Root strength increased by ")
     *         .append(Component.literal(String.format("%.1f%%", bonus * 100))
     *             .withStyle(ChatFormatting.GREEN)));
     * }
     *
     * // Meditation-based strength improvement
     * if (isMeditating(player)) {
     *     float meditationBonus = calculateMeditationStrengthGain(player, instance);
     *     if (meditationBonus > 0) {
     *         instance.increaseStrength(player, meditationBonus);
     *
     *         // Visual feedback
     *         spawnMeditationParticles(player);
     *         player.level().playSound(null, player.blockPosition(),
     *             SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.5f);
     *     }
     * }
     *
     * // Strength milestone rewards
     * float currentStrength = instance.getStrength();
     * instance.increaseStrength(player, 0.05f);
     * float newStrength = instance.getStrength();
     *
     * // Check for strength thresholds
     * if (currentStrength < 0.5f && newStrength >= 0.5f) {
     *     // Reached 50% strength milestone
     *     player.sendSystemMessage(Component.literal("Root purity milestone reached!")
     *         .withStyle(ChatFormatting.GOLD));
     *     player.giveExperiencePoints(50);
     * }
     *
     * if (currentStrength < 1.0f && newStrength >= 1.0f) {
     *     // Perfect strength achieved
     *     player.sendSystemMessage(Component.literal("Perfect root purity achieved!")
     *         .withStyle(ChatFormatting.LIGHT_PURPLE));
     *     player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
     * }
     *
     * // Strength-based technique unlocking
     * if (instance.getStrength() >= 0.8f) {
     *     // Unlock powerful techniques at 80% strength
     *     unlockAdvancedTechniques(player, instance);
     * }
     *
     * // Group cultivation strength sharing
     * if (isInCultivationGroup(player)) {
     *     List<Player> groupMembers = getCultivationGroupMembers(player);
     *     float sharedBonus = strengthGain * 0.1f; // 10% shared to group
     *
     *     for (Player member : groupMembers) {
     *         if (member != player) {
     *             SpiritualRootInstance memberRoot = getSpiritualRoot(member);
     *             if (memberRoot != null) {
     *                 memberRoot.increaseStrength(member, sharedBonus);
     *                 member.sendSystemMessage(Component.literal("Gained strength from group cultivation"));
     *             }
     *         }
     *     }
     * }
     * }</pre>
     * <p>
     * <strong>Balancing Considerations:</strong>
     * </p>
     * <ul>
     *     <li>Strength increases should be carefully balanced to prevent overpowering</li>
     *     <li>Consider diminishing returns for very high strength values</li>
     *     <li>Different root types may have different strength scaling</li>
     *     <li>Strength loss mechanisms may be needed for game balance</li>
     * </ul>
     *
     * @param living The entity whose root strength is being increased
     * @param amount The amount to increase strength by (typically 0.0-1.0 range)
     * @see SpiritualRoot#increaseStrength(SpiritualRootInstance, LivingEntity, float)
     * @see #getStrength()
     * @see #setStrength(float)
     */
    public void increaseStrength(LivingEntity living, float amount) {
        this.getSpiritualRoot().increaseStrength(this, living, amount);
    }

    //endregion

    //region МЕТОДИ ЕЛЕМЕНТІВ ТА ЕВОЛЮЦІЇ

    /**
     * Отримує елемент, пов'язаний з цим духовним коренем.
     *
     * <p>Елемент визначає тип елементальної енергії та впливає на
     * взаємодію з навколишнім середовищем і техніками. Кожен духовний
     * корінь може мати властивий йому елемент або бути нейтральним.</p>
     *
     * <p><b>Приклади елементів:</b></p>
     * <ul>
     *   <li>Вогонь - підвищує атаку та опір до вогню</li>
     *   <li>Вода - покращує регенерацію та захист</li>
     *   <li>Земля - збільшує міцність та стабільність</li>
     *   <li>Повітря - надає швидкість та спритність</li>
     * </ul>
     *
     * @param entity Сутність, для якої визначається елемент (не може бути null)
     * @return Елемент духовного кореня або null, якщо елемент не визначено
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see Element
     * @see SpiritualRoot#getElement(SpiritualRootInstance, LivingEntity)
     */
    public @Nullable Element getElement(@NonNull LivingEntity entity) {
        return this.getSpiritualRoot().getElement(this, entity);
    }

    /**
     * Отримує перший ступінь еволюції цього духовного кореня.
     *
     * <p>Еволюція дозволяє корению розвинутися в більш потужну форму
     * з покращеними характеристиками та новими здібностями. Перший ступінь
     * зазвичай досягається через накопичення досвіду та виконання певних умов.</p>
     *
     * <p><b>Умови еволюції можуть включати:</b></p>
     * <ul>
     *   <li>Досягнення певного рівня</li>
     *   <li>Накопичення достатньої кількості досвіду</li>
     *   <li>Виконання спеціальних завдань або ритуалів</li>
     *   <li>Отримання рідкісних матеріалів</li>
     * </ul>
     *
     * @param living Сутність, для якої визначається еволюція (не може бути null)
     * @return Еволюціонований духовний корінь першого ступеня або null,
     *         якщо еволюція недоступна або умови не виконані
     *
     * @throws IllegalArgumentException якщо living є null
     *
     * @see #getSecondDegree(LivingEntity)
     * @see SpiritualRoot#getFirstDegree(SpiritualRootInstance, LivingEntity)
     */
    @Nullable
    public SpiritualRoot getFirstDegree(@NonNull LivingEntity living) {
        return this.getSpiritualRoot().getFirstDegree(this, living);
    }

    /**
     * Отримує другий ступінь еволюції цього духовного кореня.
     *
     * <p>Другий ступінь представляє найвищий рівень еволюції з унікальними
     * здібностями та значно покращеними характеристиками. Це рідкісне досягнення,
     * яке потребує виключних зусиль та ресурсів від культиватора.</p>
     *
     * <p><b>Переваги другого ступеня:</b></p>
     * <ul>
     *   <li>Унікальні та потужні здібності</li>
     *   <li>Значно підвищені модифікатори атрибутів</li>
     *   <li>Ексклюзивні техніки культивації</li>
     *   <li>Престиж та визнання серед інших культиваторів</li>
     * </ul>
     *
     * @param living Сутність, для якої визначається еволюція другого ступеня (не може бути null)
     * @return Еволюціонований духовний корінь другого ступеня або null,
     *         якщо еволюція недоступна або не досягнуто необхідних умов
     *
     * @throws IllegalArgumentException якщо living є null
     *
     * @see #getFirstDegree(LivingEntity)
     * @see SpiritualRoot#getSecondDegree(SpiritualRootInstance, LivingEntity)
     */
    @Nullable
    public SpiritualRoot getSecondDegree(@NonNull LivingEntity living) {
        return this.getSpiritualRoot().getSecondDegree(this, living);
    }

    /**
     * Отримує попередній ступінь еволюції цього духовного кореня.
     *
     * <p>Дозволяє отримати посилання на попередню форму духовного кореня
     * в ланцюгу еволюції. Корисно для відстеження прогресу та можливості
     * регресії в разі необхідності.</p>
     *
     * <p><b>Використання:</b></p>
     * <ul>
     *   <li>Відстеження історії еволюції</li>
     *   <li>Механізми регресії або скидання</li>
     *   <li>Порівняння характеристик різних ступенів</li>
     * </ul>
     *
     * @param living Сутність, для якої визначається попередній ступінь (не може бути null)
     * @return Попередній ступінь духовного кореня або null, якщо це базова форма
     *
     * @throws IllegalArgumentException якщо living є null
     *
     * @see SpiritualRoot#getPreviousDegree(SpiritualRootInstance, LivingEntity)
     */
    @Nullable
    public SpiritualRoot getPreviousDegree(@NonNull LivingEntity living) {
        return this.getSpiritualRoot().getPreviousDegree(this, living);
    }

    /**
     * Отримує протилежний духовний корінь для поточного.
     *
     * <p>Протилежні корені мають конфліктуючі елементальні властивості
     * та можуть створювати проблеми при одночасному культивуванні. Знання
     * протилежного кореня важливе для розуміння потенційних конфліктів
     * та стратегічного планування розвитку.</p>
     *
     * <p><b>Приклади протилежностей:</b></p>
     * <ul>
     *   <li>Вогонь ↔ Вода</li>
     *   <li>Земля ↔ Повітря</li>
     *   <li>Світло ↔ Темрява</li>
     *   <li>Порядок ↔ Хаос</li>
     * </ul>
     *
     * <p><b>Наслідки конфлікту:</b></p>
     * <ul>
     *   <li>Зменшення ефективності культивації</li>
     *   <li>Ризик внутрішньої нестабільності</li>
     *   <li>Можливість бойових переваг проти носіїв протилежних коренів</li>
     * </ul>
     *
     * @param entity Сутність, для якої визначається протилежний корінь (не може бути null)
     * @return Протилежний духовний корінь або null, якщо протилежність не визначена
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see Element
     * @see SpiritualRoot#getOpposite(SpiritualRootInstance, LivingEntity)
     */
    public @Nullable SpiritualRoot getOpposite(@NonNull LivingEntity entity) {
        return getSpiritualRoot().getOpposite(this, entity);
    }

    //endregion

    //region МЕТОДИ УПРАВЛІННЯ АТРИБУТАМИ

    /**
     * Застосовує модифікатори атрибутів цього духовного кореня до сутності.
     *
     * <p>Викликається при активації кореня для надання бонусів до характеристик.
     * Модифікатори можуть включати збільшення сили, швидкості, здоров'я тощо.
     * Ефект залежить від типу духовного кореня та його поточного рівня розвитку.</p>
     *
     * <p><b>Типи модифікаторів:</b></p>
     * <ul>
     *   <li><strong>Адитивні</strong> - додають фіксоване значення</li>
     *   <li><strong>Мультиплікативні</strong> - множать на коефіцієнт</li>
     *   <li><strong>Процентні</strong> - додають відсоток від базового значення</li>
     * </ul>
     *
     * <p><b>Атрибути що можуть змінюватися:</b></p>
     * <ul>
     *   <li>Максимальне здоров'я</li>
     *   <li>Сила атаки</li>
     *   <li>Швидкість руху</li>
     *   <li>Броня та стійкість</li>
     *   <li>Регенерація</li>
     *   <li>Удача</li>
     * </ul>
     *
     * <p><b>Важливо:</b> Цей метод має бути збалансований з {@link #removeAttributeModifiers(LivingEntity)}
     * для правильного управління життєвим циклом модифікаторів.</p>
     *
     * @param entity Сутність, до якої застосовуються модифікатори (не може бути null)
     *
     * @throws IllegalArgumentException якщо entity є null
     * @throws IllegalStateException якщо духовний корінь не ініціалізований
     *
     * @see #removeAttributeModifiers(LivingEntity)
     * @see AttributeModifier
     * @see SpiritualRoot#addAttributeModifiers(SpiritualRootInstance, LivingEntity)
     */
    public void addAttributeModifiers(@NonNull LivingEntity entity) {
        this.getSpiritualRoot().addAttributeModifiers(this, entity);
    }

    /**
     * Видаляє модифікатори атрибутів цього духовного кореня від сутності.
     *
     * <p>Викликається при деактивації кореня або його заміні для видалення
     * раніше застосованих бонусів. Забезпечує чистоту стану атрибутів сутності
     * та запобігає накопиченню застарілих модифікаторів.</p>
     *
     * <p><b>Сценарії використання:</b></p>
     * <ul>
     *   <li>Деактивація духовного кореня</li>
     *   <li>Заміна одного кореня на інший</li>
     *   <li>Еволюція до наступного ступеня</li>
     *   <li>Тимчасове припинення ефектів</li>
     *   <li>Скидання системи культивації</li>
     * </ul>
     *
     * <p><b>Безпека операції:</b></p>
     * <ul>
     *   <li>Метод є ідемпотентним - безпечний для повторних викликів</li>
     *   <li>Не впливає на модифікатори від інших джерел</li>
     *   <li>Автоматично обробляє відсутні модифікатори</li>
     * </ul>
     *
     * @param entity Сутність, від якої видаляються модифікатори (не може бути null)
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see #addAttributeModifiers(LivingEntity)
     * @see AttributeModifier
     * @see SpiritualRoot#removeAttributeModifiers(SpiritualRootInstance, LivingEntity)
     */
    public void removeAttributeModifiers(@NonNull LivingEntity entity) {
        this.getSpiritualRoot().removeAttributeModifiers(this, entity);
    }

    //endregion

    //region МЕТОДИ ПОДІЙ ЖИТТЄВОГО ЦИКЛУ

    /**
     * Викликається при першому отриманні цього духовного кореня сутністю.
     *
     * <p>Дозволяє виконати ініціалізацію, надати початкові бонуси
     * або активувати спеціальні ефекти при отриманні кореня. Це ключова
     * подія в життєвому циклі духовного кореня, яка встановлює початковий стан.</p>
     *
     * <p><b>Типові дії при активації:</b></p>
     * <ul>
     *   <li>Застосування початкових модифікаторів атрибутів</li>
     *   <li>Розблокування базових здібностей</li>
     *   <li>Ініціалізація внутрішніх параметрів</li>
     *   <li>Відображення повідомлень гравцю</li>
     *   <li>Запуск візуальних та звукових ефектів</li>
     *   <li>Синхронізація стану з клієнтом</li>
     * </ul>
     *
     * <p><b>Послідовність викликів:</b></p>
     * <ol>
     *   <li>Створення екземпляра духовного кореня</li>
     *   <li>Виклик {@code onAdd()}</li>
     *   <li>Застосування початкових ефектів</li>
     *   <li>Синхронізація з клієнтом</li>
     * </ol>
     *
     * <p><b>Примітка:</b> Цей метод викликається лише один раз для кожного
     * екземпляра кореня. При еволюції створюється новий екземпляр.</p>
     *
     * @param living Сутність, яка отримала духовний корінь (не може бути null)
     *
     * @throws IllegalArgumentException якщо living є null
     * @throws IllegalStateException якщо корінь вже був активований
     *
     * @see #onAdvance(LivingEntity)
     * @see #addAttributeModifiers(LivingEntity)
     * @see SpiritualRoot#onAdd(SpiritualRootInstance, LivingEntity)
     */
    public void onAdd(@NonNull LivingEntity living) {
        this.getSpiritualRoot().onAdd(this, living);
    }

    /**
     * Викликається при просуванні духовного кореня на наступний рівень.
     *
     * <p>Дозволяє виконати дії, специфічні для просування: покращення
     * модифікаторів, розблокування здібностей, візуальні ефекти тощо.
     * Це важлива подія, яка відзначає прогрес культиватора.</p>
     *
     * <p><b>Дії при просуванні:</b></p>
     * <ul>
     *   <li><strong>Оновлення модифікаторів:</strong> Покращення існуючих бонусів</li>
     *   <li><strong>Нові здібності:</strong> Розблокування додаткових можливостей</li>
     *   <li><strong>Візуальні ефекти:</strong> Частинки, свічення, анімації</li>
     *   <li><strong>Звукові ефекти:</strong> Звуки досягнення та прогресу</li>
     *   <li><strong>Повідомлення:</strong> Інформування гравця про досягнення</li>
     *   <li><strong>Статистика:</strong> Оновлення досягнень та рекордів</li>
     * </ul>
     *
     * <p><b>Умови просування:</b></p>
     * <ul>
     *   <li>Накопичення достатньої кількості досвіду</li>
     *   <li>Досягнення порогових значень характеристик</li>
     *   <li>Виконання спеціальних завдань або ритуалів</li>
     *   <li>Отримання рідкісних матеріалів для просування</li>
     * </ul>
     *
     * <p><b>Примітка:</b> Метод викликається кожного разу при збільшенні рівня,
     * включаючи автоматичні просування через накопичення досвіду.</p>
     *
     * @param living Сутність, чий духовний корінь просувається (не може бути null)
     *
     * @throws IllegalArgumentException якщо living є null
     * @throws IllegalStateException якщо корінь не готовий до просування
     *
     * @see #onAdd(LivingEntity)
     * @see #onAddExperience(LivingEntity, float)
     * @see #getLevel()
     * @see SpiritualRoot#onAdvance(SpiritualRootInstance, LivingEntity)
     */
    public void onAdvance(@NonNull LivingEntity living) {
        this.getSpiritualRoot().onAdvance(this, living);
    }

    /**
     * Викликається при отриманні досвіду для цього духовного кореня.
     *
     * <p>Дозволяє додати спеціальну логіку обробки досвіду, такі як
     * бонусні ефекти або додаткові нарахування. Цей метод забезпечує
     * гнучкість в системі прогресу культивації.</p>
     *
     * <p><b>Можливості обробки досвіду:</b></p>
     * <ul>
     *   <li><strong>Бонусні множники:</strong> Збільшення досвіду за певних умов</li>
     *   <li><strong>Розподіл досвіду:</strong> Передача частини іншим аспектам</li>
     *   <li><strong>Спеціальні ефекти:</strong> Візуальні індикатори прогресу</li>
     *   <li><strong>Додаткові нарахування:</strong> Бонуси від елементів або подій</li>
     *   <li><strong>Обмеження:</strong> Контроль максимальної швидкості прогресу</li>
     * </ul>
     *
     * <p><b>Джерела досвіду:</b></p>
     * <ul>
     *   <li>Медитація та духовні практики</li>
     *   <li>Бої та перемоги над ворогами</li>
     *   <li>Споживання спеціальних предметів</li>
     *   <li>Взаємодія з елементальними джерелами</li>
     *   <li>Виконання квестів та завдань</li>
     * </ul>
     *
     * <p><b>Важлиᵽво:</b> Цей метод викликається до фактичного додавання досвіду,
     * що дозволяє модифікувати кількість або додати додаткові ефекти.</p>
     *
     * @param entity Сутність, яка отримує досвід культивації (не може бути null)
     *
     * @throws IllegalArgumentException якщо entity є null
     *
     * @see #getExperience()
     * @see #onAdvance(LivingEntity)
     * @see SpiritualRoot#onAddExperience(SpiritualRootInstance, LivingEntity, float)
     */
    public void onAddExperience(@NonNull LivingEntity entity, float amount) {
        this.getSpiritualRoot().onAddExperience(this, entity, amount);
    }

    //endregion

    //region СИСТЕМНІ МЕТОДИ

    /**
     * Створює повну копію цього екземпляра духовного кореня.
     *
     * <p>Копіюються всі дані, включаючи рівень, досвід, силу та додаткові теги.
     * Стан синхронізації (dirty) також копіюється для забезпечення коректної
     * роботи мережевої синхронізації.</p>
     *
     * <p><b>Скопійовані дані:</b></p>
     * <ul>
     *   <li><strong>Базові властивості:</strong> рівень, досвід, сила</li>
     *   <li><strong>NBT теги:</strong> додаткові дані та налаштування</li>
     *   <li><strong>Стан синхронізації:</strong> dirty flag</li>
     *   <li><strong>Посилання на реєстр:</strong> зв'язок з типом кореня</li>
     * </ul>
     *
     * <p><b>Використання:</b></p>
     * <ul>
     *   <li>Створення резервних копій перед еволюцією</li>
     *   <li>Тимчасові модифікації без впливу на оригінал</li>
     *   <li>Передача стану між різними контекстами</li>
     *   <li>Відновлення після помилок</li>
     * </ul>
     *
     * <p><b>Важливо:</b> Клон є повністю незалежним - зміни в ньому
     * не впливають на оригінальний екземпляр і навпаки.</p>
     *
     * @return Незалежна копія екземпляра з усіма скопійованими даними
     *
     * @throws AssertionError якщо клонування не підтримується (не повинно траплятися)
     *
     * @see #equals(Object)
     * @see #hashCode()
     */
    @Override
    public SpiritualRootInstance clone() {
        try {
            SpiritualRootInstance clone = (SpiritualRootInstance) super.clone();
            clone.dirty = this.dirty;
            if (this.tag != null) clone.tag = this.tag.copy();
            clone.level = this.level;
            clone.experience = this.experience;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Клонування повинно бути підтримане", e);
        }
    }

    /**
     * Перевіряє рівність цього екземпляра з іншим об'єктом.
     *
     * <p>Два екземпляри вважаються рівними, якщо вони посилаються на
     * той же тип духовного кореня в тому ж реєстрі. Рівень, досвід та інші
     * змінні властивості не враховуються при порівнянні.</p>
     *
     * <p><b>Критерії рівності:</b></p>
     * <ul>
     *   <li><strong>Ідентичність посилань:</strong> {@code this == o}</li>
     *   <li><strong>Тип об'єкта:</strong> Обидва мають бути {@code SpiritualRootInstance}</li>
     *   <li><strong>ID духовного кореня:</strong> Однаковий ідентифікатор</li>
     *   <li><strong>Ключ реєстру:</strong> Той самий реєстр духовних коренів</li>
     * </ul>
     *
     * <p><b>Не враховується при порівнянні:</b></p>
     * <ul>
     *   <li>Поточний рівень розвитку</li>
     *   <li>Накопичений досвід</li>
     *   <li>NBT теги з додатковими даними</li>
     *   <li>Стан синхронізації (dirty flag)</li>
     * </ul>
     *
     * <p>Цей підхід дозволяє ідентифікувати екземпляри одного типу
     * незалежно від їх поточного стану розвитку.</p>
     *
     * @param o Об'єкт для порівняння (може бути null)
     * @return {@code true}, якщо об'єкти представляють той самий тип духовного кореня
     *
     * @see #hashCode()
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpiritualRootInstance instance = (SpiritualRootInstance) o;
        return this.getSpiritualRootId().equals(instance.getSpiritualRootId()) &&
                spiritualRootRegistrySupplier.getRegistryKey().equals(instance.spiritualRootRegistrySupplier.getRegistryKey());
    }

    /**
     * Обчислює хеш-код для цього екземпляра.
     * <p>
     * Хеш-код базується на ідентифікаторі духовного кореня та ключі реєстру.
     * </p>
     *
     * @return Хеш-код екземпляра
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getSpiritualRootId(), spiritualRootRegistrySupplier.getRegistryKey());
    }

    //endregion
}