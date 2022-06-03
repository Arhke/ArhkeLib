package com.Arhke.ArhkeLib.VDC.v18;


import com.Arhke.ArhkeLib.Lib.Base.Base;
import com.google.common.collect.HashBiMap;
import com.sk89q.worldedit.world.entity.EntityTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("rawtypes")
public class CustomEntityRegistry {
    public static void register(String name, int id, Class<?> registryClass) {
//        ((Map) Base.getPrivateField("c", EntityTypes.class, null)).put(name, registryClass);
//        ((Map) Base.getPrivateField("d", EntityTypes.class, null)).put(registryClass, name);
//        ((Map) Base.getPrivateField("f", EntityTypes.class, null)).put(registryClass, id);
    }
}