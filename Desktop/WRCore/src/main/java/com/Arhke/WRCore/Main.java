package com.Arhke.WRCore;

import com.Arhke.WRCore.District.Commands.RTPCommand;
import com.Arhke.WRCore.District.Commands.TownCommands;
import com.Arhke.WRCore.District.DistrictListeners;
import com.Arhke.WRCore.District.TUsers.TUserManager;
import com.Arhke.WRCore.District.kingdoms.*;
import com.Arhke.WRCore.District.util.NameTag;
import com.Arhke.WRCore.ItemSystem.CraftItem.CustomItemManager;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.CauldronFurniture;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.Smeltery;
import com.Arhke.WRCore.ItemSystem.Recipe.RecipeManager;
import com.Arhke.WRCore.ItemSystem.TechCommand;
import com.Arhke.WRCore.Lib.Base.CommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.WorldDataManager;
import com.Arhke.WRCore.Lib.CustomEvents.CustomEventListener;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Lib.GUI.GUIManager;
import com.Arhke.WRCore.Lib.Hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Paths;

public class Main extends JavaPlugin {

    ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();

    File kingdomsDataFolder, townDataFolder, tUserDataFolder, worldDataFolder,
            customItemFolder, recipeFolder;


    ConfigLoader configLoader;
    NameTag nameTag;

    //=============<Managers>=============
    private KingdomsManager kingdomsManager;
    private TownsManager townsManager;
    private TUserManager userManager;
    private WorldDataManager worldDataManager;
    private BattleManager battleManager;
    private CustomItemManager customItemManager;
    private RecipeManager recipeManager;
    private GUIManager guiManager;


    private Hook hook;
    private static Main plugin ;
    @Override
    public void onLoad(){

    }
    @Override
    public void onEnable() {
        plugin = this;
        consoleSender.sendMessage("[WRCore] " + ChatColor.GREEN + "WRCore started loading...");

        //registers all of the hooks
        hook = new Hook(this);

        //Initialize the paths of files
        initializeFilePath();

        //Load Config
        this.configLoader = new ConfigLoader(getPlugin());

        //Load Managers
        loadManagerData();

        //Register TownCommands
        initializeCommands();

        //register the event listeners
        initializeListeners();

//		api = new API(this);
        CustomEntityRegistry.registerCustomEntity(64, "Wither", IslandWither.class);
        new Hourly(this);
        consoleSender.sendMessage("[WRCore] " + ChatColor.GREEN + "WRCore is fully loaded!");
    }

    @Override
    public void onDisable() {
        //save everything to config
//        try {
//            for (Block b : .getMarks()) {
//                b.setType(Material.AIR);
//            }
//
//            for (Selection sel : selections) {
//                sel.getPlayer().closeInventory();
//            }
//            for (Player p : Bukkit.getOnlinePlayers()) {
//                p.closeInventory();
//            }
//            saveUsers(false);
//            saveKingdoms(false);
//            saveChallenges();
//
//            InventoryGui2.disable();
//
//            onlinePlayers.clear();
//        } catch (Exception e) {
//            ErrorManager.error(5, e);
//        }
        getBattleManager().cleanUpWithers();
        getTUserManager().saveAllUsers();
        getTownsManager().saveTowns();
        getKingdomsManager().saveKingdoms();
        getWorldDataManager().saveAllChunkData();
    }

    public static Main getPlugin() {
        return plugin;
    }
    public ConfigLoader getConfigLoader() {
        return this.configLoader;
    }
    public ConfigManager getConfig(ConfigLoader.ConfigFile cf){
        return this.configLoader.getConfigManager(cf);
    }
    public NameTag getNameTag(){
        return nameTag;
    }

    public KingdomsManager getKingdomsManager(){return this.kingdomsManager;}
    public TownsManager getTownsManager() {
        return townsManager;
    }
    public TUserManager getTUserManager() {
        return userManager;
    }
    public WorldDataManager getWorldDataManager() {
        return this.worldDataManager;
    }
    public BattleManager getBattleManager(){
        return this.battleManager;
    }
    public CustomItemManager getCustomItemManager() {return customItemManager;}
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
    public void reloadTech(){
        customItemManager = new CustomItemManager(this, new DirectoryManager(customItemFolder));
        recipeManager = new RecipeManager(this, new DirectoryManager(recipeFolder));
        File file = Paths.get(getPlugin().getDataFolder().toString(),"ItemSystem", "Materials.yml").toFile();
        FileManager fm = new FileManager(file);
        DataManager materials = fm.getDataManager();
        Smeltery.ValidMaterial.addAll(materials.buildMaterialSet("smeltery", "valid"));
        Smeltery.ExplodeMaterial.addAll(materials.buildMaterialSet("smeltery", "explode"));
        CauldronFurniture.ValidMaterial.addAll(materials.buildMaterialSet("tannery", "valid"));
        CauldronFurniture.ExplodeMaterial.addAll(materials.buildMaterialSet("tannery", "explode"));
    }
    public GUIManager getGUIManager(){
        return this.guiManager;
    }

    //==============<Hooks>=========
    public Hook getHook() {return hook;}


    public void loadManagerData() {
        this.kingdomsManager = new KingdomsManager(this, new DirectoryManager(kingdomsDataFolder));
        townsManager = new TownsManager(this, new DirectoryManager(this.townDataFolder));
        userManager = new TUserManager(this, new DirectoryManager(tUserDataFolder));
        worldDataManager = new WorldDataManager(this, new DirectoryManager(worldDataFolder));
        this.battleManager = new BattleManager(this);
        customItemManager = new CustomItemManager(this, new DirectoryManager(customItemFolder));
        recipeManager = new RecipeManager(this, new DirectoryManager(recipeFolder));
        this.guiManager = new GUIManager(this);
    }
    private void initializeFilePath() {
        this.kingdomsDataFolder = Paths.get(getDataFolder().toString(), "District", "kingdoms").toFile();
        this.townDataFolder = Paths.get(getDataFolder().toString(), "District", "towns").toFile();
        this.tUserDataFolder = Paths.get(getDataFolder().toString(), "District", "TUser").toFile();
        this.worldDataFolder = Paths.get(getDataFolder().toString(), "WorldData").toFile();
        this.customItemFolder = Paths.get(getDataFolder().toString(), "ItemSystem", "CustomItem").toFile();
        this.recipeFolder = Paths.get(getDataFolder().toString(), "ItemSystem", "Recipe").toFile();
    }
    private void initializeCommands() {
        ConfigManager townCmdConfig = getConfig(ConfigLoader.ConfigFile.TOWNCMDLANG),
                techCmdConfig = getConfig(ConfigLoader.ConfigFile.TECHCMDLANG),
                rtpCmdConfig = getConfig(ConfigLoader.ConfigFile.RTPCMDLANG);

        CommandsBase townCommand = new TownCommands(this, "town", townCmdConfig);
        CommandsBase techCommand = new TechCommand(this, "tech", techCmdConfig);
        CommandsBase rtpCommand = new RTPCommand(this, "rtp", rtpCmdConfig);
        townCmdConfig.getFM().save();
        getCommand(techCommand.getCmd()).setExecutor(techCommand);
        getCommand(townCommand.getCmd()).setExecutor(townCommand);
        getCommand(rtpCommand.getCmd()).setExecutor(rtpCommand);
    }
    private void initializeListeners() {
        ConfigManager districtListenerLang = getConfig(ConfigLoader.ConfigFile.DLISTENERLANG);
        ConfigManager itemSystemListenerLang = getConfig(ConfigLoader.ConfigFile.ILISTENERLANG);
        getServer().getPluginManager().registerEvents(new CustomEventListener(this), this);
        getServer().getPluginManager().registerEvents(new DistrictListeners(this, districtListenerLang), this);
        getServer().getPluginManager().registerEvents(new Listeners(this, itemSystemListenerLang), this);
        Bukkit.getPluginManager().registerEvents(nameTag = new NameTag(this), this);

    }


}

