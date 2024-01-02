package com.Arhke.ArhkeLib.Lib.ItemUtil.CustomItem;

import com.Arhke.ArhkeLib.Lib.Base.Base;
import de.tr7zw.nbtapi.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorTags extends Base {
    NBTItem _nbti;
    public static final String AttrModifiers = "AttributeModifiers";
    public static final String Amount = "Amount",
            AttributeName = "AttributeName", Name = "Name", Operation = "Operation", UUIDLeast = "UUIDLeast",
            UUIDMost = "UUIDMost", SlotString = "Slot";
    public static final String HideFlags = "HideFlags";
    public ArmorTags(ItemStack itemStack) {
        _nbti = new NBTItem(itemStack);
    }
    public ArmorTags(NBTItem nbti) {
        _nbti = nbti;
    }
    public void applyTag(Attributes attribute, Slot slot, Operations operation, double strength){
        applyTag(attribute.toString() + operation, attribute,slot, operation, strength);

    }
    public void applyTag(String identifier, Attributes attribute, Slot slot, Operations operation, double strength){
        if(attribute instanceof Attributes.MCAttributes ) {
            applyTag(identifier, slot, (Attributes.MCAttributes) attribute, operation, strength);
        }else if(attribute instanceof Attributes.CustomAttributes) {
            applyTag(identifier, slot, (Attributes.CustomAttributes) attribute, strength);
        }else if(attribute instanceof Attributes.CustomEffects) {
            applyTag(identifier, slot, (Attributes.CustomEffects) attribute);
        }
    }
    private void applyTag(String identifier, Slot slot, Attributes.MCAttributes attribute, Operations operation, double strength){
        NBTCompoundList nbtlist = _nbti.getCompoundList(AttrModifiers);
        for (NBTListCompound NLC : nbtlist) {
            if (NLC.getString(Name).equals(identifier) && NLC.getString(AttributeName).equals(attribute.getName())) {
                NLC.setDouble(Amount, NLC.getDouble(Amount) + strength);
                NLC.setInteger(Operation, operation.getID());
                return;
            }
        }
        NBTListCompound NLC = nbtlist.addCompound();
        NLC.setDouble(Amount, strength);
        NLC.setString(AttributeName,  attribute.getName());
        NLC.setString(Name, identifier);
        NLC.setInteger(Operation, operation.getID());
        NLC.setInteger(UUIDLeast, Base.randInt(2147483647));
        NLC.setInteger(UUIDMost, Base.randInt(2147483647));
        NLC.setString(SlotString, slot.getID());
    }
    private void applyTag(String identifier, Slot slot, Attributes.CustomAttributes attribute, double strength){
        if (strength == 0) return;
        if(attribute == Attributes.CustomAttributes.DURABILITY) slot = Slot.MAINHAND;

        StringBuilder sb = new StringBuilder();
        if(_nbti.hasKey(attribute.name())){
            sb.append(_nbti.getString(attribute.name())).append(",");
        }
        sb.append(slot.getCode()).append(":").append(strength).append(":").append(identifier);

        _nbti.setString(attribute.name(), sb.toString() );
    }
    private void applyTag(String identifier, Slot slot, Attributes.CustomEffects attribute){
        StringBuilder sb = new StringBuilder();
        if(_nbti.hasKey(attribute.name())){
            sb.append(_nbti.getString(attribute.name())).append(",");
        }
        sb.append(slot.getCode()).append(":").append(identifier);

        _nbti.setString(attribute.name(), sb.toString());
    }
    public void removeTag(String identifier, Attributes attribute){
        if (attribute instanceof Attributes.MCAttributes){
            removeTag(identifier, (Attributes.MCAttributes) attribute);
        }else if (attribute instanceof Attributes.CustomAttributes){
            removeTag(identifier, (Attributes.CustomAttributes) attribute);
        }else if (attribute instanceof Attributes.CustomEffects){
            removeTag(identifier, (Attributes.CustomEffects) attribute);
        }
    }
    private void removeTag(String identifier, Attributes.MCAttributes attribute){
        _nbti.getCompoundList(AttrModifiers).removeIf(NLC -> NLC.getString(Name).equals(identifier) && NLC.getString(AttributeName).equals(attribute.getName()));
    }
    private void removeTag(String identifier, Attributes.CustomAttributes attribute){
        if(_nbti.hasKey(attribute.name())){
            String att = _nbti.getString(attribute.name());
            String[] attributeEntries =  att.split(",");
            StringBuilder updateEntry = new StringBuilder();
            for (String attributeEntry: attributeEntries){
                if(!attributeEntry.endsWith(identifier)){
                    if(updateEntry.length() != 0){
                        updateEntry.append(',');
                    }
                    updateEntry.append(attributeEntry);
                }

            }
            _nbti.setString(attribute.name(), updateEntry.toString());
        }


    }
    private void removeTag(String identifier, Attributes.CustomEffects attribute){
        if(_nbti.hasKey(attribute.name())){
            String att = _nbti.getString(attribute.name());
            String[] attributeEntries =  att.split(",");
            StringBuilder updateEntry = new StringBuilder();
            for (String attributeEntry: attributeEntries){
                if(!attributeEntry.endsWith(identifier)){
                    if(updateEntry.length() != 0){
                        updateEntry.append(',');
                    }
                    updateEntry.append(attributeEntry);
                }

            }
            _nbti.setString(attribute.name(), updateEntry.toString());
        }


    }

    //==============<Accessories>===============
    public static final String NBTIAccessory = "NBTIAccessoryiNfO",NBTISlotCount = "NBTIAccessorySlotCount",NBTIMaxSlotCount = "NBTIMAxAccessorySlotCount";
    public void setMaxAccessorySlot(int value){
        _nbti.setInteger(NBTIMaxSlotCount, value);
    }
    //attribute:value
    public void addTagToAccessory(Attributes.CustomAttributes attribute, double strength){
        String temp = _nbti.getString(NBTIAccessory);
        if(temp == null)temp = "";
        if(temp.isEmpty()) temp = attribute.name()+":"+strength;
        else temp = temp+","+attribute.name()+":"+strength;
        _nbti.setString(NBTIAccessory, temp);
    }
    public void addTagToAccessory(Attributes.MCAttributes attribute, double strength){
        String temp = _nbti.getString(NBTIAccessory);
        if(temp == null)temp = "";
        if(temp.isEmpty()) temp = attribute.name()+":"+strength;
        else temp = temp+","+attribute.name()+":"+strength;
        _nbti.setString(NBTIAccessory, temp);
    }
    public void addTagToAccessory(Attributes.CustomEffects attribute){
        String temp = _nbti.getString(NBTIAccessory);
        if(temp == null)temp = "";
        if(temp.isEmpty()) temp = attribute.name();
        else temp = temp+","+attribute.name();
        _nbti.setString(NBTIAccessory, temp);
    }
    public boolean addAccessory(NBTItem accessory, Slot slot){
        if(_nbti.hasKey(NBTIAccessory) || !_nbti.hasKey(NBTIMaxSlotCount) || !accessory.hasKey(NBTIAccessory)){
            return false;
        }
        Integer temp = _nbti.getInteger(NBTISlotCount);
        int slotIndex = temp == null? 0:temp;
        temp = _nbti.getInteger(NBTIMaxSlotCount);
        int maxSlot = temp == null? 0:temp;
        if(slotIndex >= maxSlot){
            return false;
        }
        String[] accessoryList = accessory.getString(NBTIAccessory).split(",");
        for(String accessoryEntry:accessoryList){
            int colon = accessoryEntry.indexOf(":");
            if (colon == -1) colon = accessoryEntry.length();
            Attributes attribute;
            try {
                 attribute = Attributes.getAttribute(accessoryEntry.substring(0, colon));
            }catch(IllegalArgumentException e){
                continue;
            }
            double value = 0;
            try {
                value = Double.parseDouble(accessoryEntry.substring(colon));
            }catch(NumberFormatException|IndexOutOfBoundsException ignored){
            }
            applyTag(slotIndex+"", attribute, slot, attribute == Attributes.MCAttributes.ATTACKDAMAGE || attribute == Attributes.MCAttributes.ATTACKSPEED?Operations.MULTIPLY: Operations.ADD, value);
        }
        _nbti.setInteger(NBTISlotCount, slotIndex+1);
        return true;
    }
    public boolean removeAccessory(){
        if(_nbti.hasKey(NBTIAccessory) || !_nbti.hasKey(NBTIMaxSlotCount)){
            return false;
        }
        Integer temp = _nbti.getInteger(NBTISlotCount);
        int slotIndex = temp == null? 0:temp;
        for(Attributes attribute: Attributes.CustomAttributes.values()) {
            removeTag((slotIndex - 1) + "", attribute);
        }
        for(Attributes attribute: Attributes.MCAttributes.values()) {
            removeTag((slotIndex - 1) + "", attribute);
        }
        for(Attributes attribute: Attributes.CustomEffects.values()) {
            removeTag((slotIndex - 1) + "", attribute);
        }

        _nbti.setInteger(NBTISlotCount, slotIndex-1);
        return true;
    }
    public static boolean isAccessory(NBTItem accessory){
        return accessory.hasKey(NBTIAccessory);
    }
    public static boolean canAttach(NBTItem nbti){
        if(nbti.hasKey(NBTIAccessory) || !nbti.hasKey(NBTIMaxSlotCount)){
            return false;
        }
        Integer temp = nbti.getInteger(NBTISlotCount);
        int slotIndex = temp == null? 0:temp;
        temp = nbti.getInteger(NBTIMaxSlotCount);
        int maxSlot = temp == null? 0:temp;
        return slotIndex < maxSlot;
    }

    public static double getPlayerAttribute(Player player, Attributes.CustomAttributes attributes) {
        double ret = 0.0;
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length;i++) {
            ItemStack is = armor[i];
            if (is != null && is.getType() != Material.AIR) {
                NBTItem nbti = new NBTItem(is);
                if (nbti.hasKey(attributes.name())) {
                    ret += getCustomTagDouble(nbti, i, attributes);
                }
            }
        }
        ItemStack is = player.getInventory().getItemInOffHand();
        if (is.getType() != Material.AIR) {
            NBTItem nbti = new NBTItem(is);
            if (nbti.hasKey(attributes.name())) {
                ret += getCustomTagDouble(nbti, Slot.OFFHAND.getCode(), attributes);
            }
        }
        is = player.getInventory().getItemInMainHand();
        if (is.getType() != Material.AIR) {
            NBTItem nbti = new NBTItem(is);
            if (nbti.hasKey(attributes.name())) {
                ret += getCustomTagDouble(nbti, Slot.MAINHAND.getCode(), attributes);
            }
        }
        return ret;
    }
    public static boolean hasPlayerEffect(Player player, Attributes.CustomEffects effects) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length;i++) {
            ItemStack is = armor[i];
            if (is != null && is.getType() != Material.AIR) {
                NBTItem nbti = new NBTItem(is);
                if (nbti.hasKey(effects.name()) && hasEffect(nbti, i, effects)) {
                    return true;
                }
            }
        }
        ItemStack is = player.getInventory().getItemInOffHand();
        if (is.getType() != Material.AIR) {
            NBTItem nbti = new NBTItem(is);
            if (nbti.hasKey(effects.name()) && hasEffect(nbti, Slot.OFFHAND.getCode(), effects)) {
                return true;
            }
        }
        is = player.getInventory().getItemInMainHand();
        if (is.getType() != Material.AIR) {
            NBTItem nbti = new NBTItem(is);
            //noinspection RedundantIfStatement
            if (nbti.hasKey(effects.name()) && hasEffect(nbti, Slot.MAINHAND.getCode(), effects)) {
                return true;
            }
        }
        return false;
    }
    //slotnum:value:identifier
    public static double getCustomTagDouble(NBTItem nbti, int slotnum, Attributes.CustomAttributes attribute){
        if(attribute == Attributes.CustomAttributes.DURABILITY) slotnum = Slot.MAINHAND.getCode();
        String value = nbti.getString(attribute.name());
        if(value == null){
            return 0d;
        }
        double ret = 0d;
        int start = 0;
        while(true){
            start = value.indexOf(slotnum+":", start);
            if (start == -1){
                break;
            }
            int end = value.indexOf(",", start);
            if(end == -1){
                end = value.length();
            }
            String[] triplet = value.substring(start, end).split(":");
            if(triplet.length != 3){
                warn("Unable to parse custom tag double");
                return 0;
            }
            try {
                ret+=Double.parseDouble(triplet[1]);
            }catch(NumberFormatException|IndexOutOfBoundsException e){
                return 0d;
            }
            start = end;
        }
        return ret;
    }
    //slotnum:identifier
    public static boolean hasEffect(NBTItem nbti, int slotnum, Attributes.CustomEffects effects){
        String value = nbti.getString(effects.name());
        if(value == null){
            return false;
        }
        return value.indexOf(slotnum+":") != -1;
    }

    public void hideFlags(Flag... flags){
        int count = 0;
        for(Flag flag:flags){
            count += flag.getID();
        }
        _nbti.setInteger(HideFlags,count);
    }

    public ItemStack getItem(){
        return _nbti.getItem();
    }
    public NBTItem getNBTI() {return _nbti;}


    public enum Operations {
        ADD(0), MULTIPLYBASE(1), MULTIPLY(2);
        final int _id;
        Operations(int id){
            _id = id;
        }
        public int getID() {
            return _id;
        }
    }
    public enum Flag {
        ENCHANTMENTS(1), ATTRIBUTEMODIFIERS(2), UNBREAKABLE(4), CANDESTROY(8), CANPLACEMOBS(16), OTHER(32), DYED(64);
        final int _id;
        Flag(int id){
            _id = id;
        }
        public int getID(){
            return _id;
        }
    }
    public enum Slot{
        MAINHAND(5, "mainhand", "Main Hand"), OFFHAND(4, "offhand", "Off Hand"),
        HELMET(3, "head", "Head"), CHESTPLATE(2, "chest", "Chest"), LEGGINGS(1, "legs", "Legs"), BOOTS(0, "feet", "Feet");
        private final String _id;
        private final String _name;
        private final int _code;
        Slot(int code, String id, String name){
            _code = code;
            _name = name;
            _id = id;
        }
        public String getName() {
            return _name;
        }
        public String getID() {
            return _id;
        }
        public int getCode(){
            return _code;
        }
        @Override
        public String toString() {
            return _id;
        }
    }
}
