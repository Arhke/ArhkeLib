package com.Arhke.ArhkeLib.Lib.ItemUtil;

import com.Arhke.ArhkeLib.Lib.ItemUtil.CustomItem.ArmorTags.Slot;
import org.bukkit.Material;

public enum EquipmentType {
    HELMET("Helmet", Slot.HELMET),
    CHESTPLATE("Chestplate", Slot.CHESTPLATE), LEGGINGS("Leggings", Slot.LEGGINGS), BOOTS("Boots", Slot.BOOTS),
    SWORD("Sword", Slot.MAINHAND), AXE("Axe", Slot.MAINHAND), PICKAXE("Pickaxe", Slot.MAINHAND), SPADE("Shovel", Slot.MAINHAND),
    HOE("Hoe", Slot.MAINHAND), SHIELD("Shield", Slot.OFFHAND), OTHER("", Slot.OFFHAND);
    final String _name;
    final Slot _slot;
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
