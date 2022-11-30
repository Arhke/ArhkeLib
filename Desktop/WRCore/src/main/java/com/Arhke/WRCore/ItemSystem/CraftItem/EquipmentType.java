package com.Arhke.WRCore.ItemSystem.CraftItem;

import com.Arhke.WRCore.Lib.ArmorTags.*;
import org.bukkit.Material;

public enum EquipmentType {
    HELMET("Helmet", Slot.HELMET), CHESTPLATE("Chestplate", Slot.CHESTPLATE), LEGGINGS("Leggings", Slot.LEGGINGS), BOOTS("Boots", Slot.BOOTS),
    SWORD("Sword", Slot.MAINHAND), AXE("Axe", Slot.MAINHAND), PICKAXE("Pickaxe", Slot.MAINHAND), Spade("Shovel", Slot.MAINHAND),
    HOE("Hoe", Slot.MAINHAND), SHIELD("Shield", Slot.OFFHAND), OTHER("", Slot.MAINHAND);
    String _name;
    Slot _slot;
    EquipmentType(String name, Slot slot){
        _name = name;
        _slot = slot;
    }
    public Slot getSlot(){
        return _slot;
    }
    public String getName(){
        return _name;
    }
    public static EquipmentType getEquipmentType(Material material){
        for(EquipmentType et: EquipmentType.values()) {
            if(material.name().endsWith(et.name())){
                return et;
            }
        }
        return EquipmentType.OTHER;
    }
}
