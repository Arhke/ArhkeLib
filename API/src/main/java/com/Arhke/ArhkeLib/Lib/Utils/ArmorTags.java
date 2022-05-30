//package com.Arhke.ArhkeLib.Lib.Utils;
//
//import com.Arhke.ArhkeLib.Lib.Base.Base;
//
//import de.tr7zw.nbtapi.NBTItem;
//import de.tr7zw.nbtapi.NBTList;
//import de.tr7zw.nbtapi.NBTListCompound;
//import de.tr7zw.nbtapi.NBTType;
//import org.bukkit.inventory.ItemStack;
//
//public class ArmorTags extends Base{
//    NBTItem _nbti;
//    public static final String AttrModifiers = "AttributeModifiers";
//    public static final String Amount = "Amount",
//    AttributeName = "AttributeName", Name = "Name", Operation = "Operation", UUIDLeast = "UUIDLeast",
//    UUIDMost = "UUIDMost", Slot = "Slot";
//    public static final String HideFlags = "HideFlags";
//    public ArmorTags(ItemStack itemStack) {
//        _nbti = new NBTItem(itemStack);
//    }
//    public ArmorTags(NBTItem nbti) {
//        _nbti = nbti;
//    }
//    public void applyTag(Attributes attribute, Slot slot, Operations operation, double strength){
//        applyTag(attribute.getName()+operation, slot, attribute, operation, strength);
//    }
//    public void applyTag(String name, Slot slot, Attributes attribute, Operations operation, double strength){
//        NBTList nbtlist = _nbti.getgetList(AttrModifiers, NBTType.NBTTagCompound);
//        for(int i = 0; i < nbtlist.size(); i++) {
//            NBTListCompound NLC = nbtlist.getCompound(i);
//            if (NLC.getString(Name).equals(name)){
//                NLC.setDouble(Amount, NLC.getDouble(Amount) + strength);
//                NLC.setInteger(Operation, operation.getID());
//                return;
//            }
//        }
//        NBTListCompound NLC = nbtlist.getParent().addCompound();
//        NLC.setDouble(Amount, strength);
//        NLC.setString(AttributeName,  attribute.getName());
//        NLC.setString(Name, name);
//        NLC.setInteger(Operation, operation.getID());
//        NLC.setInteger(UUIDLeast, Base.randInt(2147483647));
//        NLC.setInteger(UUIDMost, Base.randInt(2147483647));
//        NLC.setString(Slot, slot.getID());
//    }
//
//
//    public void hideFlags(Flag... flags){
//        int count = 0;
//        for(Flag flag:flags){
//            count += flag.getID();
//        }
//        _nbti.setInteger(HideFlags,count);
//    }
//    public ItemStack getItem(){
//        return _nbti.getItem();
//    }
//    public NBTItem getNBTI() {return _nbti;}
//    public enum Attributes {
//        MAXHEALTH("generic.maxHealth", "Max Health"), KNOCKBACKRESIST("generic.knockbackResistance", "KnockBack Resist"),
//        ATTACKDAMAGE("generic.attackDamage", "Attack Damage"), ARMOR("generic.armor", "Armor"),
//        ARMORTOUGHNESS("generic.armorToughness", "Armor Toughness"), ATTACKSPEED("generic.attackSpeed", "Attack Speed"),
//        LUCK("generic.luck", "Luck"), SPEED("generic.movementSpeed", "Speed");
//        String _name;
//        String _display;
//        Attributes(String name, String display){
//            _name = name;
//            _display = display;
//        }
//        public String getName() {
//            return _name;
//        }
//        public String getDisplay(){ return _display;}
//    }
//    public enum Operations {
//        ADD(0), MULTIPLYBASE(1), MULTIPLY(2);
//        int _id;
//        Operations(int id){
//            _id = id;
//        }
//        public int getID() {
//            return _id;
//        }
//    }
//    public enum Flag {
//        ENCHANTMENTS(1), ATTRIBUTEMODIFIERS(2), UNBREAKABLE(4), CANDESTROY(8), CANPLACEMOBS(16), OTHER(32), DYED(64);
//        int _id;
//        Flag(int id){
//            _id = id;
//        }
//        public int getID(){
//            return _id;
//        }
//    }
//    public enum Slot{
//        MAINHAND(5, "mainhand", "ArhkeLib Hand"), OFFHAND(4, "offhand", "Off Hand"),
//        HELMET(3, "head", "Head"), CHESTPLATE(2, "chest", "Chest"), LEGGINGS(1, "legs", "Legs"), BOOTS(0, "feet", "Feet");
//        private String _id;
//        private String _name;
//        private int _code;
//        Slot(int code, String id, String name){
//            _code = code;
//            _name = name;
//            _id = id;
//        }
//        public String getName() {
//            return _name;
//        }
//        public String getID() {
//            return _id;
//        }
//        public int getCode(){
//            return _code;
//        }
//        @Override
//        public String toString() {
//            return _id;
//        }
//    }
//}
