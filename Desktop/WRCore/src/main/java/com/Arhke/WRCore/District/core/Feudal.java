package com.Arhke.WRCore.District.core;

import com.Arhke.WRCore.District.Commands.TownCommands;
import com.Arhke.WRCore.District.configs.Configuration;
import com.Arhke.WRCore.District.kingdoms.Battle;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * NEEDED LIBRARIES:
 * Spigot 1.8
 * Spigot 1.9
 * Spigot 1.9.4
 * Spigot 1.10
 * Spigot 1.11
 * Vault
 * WorldEdit
 * WorldGuard
 *
 */

/**
 * The main class and starting point for this plugin. I (the author) am aware
 * that the java documentation is incorrect and does not describe all contents
 * of this project completely. For example, return and param descriptions have
 * not been made. If time permits, updates may be added to that aspect of this
 * java doc. However, this java doc still provides information which can still
 * be used.
 *
 * @author Michael Forseth
 *
 */
public class Feudal extends MainBase {
	public static final Random RANDOM = new Random();
	private static File pluginFolder;

	private static Configuration config;
	private static Configuration commandConfig;
	private static Configuration challengesConfig;
	private static Configuration language;
//	private static FeudalAPI api;
	private static File kingdomsFolder;
	private static File dataFolder;
	private static File playerFolder;

	private volatile static ArrayList<Battle> battles = new ArrayList<Battle>();
	private static ArrayList<Configuration> kingdomConfigs = new ArrayList<Configuration>();
	private static ArrayList<Configuration> ncpConfigs = new ArrayList<Configuration>();
//	private static ArrayList<Selection> selections = new ArrayList<Selection>();
//	private static ArrayList<TrackPlayer> trackPlayers = new ArrayList<TrackPlayer>();
	private static ArrayList<Kingdom> kingdoms = new ArrayList<Kingdom>();
	private static String minecraftVersion = "1.12";
	private static TownCommands commands;

	private static volatile List<String> saving = Collections.synchronizedList(new ArrayList<String>());

	private static final String pluginName = "Feudal";
	public static Feudal _feudal;



	public Feudal(Main Instance) {
		super(Instance);
	}

	public void onDisable() {

	}

	@SuppressWarnings("deprecation")
//	public void onEnable() {
//		_feudal = this;
//		minecraftVersion = Bukkit.getVersion();
//		if (minecraftVersion.contains("MC: 1.12")) {
//			minecraftVersion = "1.12";
//		} else {
//			Feudal.error("This plugin can not run on this version. You must use minecraft 1.7.x, 1.8.x, 1.9, 1.9.4, 1.10, 1.11, or 1.12");
//			Feudal.error("Disabling");
//			Bukkit.getPluginManager().disablePlugin(getPlugin());
//			return;
//		}
//
//		Base.info("Enabling Feudal for minecraft " + minecraftVersion);
//
//
//
//
//
//		if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
//			if (!Bukkit.getServer().getPluginManager()
//					.isPluginEnabled("WorldGuard")) {
//				Bukkit.getPluginManager().enablePlugin(
//						Bukkit.getPluginManager().getPlugin("WorldGuard"));
//			}
//			WGUtils.setupWG();
//		}
//
//		try {
//			if (Configs.CreateConfigs()) {
//				info("Feudal configs have been successfully loaded!");
//			}
//		} catch (Exception e) {
//			Feudal.error("There was a problem while loading the Feudal configs.");
//			e.printStackTrace();
//		}
//
//		ErrorManager.load();
//
//		try {
//			PluginManager m = getPlugin().getServer().getPluginManager();
//			m.registerEvents(new EventManager(), getPlugin());
//			if(!getVersion().contains("1.7") && !getVersion().contains("1.8")) {
//				m.registerEvents(new EventManager1_8Plus(), getPlugin());
//			}
//
//			// register commands
//			commands = new TownCommands();
//			getPlugin().getCommand("feudal").setExecutor(commands);
//
//			// Load all kingdoms
//			for (File kingdomFile : kingdomsFolder.listFiles()) {
//				Configuration config = new Configuration(kingdomFile);
//				try {
//					config.loadConfig();
//				} catch (Exception e) {
//					try {
//						config.broke();
//					} catch (Exception e1) {
//						ErrorManager.error(6, e);
//						System.out
//								.println("[Feudal] FAILED TO SAVE DATA FOR BROKEN FILE: "
//										+ kingdomFile.getName());
//					}
//					ErrorManager.error(25, e);
//					continue;
//				}
//				Kingdom king = null;
//				try {
//					king = new Kingdom(config);
//				} catch (Exception e) {
//					ErrorManager.error(7, e);
//					System.out
//							.println("[Feudal] Failed to load kingdom from config: "
//									+ kingdomFile.getName());
//					continue;
//				}
//
//				// Does not load kingdom if the leader is missing. Sorry..
//				boolean leader = false;
//				for (String s : king.getMembers().keySet()) {
//					if (king.getMembers().get(s).equals(Rank.LEADER)) {
//						leader = true;
//					}
//				}
//				if (leader) {
//					kingdoms.add(king);
//					king.updateLand();
//				}
//				//
//			}
//
//			// Load battles
//			for (String challengeStr : Feudal.getChallengesConfig().getConfig()
//					.getStringList("battles")) {
//				Battle c = null;
//				try {
//					c = new Battle(challengeStr);
//				} catch (Exception e) {
//					ErrorManager.error(8, e);
//					continue;
//				}
//				if (c.getAttacker() != null && c.getDefender() != null) {
//					battles.add(c);
//				}
//			}
//			//
//
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				Feudal.loadPlayer(p);
//			}
//
//			Effect.saturationRegain();
//
//			ScheduledTasks.startScheduler();
//
//			try {
//				Metrics metrics = new Metrics(getPlugin());
//				metrics.start();
//			} catch (IOException e) {
//				ErrorManager.error(9, e);
//			}
//
//			InventoryGui2.enable(getPlugin());
//
//			// Make a recipe for the op gold apple (This is restricted to be
//			// crafted by healers only) (Configurable)
//			if (Feudal.getConfiguration().getConfig()
//					.getBoolean("Healer.canCraftGodApple")) {
//				ShapedRecipe godApple = new ShapedRecipe(new ItemStack(
//						Material.GOLDEN_APPLE, 1, (short) 1));
//				godApple.shape("***", "*%*", "***");
//				godApple.setIngredient('*', Material.GOLD_BLOCK);
//				godApple.setIngredient('%', Material.APPLE);
//				Bukkit.addRecipe(godApple);
//			}
//			//
//
//
//			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
//				new FeudalPlaceholder(getPlugin()).hook();
//				Feudal.log("Placeholders loaded for PlaceholderAPI");
//			}
//
//		} catch (Exception e) {
//			ErrorManager.error(4, e);
//		}
//
//		String commandOri = null;
//		String[] aliases = null;
//		for(String s : Bukkit.getCommandAliases().keySet()) {
//			if(s.equalsIgnoreCase("feudal")) {
//				aliases = Bukkit.getCommandAliases().get(s);
//				commandOri = s;
//			}
//		}
//		String[] newAliases = null;
//		List<String> customCommands = Feudal.getConfiguration().getConfig().getStringList("customCommands");
//		try {
//			if(aliases != null) {
//					newAliases = new String[aliases.length+customCommands.size()];
//					for(int i = 0; i < aliases.length; i++) {
//						newAliases[i] = aliases[i];
//					}
//					for(int i = aliases.length; i < newAliases.length; i++) {
//						newAliases[i] = customCommands.remove(0);
//					}
//			}else {
//				newAliases = customCommands.toArray(new String[customCommands.size()]);
//			}
//		}catch(Exception e) {
//			ErrorManager.error(1413222018, e);
//		}
//		if(commandOri != null && newAliases != null) {
//			Bukkit.getCommandAliases().put(commandOri, newAliases);
//		}
//
//		ScheduledTasks.startKingdomSaveTimer();
//
////		api = new FeudalAPICore();
//		Updates.checkUpdates(getPlugin(), "Feudal", "22873");
//	}

	/**
	 *
	 * Get the running instance of FeudalAPI which can be used for API implementation.
	 *
	 * @return Current instance for FeudalAPI
	 */
//	public static FeudalAPI getAPI(){
//		return api;
//	}


	/**
	 * Get the main plugin folder
	 *
	 * @return The plugin folder
	 */
	public static File getPluginFolder() {
		return pluginFolder;
	}

	/**
	 * Set the plugin folder
	 *
	 * @param pluginFolder
	 *            The plugin folder
	 */
	public static void setPluginFolder(File pluginFolder) {
		Feudal.pluginFolder = pluginFolder;
	}

	public static Configuration getCommandConfig() {
		return commandConfig;
	}

	public static void setCommandConfig(Configuration config) {
		commandConfig = config;
	}

	/**
	 * Get the main config Configuration
	 *
	 * @return The main configuration
	 */
	public static Configuration getConfiguration() {
		return config;
	}

	/**
	 * Set the main config file Configuration
	 *
	 * @param config
	 *            The main configuration
	 */
	public static void setConfiguration(Configuration config) {
		Feudal.config = config;
	}



	/**
	 * Get array list of kingdom configs
	 *
	 * @return List of kingdom configs
	 */
	public static ArrayList<Configuration> getKingdomConfigs() {
		return kingdomConfigs;
	}

	/**
	 * Set the kingdom configs arraylist
	 *
	 * @param kingdomConfigs
	 *            List of kingdom configs
	 */
	public static void setKingdomConfigs(ArrayList<Configuration> kingdomConfigs) {
		Feudal.kingdomConfigs = kingdomConfigs;
	}






	/**
	 * Load a player. Creates a new config if they do not have a player data.
	 *
	 * @param player
	 *            The player
	 */











	/**
	 * Get array list of all active selections (Player setup menus)
	 *
	 * @return list of slection menus
	 */
//	public static ArrayList<Selection> getSelections() {
//		return selections;
//	}



	/**
	 * Get language config.
	 *
	 * @return language config
	 */
	public static Configuration getLanguage() {
		return language;
	}

	public static void setLanguage(Configuration language) {
		Feudal.language = language;
	}








//	/**
//	 * Get all TrackPlayers in a list. There are the menus for the compass
//	 * tracker.
//	 *
//	 * @return list of open track player menus
//	 */
//	public static ArrayList<TrackPlayer> getTrackPlayers() {
//		return trackPlayers;
//	}




	/**
	 * Converts long to: MM.dd.yy hh:mm a
	 *
	 * @param playerTime
	 *            Time in ms
	 * @return Time as string MM.dd.yy hh:mm a
	 */
	public static String niceTime(long playerTime) {
		return new SimpleDateFormat("MM.dd.yy hh:mm a").format(new Date(
				playerTime));
	}











//
//	/**
//	 * Get item in main hand for 1.9 and 1.8 mc
//	 *
//	 * @param p
//	 * @return
//	 */
//	@SuppressWarnings("deprecation")
//	public static ItemStack getItemInHand(Player p) {
//		if (!Feudal.getVersion().contains("1.8") && !Feudal.getVersion().contains("1.7")) {
//			return Utils1_9.getItemInHand(p.getInventory());
//		} else {
//			return p.getItemInHand();
//		}
//	}
//
//	/**
//	 * Set item in main hand for 1.9 and 1.8 mc
//	 *
//	 * @param p
//	 * @param item
//	 */
//	@SuppressWarnings("deprecation")
//	public static void setItemInHand(Player p, ItemStack item) {
//		if (!Feudal.getVersion().contains("1.8") && !Feudal.getVersion().contains("1.7")) {
//			Utils1_9.setItemInHand(p.getInventory(), item);
//		} else {
//			p.setItemInHand(item);
//		}
//	}

	/**
	 * Check if a kingdom name is available
	 *
	 * @param kingdom
	 * @param string
	 * @return
	 */
	public static boolean canUseKingdomName(Kingdom kingdom, String string) {
		string = string.toLowerCase();
		for (Kingdom king : Feudal.kingdoms) {
			if (!king.equals(kingdom)) {
				if (king.getName().toLowerCase().equals(string)) {
					return false;
				}
			}
		}
		return true;
	}


	/*
	 * public static boolean playerFileExists(String string) { File f = new
	 * File(Main.playerFolder.getAbsolutePath() + "/" + string + ".yml");
	 * if(f.exists()){ return true; }else{ return false; } }
	 */

	/*
	 * public static void loadWorld(String w) { World world =
	 * Bukkit.getWorld(w); if(world == null){ WorldCreator creator = new
	 * WorldCreator(w); creator.environment(World.Environment.NORMAL);
	 * creator.generateStructures(true); world = creator.createWorld(); } }
	 */


//	private void saveUsers(boolean async) {
//		for(TUser user : onlinePlayers.values()) {
//			try {
//				user.save(async);
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public TownCommands getCommands() {
		return commands;
	}



}
