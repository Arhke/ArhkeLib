package com.Arhke.ArhkeLib.Utils.version;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

//This class is copied from me.gorgeousone.paintball.util on github
public final class VersionUtil {
	/**
	 * 1.16
	 *
	 * Renamed generic.maxHealth to generic.max_health.
	 * Renamed generic.knockbackResistance to generic.knockback_resistance.
	 * Renamed generic.movementSpeed to generic.movement_speed.
	 * Renamed generic.attackDamage to generic.attack_damage
	 * Renamed generic.attackKnockback to generic.attack_knockback
	 * Renamed generic.attackSpeed to generic.attack_speed
	 * Renamed generic.armorToughness to generic.armor_toughness
	 *
	 */
	public static Version PLUGIN_VERSION;
	public static Version SERVER_VERSION;
	public static boolean IS_LEGACY_SERVER;
	
	private VersionUtil() {}
	
	public static void setup(JavaPlugin plugin) {
		PLUGIN_VERSION = new Version(plugin.getDescription().getVersion());
		SERVER_VERSION = new Version(getServerVersionString(), "_");
		IS_LEGACY_SERVER = SERVER_VERSION.isBelow(new Version("1.13.0"));
	}
	
	public static String getServerVersionString() {
		return Bukkit.getServer().getClass().getName().split("\\.")[3].replaceAll("[a-zA-Z]", "");
	}
}
