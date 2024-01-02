package com.Arhke.ArhkeLib.Lib.ItemUtil.CustomItem;


public interface Attributes {
    enum MCAttributes implements Attributes {
        MAXHEALTH("generic.maxHealth", "Max Health"), KNOCKBACKRESIST("generic.knockbackResistance", "KnockBack Resist"),
        ATTACKDAMAGE("generic.attackDamage", "Attack Damage"), ARMOR("generic.armor", "Armor"),
        ARMORTOUGHNESS("generic.armorToughness", "Armor Toughness"), ATTACKSPEED("generic.attackSpeed", "Attack Speed"),
        LUCK("generic.luck", "Luck"), SPEED("generic.movementSpeed", "Speed");
        final String name, verbose;
        MCAttributes(String name, String newname){
            this.name = name; this.verbose = newname;
        }
        public static MCAttributes getAttribute(String identifier){
            for(MCAttributes attr: MCAttributes.values()){
                if(attr.name().equals(identifier)){
                    return attr;
                }
            }
            return null;
        }
        public String getName() {
            return name;
        }
        public String getVerbose(){
            return this.verbose;
        }
    }
    enum CustomAttributes implements Attributes{
        DODGE("Dodge"), DEFLECT("Deflect"), HEALING("Healing"), DEFENSE("Defense"), RANGEDDAMAGE("Ranged Damage"),DURABILITY("Durability"), CRITDMG("Crit Damage"), MAGICRESIST("Magic Resist"),
        PHYSDMG("&7Physical Damage"), MAGICDMG("&9Magic Damage"), FIREDMG("&cFire Damage"), BLASTDMG("&4Blast Damage"), PROJDMG("&2Projectile Damage"), LIFESTEAL("Lifesteal"), HUNGER("Hunger Reduction");


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
        ONHITPOISON("Poisonous Blade"), POISONRESISTANCE("Poison Immunity"), ONHITWITHER("Corruption Touch"), WITHERRESISTANCE("Wither Immunity"), ONHITWEAKNESS("Degrading Weakness"),
        WEAKNESSRESISTANCE("Weakness Immunity"),ONHITBLIND("Blinding Light"), BLINDRESISTANCE("Un-Blind-able"), ONHITSLOW("Leg Breaker"), SLOWRESISTANCE("Slowness Immunity"),
        ONHITHEAL("Vampirism"), ANTIHEAL1("Painful Wounds"), ANTIHEAL2("Nasty Wounds"), ANTIHEAL3("Grievous Wounds"), ANTIHEAL4("Life-Threatening Wounds"), ANTIHEALRESISTANCE("Anti-Heal Immunity"),
        ONHITABSORPTION("Shielding"), ONHITCLEANSER("Anti-Potion Buff"), ONHITHUNGER("Starv'em"), HUNGERRESISTANCE("Hunger Immunity"),
        FATIGUERESISTANCE("Fatigue Immunity"), LEVITATIONRESISTANCE("Levitation Immunity"), CONFUSIONRESISTANCE("Confusion Immunity");
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

