package io.github.solusmods.eternalcore.storage.api;

import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;

public abstract class Storage {
    protected final StorageHolder holder;
    /**
     * -- GETTER --
     * Used to check if the storage is dirty.
     */
    @Getter
    public boolean dirty = true;

    protected Storage(StorageHolder holder) {
        this.holder = holder;
    }


    /**
     * Used to save data to the entity.
     * Add all information to the given tag.
     *
     * @see Storage#load(CompoundTag)
     */
    public abstract void save(CompoundTag data);

    /**
     * Used to load data from the entity.
     * Read all information from the given tag.
     *
     * @see Storage#save(CompoundTag)
     */
    public abstract void load(CompoundTag data);

    /**
     * Used to create update packets.
     * Override this method to optimize the packet data.
     *
     * @see #loadUpdate(CompoundTag)
     */
    public void saveOutdated(CompoundTag data) {
        this.save(data);
    }

    /**
     * Used to apply update packets.
     *
     * @see #saveOutdated(CompoundTag)
     */
    public void loadUpdate(CompoundTag data) {
        this.load(data);
    }

    /**
     * Used to mark the storage as dirty.
     * This will cause the storage to be synchronized.
     */
    public void markDirty() {
        this.dirty = true;
        sync();
    }

    /**
     * Used to clear the dirty flag.
     */
    public void clearDirty() {
        this.dirty = false;
    }

    public  <T> void loadInstanceCollection(CompoundTag data, String collectionKey, Collection<T> collection,
                                            NBTDeserializer<T> deserializer) {
        if (collection == null) return;
        collection.clear();
        if (data.contains(collectionKey, Tag.TAG_COMPOUND)) {
            CompoundTag collectionTag = data.getCompound(collectionKey);

            for (String key : collectionTag.getAllKeys()) {
                try {
                    CompoundTag instanceTag = collectionTag.getCompound(key);
                    T instance = deserializer.fromNBT(instanceTag);
                    collection.add(instance);
                } catch (IllegalArgumentException e) {
                    EternalCoreStorage.LOG.error("Invalid UUID format in NBT: {}", key, e);
                }
            }
        }
    }


    public <I, V extends Number> void loadInstanceCollectionMap(CompoundTag data, String collectionKey, Map<I, V> map, String valueTag,
                                                 NBTDeserializer<I> deserializer,
                                                 BiFunction<CompoundTag, String, V> valueExtractor,
                                                 GetResourceLocation<I> location) {
        if (map == null) return;
        map.clear();
        if (data.contains(collectionKey, Tag.TAG_COMPOUND)) {
            CompoundTag collectionTag = data.getCompound(collectionKey);

            for (String key : collectionTag.getAllKeys()) {
                try {
                    CompoundTag entryTag = collectionTag.getCompound(key);
                    for (String instanceKey: entryTag.getAllKeys()){

                    }
                    I instanceKey = deserializer.fromNBT(entryTag.getCompound("instance"));
                    V instanceValue = valueExtractor.apply(entryTag, valueTag);
                    map.put(instanceKey, instanceValue);
                } catch (Exception e) {
                    EternalCoreStorage.LOG.error("Failed to load map entry in NBT for key '{}'", key, e);
                }
            }
        }
    }

    public <T> void loadSimple(CompoundTag data, String valueTag, T value, SimpleNBTDeserializer<T> deserializer){
        value = deserializer.fromNBT(data, valueTag);
    }

    public <I, V extends Number> void saveInstanceCollectionMap(CompoundTag data, String collectionKey, Map<I, V> map,
                                                                String valueTag,
                                                                NBTSerializer<I> serializer,
                                                                ToDoubleFunction<V> valueGetter,
                                                                GetResourceLocation<I> location){
        CompoundTag collectionTag = new CompoundTag();
        for (Map.Entry<I, V> entry: map.entrySet()){
            ResourceLocation id = location.getId(entry.getKey());
            CompoundTag instanceTag = serializer.toNBT(entry.getKey());
            collectionTag.put(id.toString(), instanceTag);
            collectionTag.putDouble(valueTag, valueGetter.applyAsDouble(entry.getValue()));
        }
        data.put(collectionKey, collectionTag);
    }


    public <T> void loadSingleInstance(CompoundTag data, String instanceKey, T instance, NBTDeserializer<T> deserializer){
        if (data.contains(instanceKey))
            instance = deserializer.fromNBT(data.getCompound(instanceKey));
    }

    public <T> void saveInstanceCollection(CompoundTag data, String collectionKey, Collection<T> collection,
                                           NBTSerializer<T> serializer, GetResourceLocation<T> location){
        CompoundTag collectionTag = new CompoundTag();
        for (T instance: collection){
            ResourceLocation id = location.getId(instance);
            CompoundTag instanceTag = serializer.toNBT(instance);
            collectionTag.put(id.toString(), instanceTag);
        }
        data.put(collectionKey, collectionTag);
    }

    public <T> void saveInstance(CompoundTag data, String instanceKey, T instance, NBTSerializer<T> serializer){
        if (instance != null)
            data.put(instanceKey, serializer.toNBT(instance));
    }

    public void sync(){
        CompoundTag tag = new CompoundTag();
        this.save(tag);
        StorageManager.toServer(holder);
    }

}
