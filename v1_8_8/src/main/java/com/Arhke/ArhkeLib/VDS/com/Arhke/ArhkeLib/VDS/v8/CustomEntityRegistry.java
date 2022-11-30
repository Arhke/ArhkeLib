package com.Arhke.ArhkeLib.VDS.com.Arhke.ArhkeLib.VDS.v8;

import com.sk89q.worldedit.world.entity.EntityTypes;

import java.lang.reflect.Method;

public class CustomEntityRegistry {
//    public void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass){
//        try {
//
//            List<Map<?, ?>> dataMap = new ArrayList<Map<?, ?>>();
//            for (Field f : EntityTypes.class.getDeclaredFields()){
//                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())){
//                    f.setAccessible(true);
//                    dataMap.add((Map<?, ?>) f.get(null));
//                }
//            }
//
//            if (dataMap.get(2).containsKey(id)){
//                dataMap.get(0).remove(name);
//                dataMap.get(2).remove(id);
//            }
//
//            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
//            method.setAccessible(true);
//            method.invoke(null, customClass, name, id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
