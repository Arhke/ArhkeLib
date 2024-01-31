package com.Arhke.ArhkeLib.Hook;


import at.pavlov.cannons.API.CannonsAPI;
import at.pavlov.cannons.Cannons;
import com.Arhke.ArhkeLib.Base.MainBase;
import com.earth2me.essentials.Essentials;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
//import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.tr7zw.nbtapi.plugin.NBTAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Hook extends MainBase<JavaPlugin> {
	private Essentials essentials;
	private Economy econ;
	private Permission perm;
//	private WorldGuard worldGuard;
	private WorldGuardPlugin wgPlugin;
	private CannonsAPI cannonsAPI;
	private Cannons cannons;
	private WorldEditPlugin worldEdit;
	private NBTAPI nbtapi;
	public Hook(JavaPlugin instance, Plugins... plugins){
		super(instance);
		List<Plugins> pluginsList = Arrays.asList(plugins);
		if (pluginsList.contains(Plugins.VAULT) && !setUpVault()){
			exceptDisable("Disabled due to no Vault Dependency Found");
		}
		if (pluginsList.contains(Plugins.PLACEHOLDERAPI)  && !setUpPlaceHolders()){
			exceptDisable("Disabled due to no PlaceHolderAPI Dependency Found");
		}
		if (pluginsList.contains(Plugins.WORLDGUARD)  && !setUpWorldGuard()){
			exceptDisable("Disabled due to no WorldGuard Dependency Found");
		}
		if (pluginsList.contains(Plugins.ESSSENTIALS)  && !setUpEssentials()) {
			exceptDisable("Disabled due to no Essentials Dependency Found");
		}
		if (pluginsList.contains(Plugins.WORLDEDIT)  && !setUpWorldEdit()) {
			exceptDisable("Disabled due to no WorldEdit Dependency Found");
		}
		if (pluginsList.contains(Plugins.NBTAPI)  && !setUpNBTAPI()) {
			exceptDisable("Disabled due to no NBTAPI Dependency Found");
		}
		if (pluginsList.contains(Plugins.CANNONS)  && !setUpCannons()) {
			exceptDisable("Disabled due to no Cannons Dependency Found");
		}
	}

	//==============<SetUp>==================
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
		return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	}
	boolean setUpEssentials() {
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
		this.wgPlugin = (WorldGuardPlugin) plugin;
//		this.worldGuard =  WorldGuard.getInstance();
		return true;
	}
	boolean setUpWorldEdit() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");

		if (!(plugin instanceof WorldEditPlugin)) {
			return false;
		}
		this.worldEdit = (WorldEditPlugin) plugin;
		return true;
	}
	boolean setUpNBTAPI(){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("NBTAPI");

		if (!(plugin instanceof NBTAPI)) {
			return false;
		}
		this.nbtapi = (NBTAPI) plugin;
		return true;
	}
	boolean setUpCannons() {
		Plugin plug = Bukkit.getPluginManager().getPlugin("Cannons");

		if (plug instanceof Cannons) {
			this.cannons = ((Cannons) plug);
			this.cannonsAPI = cannons.getCannonsAPI();
			return true;
		}
		return false;
	}
	//==================<Get>=====================
	public WorldEditPlugin getWorldEdit(){
		return worldEdit;
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
	 * @param uuid Player UUID
	 * @return the balance from the corresponding UUID
	 */
	public double getBalance(UUID uuid){
		OfflinePlayer offP = Bukkit.getOfflinePlayer(uuid);
		return this.getEcon().getBalance(offP);
	}
	/**
	 * Get balance from OfflinePlayer instance
	 * @param player OfflinePlayer
	 * @return the balance from the corresponding account from this player
	 */
	public double getBalance(OfflinePlayer player){
		return this.getEcon().getBalance(player);
	}
	public boolean hasAccount(OfflinePlayer player) {
		return this.econ.hasAccount(player);
	}
	public boolean depositMoney(OfflinePlayer player, double money) {
		return econ.depositPlayer(player, money).transactionSuccess();
	}
	public boolean withdrawMoney(OfflinePlayer player, double money){
		return econ.withdrawPlayer(player, money).transactionSuccess();
	}
	public String getCurrencyPlural() {
		return econ.currencyNamePlural();
	}

	//=============<Vault Perms>==========
	public Permission getPerms() {
		return this.perm;
	}
	public void addPermission(OfflinePlayer offp, String perm){
		this.perm.playerAdd(null, offp, perm);
	}
	public void removePermission(OfflinePlayer offp, String perm){
		this.perm.playerRemove(null, offp, perm);
	}

	//=============<PlaceholderAPI>==========
	public void registerPlaceholderAPI(String identifier, BiFunction<Player, String, String> translation){
		new Placeholder(getPlugin(), identifier){
			@Override
			public String onPlaceholderRequest(Player p, String params) {
				return translation.apply(p, params);
			}
		}.register();
	}
	//===============<WorldGuard Stuff>==========
//	public  boolean hasWG() {
//		return worldGuard!=null;
//	}
//	public boolean isInRegion(Location location, String name){
//		RegionManager rm = this.worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())));
//		if(rm == null)return false;
//		ProtectedRegion region = rm.getRegion(name);
//		if(region == null) return false;
//		return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//	}
//	public ProtectedRegion getRegion(World world, String name){
//		RegionManager rm = this.worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
//		if(rm == null)return null;
//		return rm.getRegion(name);
//	}
//	public boolean canPVP(Player p, Location location) {
//		return canWG(p, location, Flags.PVP);
//	}
//	public boolean canBuild(Player p, Location location) {
//		return canWG(p, location, Flags.BUILD);
//	}
//	public boolean canBuild(Player p, Block b) {
//		return canBuild(p, b.getLocation());
//	}
//	public boolean canWG(Player p, Location location, StateFlag... flags){
//		if(worldGuard.getPlatform().getSessionManager().hasBypass(wgPlugin.wrapPlayer(p), BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())))){
//			return true;
//		}
//		return worldGuard.getPlatform().getRegionContainer().createQuery().testState(BukkitAdapter.adapt(location), wgPlugin.wrapPlayer(p), flags);
//
//	}
	//==============<WorldEdit Stuff>=============
	public void setBlock(Location loc1, Location loc2, Material mat){
//		if (loc1.getWorld() == null || !Objects.equals(loc1.getWorld(), loc2.getWorld())) return;
//		BlockVector3 pos1 = BlockVector3.at(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ());
//		BlockVector3 pos2 = BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
//		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(loc1.getWorld());
//		CuboidRegion region = new CuboidRegion(world, pos1, pos2);
//		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
//		BlockType type = BlockTypes.AIR;
//		BlockState b = type.getDefaultState();
//		BaseBlock block = b.toBaseBlock();
//		try {
//			editSession.setBlocks(region, block);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
//	public BlockVector3 toBV3(Location loc){
//		return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//	}
//	public EditSession worldEditSession(World world){
//		return getWorldEdit().getWorldEdit().newEditSession(BukkitAdapter.adapt(world));
//	}
//	public boolean regen(Location loc1, Location loc2){
//		RegenOptions options = RegenOptions.builder()
////					.seed()
//				.regenBiomes(true)
//				.build();
//		bc(loc1.toString());
//		bc(loc2.toString());
//		return BukkitAdapter.adapt(Objects.requireNonNull(loc1.getWorld())).regenerate(new CuboidRegion(BukkitAdapter.adapt(loc1.getWorld()), toBV3(loc1), toBV3(loc2)),worldEditSession(loc1.getWorld()), options);
//	}
//

	public boolean isLandProtected(Player p, Chunk c) {
		int y = p.getLocation().getBlockY();
		if(c == null){
			return false;
		}
		try{
			Block[] blocks = new Block[]{c.getBlock(0, y, 0)
					, c.getBlock(15, y, 0)
					, c.getBlock(15, y, 15)
					, c.getBlock(0, y, 15)
					, c.getBlock(8, y, 8)
					, c.getBlock(8, c.getWorld().getSeaLevel(), 8)
					, c.getBlock(0, c.getWorld().getSeaLevel(), 0)
					, c.getBlock(15, c.getWorld().getSeaLevel(), 0)
					, c.getBlock(15, c.getWorld().getSeaLevel(), 15)
					, c.getBlock(0, c.getWorld().getSeaLevel(), 15)};
			for(Block b : blocks){
//				if(!canBuild(p, b)){
//					return true;
//				}
			}
		}catch(Exception e){
			err(e.toString());
			return false;
		}
		return false;
	}


	//==============<Essentials>=========
	public Essentials getEssentials(){
		return this.essentials;
	}
	public void essentialsTeleport(Player p, Location loc){
		try {
			this.essentials.getUser(p).getTeleport().teleport(loc,null, PlayerTeleportEvent.TeleportCause.COMMAND);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//==============<Cannons>=================
	public Cannons getCannons(){
		return this.cannons;
	}
	public CannonsAPI getCannonsAPI(){
		return this.cannonsAPI;
	}
	//target ability
//	public static boolean isTargeteableEntity(Entity entity){
//		return isTargeteableLivingEntity(entity) || entity instanceof Item;
//	}
//	public static boolean isTargeteableLivingEntity(Entity entity){
//		return entity instanceof Player && !entity.hasMetadata("NPC") && !isInProtectedRegion((Player)entity);
//	}
//	public static boolean isInProtectedRegion(Player player){
//		return !WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).allows(DefaultFlag.PVP);
//	}



}
