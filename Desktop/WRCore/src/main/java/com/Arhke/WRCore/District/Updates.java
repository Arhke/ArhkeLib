package com.Arhke.WRCore.District;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/**
 * Checks for plugin update
 * 
 * @author Michael Forseth
 * @version 9.10.17
 */
public class Updates extends Thread implements Listener {

	private static Updates updates;
	private boolean updated = true;
	private String name;
	private String id;
	private String version;
	private String newVersion = "Unknown";
	
	private Updates(String name, String id, String version) {
		this.name = name;
		this.id = id;
		this.version = version;
	}
	
	public void run() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id);
			URLConnection connection = url.openConnection();
			Scanner scan = new Scanner(connection.getInputStream());
			if(scan.hasNextLine()) {
				newVersion = scan.nextLine();
			}
			scan.close();
			
			if(newVersion.equals("Unknown")) {
				return;
			}else {
				if(!newVersion.equals(version)) {
					updated = false;
					Bukkit.getConsoleSender().sendMessage(
							"�c["+name+"] has an update available. Current version: " + version + 
							" New version: " + newVersion + " - Download at: https://www.spigotmc.org/resources/" + id);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void join(PlayerJoinEvent event) {
		if(updates != null && !updates.updated && event.getPlayer().isOp()) {
			event.getPlayer().sendMessage("�6�l" + name + 
					" �dis outdated! Download: �f�lhttps://www.spigotmc.org/resources/" + id);
		}
	}
	
	public static void checkUpdates(Plugin plugin, String name, String id) {
		if(updates != null) {
			return;
		}
		updates = new Updates(name, id, plugin.getDescription().getVersion());
		plugin.getServer().getPluginManager().registerEvents(updates, plugin);
		updates.start();
	}
	
}
