package com.Arhke.ArhkeLib.ItemUtil.CustomItem;


import org.bukkit.attribute.Attribute;

public interface Attributes {
    enum MCAttributes implements Attributes {

        MAXHEALTH(Attribute.MAX_HEALTH, "Max Health"),
        KNOCKBACKRESIST(Attribute.KNOCKBACK_RESISTANCE, "Knockback Resistance"),
        SPEED(Attribute.MOVEMENT_SPEED, "Speed"),
        FLYING_SPEED(Attribute.FLYING_SPEED, "Flying Speed"),
        ATTACKDAMAGE(Attribute.ATTACK_DAMAGE, "Attack Damage"),
        ATTACK_KNOCKBACK(Attribute.ATTACK_KNOCKBACK, "Attack Knockback"),
        ATTACKSPEED(Attribute.ATTACK_SPEED, "Attack Speed"),
        ARMOR(Attribute.ARMOR, "Armor"),
        ARMORTOUGHNESS(Attribute.ARMOR_TOUGHNESS, "Armor Toughness"),
        FALL_DAMAGE_MULTIPLIER(Attribute.FALL_DAMAGE_MULTIPLIER, "Fall Damage Multiplier"),
        LUCK(Attribute.LUCK, "Luck"),
        MAX_ABSORPTION(Attribute.MAX_ABSORPTION, "Max Absorption"),
        SAFE_FALL_DISTANCE(Attribute.SAFE_FALL_DISTANCE, "Safe Fall Distance"),
        SCALE(Attribute.SCALE, "Scale"),
        STEP_HEIGHT(Attribute.STEP_HEIGHT, "Step Height"),
        GRAVITY(Attribute.GRAVITY, "Gravity"),
        JUMP_STRENGTH(Attribute.JUMP_STRENGTH, "Jump Strength"),
        BURNING_TIME(Attribute.BURNING_TIME, "Burning Time"),
        EXPLOSION_KNOCKBACK_RESISTANCE(Attribute.EXPLOSION_KNOCKBACK_RESISTANCE, "Explosion Knockback Resistance"),
        MOVEMENT_EFFICIENCY(Attribute.MOVEMENT_EFFICIENCY, "Movement Efficiency"),
        OXYGEN_BONUS(Attribute.OXYGEN_BONUS, "Oxygen Bonus"),
        WATER_MOVEMENT_EFFICIENCY(Attribute.WATER_MOVEMENT_EFFICIENCY, "Water Movement Efficiency"),
        BLOCK_INTERACTION_RANGE(Attribute.BLOCK_INTERACTION_RANGE, "Block Interaction Range"),
        ENTITY_INTERACTION_RANGE(Attribute.ENTITY_INTERACTION_RANGE, "Entity Interaction Range"),
        BLOCK_BREAK_SPEED(Attribute.BLOCK_BREAK_SPEED, "Block Break Speed"),
        MINING_EFFICIENCY(Attribute.MINING_EFFICIENCY, "Mining Efficiency"),
        SNEAKING_SPEED(Attribute.SNEAKING_SPEED, "Sneaking Speed"),
        SUBMERGED_MINING_SPEED(Attribute.SUBMERGED_MINING_SPEED, "Submerged Mining Speed"),
        SWEEPING_DAMAGE_RATIO(Attribute.SWEEPING_DAMAGE_RATIO, "Sweeping Damage Ratio");
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

