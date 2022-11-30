package com.Arhke.ArhkeLib.VDC.v18;


import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;


@SuppressWarnings("rawtypes")
public class CustomEntityRegistry {
    public static void register(String name, int id, Class<?> registryClass) {
//        ((Map) Base.getPrivateField("c", EntityTypes.class, null)).put(name, registryClass);
//        ((Map) Base.getPrivateField("d", EntityTypes.class, null)).put(registryClass, name);
//        ((Map) Base.getPrivateField("f", EntityTypes.class, null)).put(registryClass, id);
    }
    public static void registerEntities() {
        Map<String, Type<?>> types = (Map<String, Type<?>>) DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).findChoiceType(References.ENTITY).types();

        unfreezeRegistry();
//        registerEntity("giant", GiantEntity::new, types);
        Registry.ENTITY_TYPE.freeze();
    }

    private static void unfreezeRegistry() {
        /* As of 1.18.2, registries are frozen once NMS is done adding to them,
           so we have to do some super hacky things to add custom entities now.
           Basically, when the registry is frozen, the "frozen" field is set to prevent new entries,
           and a map "intrusiveHolderCache" is set to null (don't really know what it does.)
           If frozen is true or "intrusiveHolderCache" is null, it will refuse to add entries,
           so we just have to fix both of those things and it'll let us add entries again.
           The registry being frozen may be vital to how the registry works (idk), so it is refrozen after adding our entries.

           Partial stack trace produced when trying to add entities when the registry is frozen:
           [Server thread/ERROR]: Registry is already frozen initializing UltraCosmetics v2.6.1-DEV-b5 (Is it up to date?)
            java.lang.IllegalStateException: Registry is already frozen
                    at net.minecraft.core.RegistryMaterials.e(SourceFile:343) ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da]
                    at net.minecraft.world.entity.EntityTypes.<init>(EntityTypes.java:300) ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da]
                    at net.minecraft.world.entity.EntityTypes$Builder.a(EntityTypes.java:669) ~[spigot-1.18.2-R0.1-SNAPSHOT.jar:3445-Spigot-fb0dd5f-05a38da]
                    at be.isach.ultracosmetics.v1_18_R2.customentities.CustomEntities.registerEntity(CustomEntities.java:78) ~[?:?]
        */
        Class<MappedRegistry> registryClass = MappedRegistry.class;
        try {
            Field intrusiveHolderCache = registryClass.getDeclaredField(ObfuscatedFields.INTRUSIVE_HOLDER_CACHE);
            intrusiveHolderCache.setAccessible(true);
            intrusiveHolderCache.set(Registry.ENTITY_TYPE, new IdentityHashMap<EntityType<?>, Holder.Reference<EntityType<?>>>());
            Field frozen = registryClass.getDeclaredField(ObfuscatedFields.FROZEN);
            frozen.setAccessible(true);
            frozen.set(Registry.ENTITY_TYPE, false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void registerEntity(String type, EntityFactory customMob, Map<String, Type<?>> types) {
        String customName = "minecraft:ultracosmetics_" + type;
        types.put(customName, types.get("minecraft:" + type));
        EntityType.Builder<Entity> a = EntityType.Builder.of(customMob, MobCategory.AMBIENT);
        Registry.register(Registry.ENTITY_TYPE, customName, a.build(customName));
    }

}