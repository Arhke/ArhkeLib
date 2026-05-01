package com.Arhke.ArhkeLib.ItemUtil.CustomItem;


import org.bukkit.attribute.Attribute;

public interface Attributes {
    enum MCAttributes implements Attributes {

        MAXHEALTH(Attribute.MAX_HEALTH, "Max Health"), KNOCKBACKRESIST(Attribute.KNOCKBACK_RESISTANCE, "KnockBack Resist"),
        ATTACKDAMAGE(Attribute.ATTACK_DAMAGE, "Attack Damage"), ARMOR(Attribute.ARMOR, "Armor"),
        ARMORTOUGHNESS(Attribute.ARMOR_TOUGHNESS, "Armor Toughness"), ATTACKSPEED(Attribute.ATTACK_SPEED, "Attack Speed"),
        LUCK(Attribute.LUCK, "Luck"), SPEED(Attribute.MOVEMENT_SPEED, "Speed");
        final Attribute attr;
        final String verbose;
        MCAttributes(Attribute attr, String newname){
            this.attr = attr; this.verbose = newname;
        }
        public static MCAttributes getAttribute(String identifier){
            for(MCAttributes attr: MCAttributes.values()){
                if(attr.name().equals(identifier)){
                    return attr;
                }
            }
            return null;
        }
        public Attribute getAttr() {
            return attr;
        }
        public String getVerbose(){
            return this.verbose;
        }
    }
    enum CustomAttributes implements Attributes{
        DODGE("Hit Dodge Chance"), DEFLECT("Arrow Deflect Chance"), HEALING("Healing"), DEFENSE("Flat Damage Reduction"),
        RANGEDDAMAGE("Ranged Damage"),DURABILITY("Durability"), CRITDMG("Crit Damage"), MAGICRESIST("Magic Resist"),
        PHYSDMG("&7Physical Damage"), MAGICDMG("&bMagic Damage"), FIREDMG("&cFire Damage"), BLASTDMG("&4Blast Damage"),
        PROJDMG("&2Projectile Damage"), LIFESTEAL("Lifesteal"), HUNGER("Hunger Reduction"),
        DMGCAP("Max Damage Taken &7(per hit)");


        public static CustomAttributes getAttribute(String identifier){
            for(CustomAttributes attr: CustomAttributes.values()){
                if(attr.name().equals(identifier)){
                    return attr;
                }
            }
            return null;
        }
        final String verbose;
        CustomAttributes(String verbose){
            this.verbose = verbose;
        }
        @Override
        public String getVerbose(){
            return verbose;
        }

    }
    enum CustomEffects implements Attributes{
        ONHITPOISON("Poisonous Blade"), POISONRESISTANCE("Poison Immunity"), ONHITWITHER("Withering Touch"), WITHERRESISTANCE("Wither Immunity"), ONHITWEAKNESS("Weakening Hit"),
        WEAKNESSRESISTANCE("Weakness Immunity"),ONHITBLIND("Blinding Attack"), BLINDRESISTANCE("Blind Immunity"), ONHITSLOW("Slow-On-Hit"), SLOWRESISTANCE("Slowness Immunity"),
        ONHITHEAL("Vampire-ism"), ANTIHEAL1("25% Anti-Heal"), ANTIHEAL2("40% Anti-Heal"), ANTIHEAL3("60% Anti-Heal"), ANTIHEAL4("100% Anti-Heal"), ANTIHEALRESISTANCE("Anti-Heal Immunity"),
        ONHITABSORPTION("Absorption On-Hit"), ONHITREGENERATION("Regeneration On-Hit"), ONHITCLEANSER("Anti-Potion Buff"), ONHITHUNGER("Starving Touch"), HUNGERRESISTANCE("Hunger Effect Immunity"),
        FATIGUERESISTANCE("Mining-Fatigue Immunity"), LEVITATIONRESISTANCE("Levitation Immunity"), CONFUSIONRESISTANCE("Confusion Immunity"), MACERESISTANCE("Complete Mace Immunity");
        public static CustomEffects getAttribute(String identifier){
            for(CustomEffects attr: CustomEffects.values()){
                if(attr.name().equals(identifier)){
                    return attr;
                }
            }
            return null;
        }
        final String verbose;
        CustomEffects(String verbose){
            this.verbose = verbose;
        }
        @Override
        public String getVerbose(){
            return verbose;
        }

    }
    static Attributes getAttribute(String identifier){
        Attributes attr;
        if((attr = MCAttributes.getAttribute(identifier))!=null) return attr;
        if((attr = CustomAttributes.getAttribute(identifier))!=null) return attr;
        if((attr = CustomEffects.getAttribute(identifier))!=null) return attr;
        return null;
    }
    String getVerbose();
}

