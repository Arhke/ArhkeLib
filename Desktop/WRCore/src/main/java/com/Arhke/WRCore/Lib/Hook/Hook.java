package com.Arhke.WRCore.Lib.Hook;


import at.pavlov.cannons.API.CannonsAPI;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Main;
import com.Arhke.WRCore.PlaceHolder;
import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class Hook extends MainBase {
	private Essentials essentials;
	private Economy econ;
	private Permission perm;
	private PlaceHolder ph;
	private WorldGuardPlugin worldGuard;
	private CannonsAPI cannonsAPI;
	private Cannons cannons;
	public Hook(Main Instance){
		super(Instance);
		if (!setUpVault()){
			exceptDisable("Disabed due to no Vault Dependency Found");
		}if (!setUpPlaceHolders()){
			exceptDisable("Disabed due to no PlaceHolderAPI Dependency Found");
		}
		if (!setUpWorldGuard()){
			exceptDisable("Disabled due to no WorldGuard Dependency Found");
		}if (!loadCannonsAPI()) {
			exceptDisable("Disabled due to no Cannons Dependency Found");
		}if (!setUpEssentials()) {
			exceptDisable("Disabled due to no Cannons Dependency Found");
		}
	}
	boolean setUpVault() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null){
			return false;
		}

		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			econ = economyProvider.getProvider();
		}
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			perm = permissionProvider.getProvider();
		}
		return econ != null && perm != null;
	}
	boolean setUpPlaceHolders() {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null){
			return false;
		}
		ph = new PlaceHolder(getPlugin());
		ph.register();
		return true;
	}boolean setUpEssentials() {
		if(Bukkit.getPluginManager().getPlugin("Essentials") == null){
			return false;
		}
		this.essentials = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
		return true;
	}
	boolean setUpWorldGuard() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

		if (!(plugin instanceof WorldGuardPlugin)) {
			return false;
		}
		this.worldGuard = (WorldGuardPlugin) plugin;
		return true;
	}
	boolean loadCannonsAPI() {
		Plugin plug = Bukkit.getPluginManager().getPlugin("Cannons");

		if (plug instanceof Cannons) {
			this.cannons = ((Cannons) plug);
			this.cannonsAPI = cannons.getCannonsAPI();
			return true;
		}
		return false;
	}


	//===============<Economy Tools>=================
	/**
	 * @return Get the economy instance
	 */
	public Economy getEcon(){
		return this.econ;
	}
	/**
	 * Get balance from UUID
	 * @param uuid
	 * @return the balance from the corresponding UUID
	 */
	public double getBalance(UUID uuid){
		OfflinePlayer offP = Bukkit.getOfflinePlayer(uuid);
		return this.getEcon().getBalance(offP);
	}
	/**
	 * Get balance from OfflinePlayer instance
	 * @param player
	 * @return the balance from the corresponding account from this player
	 */
	public double getBalance(OfflinePlayer player){
		return this.getEcon().getBalance(player);
	}
	public boolean hasAccount(OfflinePlayer player) {
		return this.econ.hasAccount(player);
	}
	public EconomyResponse depositMoney(OfflinePlayer player, double money) {
		return econ.depositPlayer(player, money);
	}
	public EconomyResponse withdrawMoney(OfflinePlayer player, double money){
		return econ.withdrawPlayer(player, money);
	}
	public String getCurrencyPlural() {
		return econ.currencyNamePlural();
	}

	//=============<Vault Perms>==========
	public Permission getPerms() {
		return this.perm;
	}
	public void addPermission(Player player, String perm){
		this.perm.playerAdd(player, perm);
	}
	public void removePermission(Player player, String perm){
		try{
			this.perm.playerRemove(player, perm);
		}catch(Exception e){

		}
	}

	//===============<WorldGuard Stuff>==========
	public  boolean hasWG() {
		return worldGuard!=null;
	}
	public  boolean canBuild(Player p, Block b) {
		return worldGuard.canBuild(p, b);
	}
	public  boolean canBuild(Player p, Location location) {
		return worldGuard.canBuild(p, location);
	}
	public boolean isLandProtected(Player p, ChunkData cData) {
		Chunk c = cData.getWorld().getChunkAt(cData.getX(), cData.getZ());
		int y = p.getLocation().getBlockY();
		if(c == null){
			return false;
		}
		try{
			Block blocks[] = new Block[]{c.getBlock(0, y, 0)
					, c.getBlock(15, y, 0)
					, c.getBlock(15, y, 15)
					, c.getBlock(0, y, 15)
					, c.getBlock(8, y, 8)
					, c.getBlock(8, c.getWorld().getSeaLevel(), 8)
					, c.getBlock(0, c.getWorld().getSeaLevel(), 0)
					, c.getBlock(15, c.getWorld().getSeaLevel(), 0)
					, c.getBlock(15, c.getWorld().getSeaLevel(), 15)
					, c.getBlock(0, c.getWorld().getSeaLevel(), 15)};
			boolean containsOcean = true;
			for(Block b : blocks){
				if(b != null){
					if (!b.getBiome().name().contains("OCEAN")){
						containsOcean = false;
					}
					if(!canBuild(p, b)){
						return true;
					}
				}
			}
			if (containsOcean){
				return true;
			}
		}catch(Exception e){
			ErrorManager.error(37, e);
			return false;
		}
		return false;
	}

	//==============<Cannons Stuff>============
	public Cannons getCannons(){
		return this.cannons;
	}
	public CannonsAPI getCannonsAPI(){
		return this.cannonsAPI;
	}
	public Cannon getCannon(UUID uuid){
		return this.cannonsAPI.getCannon(uuid);
	}

	//==============<Essentials>=========
	public Essentials getEssentials(){
		return this.essentials;
	}





}
