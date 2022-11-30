package com.Arhke.WRCore.ItemSystem.FurnitureMapping;

import com.Arhke.WRCore.Main;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;

public enum FurnitureType{
    SMELTERY(Smeltery.class), TANNERY(Tannery.class),
    CAULDRON(CauldronFurniture.class);
    Class<? extends Furniture> fclass;
    FurnitureType(Class<? extends Furniture> fclass)  {
        this.fclass = fclass;
    }
    public Furniture newInstance(Main plugin, Location location){
        try {
            return fclass.getConstructor(Main.class, Location.class).newInstance(plugin,location);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }
    public boolean isInstance(Furniture furniture){
        return fclass.isInstance(furniture);
    }
}