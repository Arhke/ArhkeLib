package com.Arhke.WRCore.ItemSystem.CraftItem;

import com.Arhke.WRCore.Lib.ArmorTags;
import com.Arhke.WRCore.Lib.ArmorTags.*;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItem extends MainBase {
    public static final String EquipmentKey = "Equipment", SlotKey = "Slot";

    public static final String DamageKey = "Damage",
            AttackSpeedKey = "AttackSpeed", MaxHealthKey = "MaxHealth", DurabilityKey = "DurabilityMultiplier",
            ArmorKey = "Armor", ArmorToughnessKey = "ArmorToughness", KnockBackResistKey = "KnockbackResist",
            MovementSpeedKey = "MovementSpeed", MagicResistKey = "MagicResist", LuckKey = "Luck", DodgeKey = "Dodge",
            DeflectKey = "Deflect", RangedDamageKey = "RangedDamage", CriticalDmgKey = "CriticalDmg", ColorKey = "Color";

    public static final String NBTIDurability = "DurabilityMultiplier", NBTIMagicResist = "MagicResist",
            NBTIDodge = "Dodge", NBTIDeflect = "Deflect", NBTIRangedDmg = "RangedDamage", NBTICriticalDmg = "CriticalDamage";


    private final Map<Material, ItemStack> equipmentMap = new HashMap<>();
    FileManager fm;
    String id;
    ItemStack is;

    public CustomItem(Main instance, FileManager fm) {
        super(instance);
        this.fm = fm;
        this.id = fm.getFileNameNoExt().toLowerCase();
        DataManager dm = fm.getDataManager();
        this.is = new ItemStack(dm.getMaterial(Material.IRON_INGOT, MaterialKey), 1);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(dm.getTCM(NameKey));
        im.setLore(dm.getStringList(LoreKey));
        is.setItemMeta(im);
        NBTItem nbti = new NBTItem(is);
        nbti.setString(NBTIMaterial, id);
        is = nbti.getItem();

        DataManager em = fm.getDataManager().getDataManager(EquipmentKey);
        for (String key : em.getConfig().getKeys(false)) {
            try {
                ItemStack equipment = getEquipment(em.getDataManager(key));
                if (equipment == null) continue;
                equipmentMap.put(equipment.getType(), equipment);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private ItemStack getEquipment(DataManager dm) throws Exception {
        Material material = dm.getMaterial(Material.IRON_CHESTPLATE, MaterialKey);
        if (!material.isItem()) {
            except("Material for equipment must be Item, skipping wrong entry: " + fm.getFileName());
            return null;
        }
        ItemStack is = new ItemStack(material, 1);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(dm.getTCM(NameKey));
        //lore
        List<String> lore = dm.getStringList(LoreKey);

        im.setLore(lore);
        is.setItemMeta(im);

        is = applyTags(is, EquipmentType.getEquipmentType(material).getSlot(), dm);
        applyColor(is, dm);
        return is;
    }

    protected void applyColor(ItemStack is, DataManager dm) {
        if (!(is.getItemMeta() instanceof LeatherArmorMeta))
            return;
        LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
        lam.setColor(dm.getColor(ColorKey));
        is.setItemMeta(lam);
    }

    protected ItemStack applyTags(ItemStack is, Slot slot, DataManager dm) {
        List<String> lore = is.getItemMeta().getLore();
        ArmorTags at = new ArmorTags(is);
        double durability, damage, attackSpeed, maxHealth, armor, armorToughness, knockbackResist,
                movementSpeed, magicResist, luck, dodge, deflect, rangedDamage, criticalDmg;
        durability = dm.getDouble(1d, DurabilityKey);
        damage = dm.getInt(DamageKey);
        attackSpeed = dm.getDouble(AttackSpeedKey);
        maxHealth = dm.getInt(MaxHealthKey);
        armor = dm.getInt(ArmorKey);
        armorToughness = dm.getInt(ArmorToughnessKey);
        knockbackResist = dm.getDouble(KnockBackResistKey);
        movementSpeed = dm.getDouble(MovementSpeedKey);
        magicResist = dm.getDouble(MagicResistKey);
        luck = dm.getInt(LuckKey);
        dodge = dm.getDouble(DodgeKey);
        deflect = dm.getDouble(DeflectKey);
        rangedDamage = dm.getDouble(RangedDamageKey);
        criticalDmg = dm.getDouble(CriticalDmgKey);

        at.getNBTI().setDouble(NBTIDurability, durability);
        at.applyTag(Attributes.ATTACKDAMAGE, slot, Operations.ADD, damage);
        at.applyTag(Attributes.ATTACKSPEED, slot, Operations.ADD, attackSpeed);
        at.applyTag(Attributes.MAXHEALTH, slot, Operations.ADD, maxHealth);
        at.applyTag(Attributes.ARMOR, slot, Operations.ADD, armor);
        at.applyTag(Attributes.ARMORTOUGHNESS, slot, Operations.ADD, armorToughness);
        at.applyTag(Attributes.KNOCKBACKRESIST, slot, Operations.ADD, knockbackResist);
        at.applyTag(Attributes.SPEED, slot, Operations.ADD, movementSpeed);
        setCustomTag(at.getNBTI(), NBTIMagicResist, slot, magicResist);
        at.applyTag(Attributes.LUCK, slot, Operations.ADD, luck);

        setCustomTag(at.getNBTI(), NBTIDodge, slot, dodge);
        setCustomTag(at.getNBTI(), NBTIDeflect, slot, deflect);
        setCustomTag(at.getNBTI(), NBTIRangedDmg, slot, rangedDamage);
        setCustomTag(at.getNBTI(), NBTICriticalDmg, slot, criticalDmg);

        at.hideFlags(ArmorTags.Flag.ATTRIBUTEMODIFIERS);
        if (slot == Slot.MAINHAND) {
            lore.add(ChatColor.GRAY + " " + damage + " Attack Damage");
            lore.add(ChatColor.GRAY + " " + roundHundredth(4.0+attackSpeed) + " Attack Speed");
            lore.add(ChatColor.GRAY + "When in " + slot.getName() + ":");
        } else if (slot == Slot.OFFHAND) {
            lore.add(ChatColor.GRAY + "When in " + slot.getName() + ":");
            lore.add(loreAssist("Attack Damage", damage, true, false));
            lore.add(loreAssist("Attack Speed", attackSpeed, true, false));
        } else {
            lore.add(ChatColor.GRAY + "When on " + slot.getName() + ":");
            lore.add(loreAssist("Attack Damage", damage, true, false));
            lore.add(loreAssist("Attack Speed", attackSpeed, true, false));
        }
        if (durability != 1) lore.add(loreAssist("Durability", durability-1, false, false));
        lore.add(loreAssist(Attributes.MAXHEALTH.getDisplay(), maxHealth, true, false));
        lore.add(loreAssist(Attributes.ARMOR.getDisplay(), armor, true, false));
        lore.add(loreAssist(Attributes.ARMORTOUGHNESS.getDisplay(), armorToughness, true, false));
        lore.add(loreAssist(Attributes.KNOCKBACKRESIST.getDisplay(), knockbackResist * 100, true, true));
        lore.add(loreAssist(Attributes.SPEED.getDisplay(), movementSpeed / 0.001, true, true));
        lore.add(loreAssist("Magic Resist", magicResist, false, false));
        lore.add(loreAssist(Attributes.LUCK.getDisplay(), luck, false, false));
        lore.add(loreAssist("Dodge", dodge, false, false));
        lore.add(loreAssist("Deflect", deflect, false, false));
        lore.add(loreAssist("Ranged Damage", rangedDamage, true, false));
        lore.add(loreAssist("Crit Damage", criticalDmg, true, false));
        lore.removeIf((s) -> s.length() == 0);
        is = at.getItem();
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    private void setCustomTag(NBTItem nbti, String key, Slot slot, double strength) {
        if (strength == 0) return;
        nbti.setString(key, (nbti.hasKey(key) ? nbti.getString(key) + "," : "") + slot.getCode() + ":" + strength);
    }

    /**
     * this just applies the lore for that specific tag entry, (it doesnt apply the "when on *blank*:" lore)
     */
    private String loreAssist(String display, double strength, boolean displayNum, boolean percentage) {
        if (strength == 0) {
            return "";
        }
        String add;
        if (strength < 0) {
            add = ChatColor.RED + " - ";
        } else {
            add = ChatColor.BLUE + " + ";
        }
        if (displayNum) {
            if (percentage) {
                add += Math.abs(roundInt(strength)) + "% ";
            } else {
                add += Math.abs(roundInt(strength)) + " ";
            }
        }
        add += display;
        return add;
    }

    public ItemStack getEquipment(Material material) {
        ItemStack is = equipmentMap.getOrDefault(material, new ItemStack(Material.AIR));
        return is == null ? null : new ItemStack(is);
    }

    public static final String NameKey = "Name", LoreKey = "Lore", MaterialKey = "Material",
            NBTIMaterial = "Material";

    public String getId() {
        return id;
    }
    public String getName() {
        return is.getItemMeta().getDisplayName();
    }
    public ItemStack getItem() {
        return is;
    }
    public ItemStack getNewItem() {
        return new ItemStack(is);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof CustomItem && ((CustomItem) o).getId().equals(this.getId());
    }

}

