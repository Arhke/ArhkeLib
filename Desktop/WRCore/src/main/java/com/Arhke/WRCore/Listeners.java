package com.Arhke.WRCore;

import com.Arhke.WRCore.District.kingdoms.IslandWither;
import com.Arhke.WRCore.ItemSystem.CraftItem.CustomItem;
import com.Arhke.WRCore.ItemSystem.Enumeration.MatchType;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.*;
import com.Arhke.WRCore.ItemSystem.Recipe.Recipe;
import com.Arhke.WRCore.ItemSystem.utils.Utility;
import com.Arhke.WRCore.ItemSystem.utils.WrappedLocation;
import com.Arhke.WRCore.Lib.ArmorTags;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.GUI.InventoryGui;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldguard.blacklist.event.BlockPlaceBlacklistEvent;
import com.sun.istack.internal.NotNull;
import de.tr7zw.itemnbtapi.NBTItem;
import io.github.eirikh1996.structureboxes.Direction;
import io.github.eirikh1996.structureboxes.Structure;
import io.github.eirikh1996.structureboxes.StructureBoxes;
import io.github.eirikh1996.structureboxes.StructureManager;
import io.github.eirikh1996.structureboxes.localisation.I18nSupport;
import io.github.eirikh1996.structureboxes.settings.Settings;
import io.github.eirikh1996.structureboxes.utils.IWorldEditLocation;
import io.github.eirikh1996.structureboxes.utils.ItemManager;
import io.github.eirikh1996.structureboxes.utils.RegionUtils;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.CraftType;
import net.countercraft.movecraft.craft.ICraft;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.countercraft.movecraft.utils.MathUtils;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Cauldron;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static com.Arhke.WRCore.ItemSystem.CraftItem.CustomItem.NBTIMaterial;
import static io.github.eirikh1996.structureboxes.utils.ChatUtils.COMMAND_PREFIX;

public class Listeners extends MainBase implements Listener{
	private final HashMap<UUID, Long> playerTimeMap = new HashMap<>();
	public static final String NBTIResult = "Result", NBTIRecipe = "Recipe", NBTIAmountMatches = "Amount";
	DataManager dm;
	public Listeners(Main Instance, DataManager dm){
		super(Instance);
		this.dm = dm;
		schematicDir = StructureBoxes.getInstance().getWorldEditPlugin().getConfig().getConfigurationSection("saving").getString("dir");

	}
	@EventHandler
	public void onEvent(InventoryClickEvent ice){
		InventoryGui ig = getPlugin().getGUIManager().get(ice.getWhoClicked().getUniqueId());
		if(ig != null){
			ig.onPress(ice);
		}
	}
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		InventoryGui ig = getPlugin().getGUIManager().remove(event.getPlayer().getUniqueId());
		if(ig != null){
			ig.onClose(event);
		}
	}
	@EventHandler
	public void onOpen(InventoryOpenEvent event){
		InventoryGui ig = getPlugin().getGUIManager().get(event.getPlayer().getUniqueId());
		if(ig != null){
			ig.onOpen(event);
		}
	}

	@EventHandler(ignoreCancelled = true)
	@SuppressWarnings("deprecation")
	public void onBlockClick(PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if(event.getHand()!= EquipmentSlot.HAND) return;
			if(player.isSneaking()){
				return;
			}
			ItemStack is = player.getInventory().getItemInMainHand();
			Location loc = event.getClickedBlock().getLocation();
			if (is.getType() == Material.LAVA_BUCKET && new NBTItem(is).hasKey(NBTIResult)) {
				if(event.getClickedBlock().getType() != Material.CAULDRON && !isCauldron(loc))
					player.sendMessage(this.dm.getTCM("moltenMetalPlace"));
				event.setCancelled(true);
			}
			ChunkData chunkData = getPlugin().getWorldDataManager().getOrNewChunkData(loc);
			if (isSmeltery(loc)) {
				if (is.getType() == Material.AIR) {
					Furniture furniture = chunkData.getSmeltery(loc);
					if (furniture == null) {
						player.sendMessage(this.dm.getTCM("smeltery.notSmelting"));
					} else {
						player.sendMessage(this.dm.getTCM("smeltery.isSmelting"));
					}
					event.setCancelled(true);
					return;
				}
				if (is.getType() == Material.WATCH) {
					Furniture furniture = chunkData.getSmeltery(loc);
					if (furniture == null) player.sendMessage(this.dm.getTCM("smeltery.notSmelting"));
					else player.sendMessage(this.dm.getTCM("smeltery.smeltTimer",(int) (furniture.getTimePassed() / 60)));
					event.setCancelled(true);
					return;
				}
				if (is.getType() == Material.BUCKET) {
					Smeltery furniture = chunkData.getSmeltery(loc);
					if (furniture == null) {
						player.sendMessage(this.dm.getTCM("smeltery.notSmelting"));
						event.setCancelled(true);
						return;
					}
					boolean addToPlayer = false;
					if(is.getAmount() == 1){
						is.setType(Material.LAVA_BUCKET);
					} else {
						itemAmountMinus(is);
						is = new ItemStack(Material.LAVA_BUCKET, 1);
						addToPlayer = true;
					}
					setDisplayName(is, "&aMoltenMetal");
					NBTItem nbti = new NBTItem(is);
					ItemStack itemStack = furniture.getOutputItem();
					if (itemStack == null) {
						nbti.setString(NBTIResult, MatchType.NONE.name());
					} else {
						nbti.setString(NBTIResult, itemToString(itemStack));
					}
					is.setItemMeta(nbti.getItem().getItemMeta());
					chunkData.removeFurniture(furniture.getLocation());
					if (addToPlayer) addItemtoPlayer(player, is);

					event.setCancelled(true);
					return;
				}
				if (Smeltery.ValidMaterial.contains(is.getType())) {
					Furniture furniture = chunkData.getOrNewFurniture(loc, FurnitureType.SMELTERY);
					ItemStack item = new ItemStack(is);
					item.setAmount(1);
					furniture.addItem(item);
					itemAmountMinus(is);
					event.setCancelled(true);
					return;
				}
				if (Smeltery.ExplodeMaterial.contains(is.getType())) {
					chunkData.removeFurniture(loc);
					itemAmountMinus(is);
					loc.getWorld().createExplosion(loc, this.dm.getInt(5, "smeltery.explodeIntensity"));
					event.setCancelled(true);
					return;
				}
				player.sendMessage(this.dm.getTCM("smeltery.doesNotFit"));
				event.setCancelled(true);
			}
			else if (event.getClickedBlock().getType() == Material.CAULDRON) {

				if (isCauldron(loc)) {
					if (is.getType() == Material.AIR) {
						Furniture furniture = chunkData.getCauldron(loc);
						if (furniture == null) {
							player.sendMessage(this.dm.getTCM("cauldron.empty"));
						} else {
							player.sendMessage(this.dm.getTCM("cauldron.notEmpty"));
						}
						event.setCancelled(true);
						return;
					}
					if (is.getType() == Material.RABBIT_HIDE) {
						CauldronFurniture furniture = chunkData.getCauldron(loc);
						if (furniture == null) {
							player.sendMessage(this.dm.getTCM("cauldron.empty"));
							event.setCancelled(true);
							player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_EMPTY, 3, 3);
							return;
						}
						itemAmountMinus(is);
						is = new ItemStack(Material.RABBIT_HIDE, 1);
						setDisplayName(is, "&dTreated Hide");
						is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						Collection<Recipe> recipes = furniture.getRecipe();
						StringBuilder recipeString = new StringBuilder();
						recipes.forEach((e)->recipeString.append(e.getId()+" "));
						NBTItem nbti = new NBTItem(is);
						nbti.setString(NBTIRecipe, recipeString.toString().trim());
						nbti.setBoolean(NBTIAmountMatches, furniture.amountMatches());
						ArmorTags at = new ArmorTags(nbti);
						at.hideFlags(ArmorTags.Flag.ENCHANTMENTS);
						is.setItemMeta(at.getItem().getItemMeta());
						addItemtoPlayer(event.getPlayer(),is);
						chunkData.removeFurniture(furniture.getLocation());
						event.setCancelled(true);
						return;
					}
					if (CauldronFurniture.ValidMaterial.contains(is.getType())) {
						Furniture furniture = chunkData.getOrNewFurniture(loc, FurnitureType.CAULDRON);
						ItemStack item = new ItemStack(is);
						item.setAmount(1);
						furniture.addItem(item);
						itemAmountMinus(is);
						event.setCancelled(true);
						player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 3, 3);
						return;
					}
					if (CauldronFurniture.ExplodeMaterial.contains(is.getType())) {
						chunkData.removeFurniture(loc);
						itemAmountMinus(is);
						loc.getWorld().createExplosion(loc, this.dm.getInt(5, "cauldron.explodeIntensity"));
						event.setCancelled(true);
						return;
					}
					player.sendMessage(this.dm.getTCM("cauldron.doesNotFit"));
					event.setCancelled(true);
				}
				else {
					NBTItem nbti;
					if (is.getType() == Material.LAVA_BUCKET && (nbti = new NBTItem(is)).hasKey(NBTIResult)) {
						Cauldron cauldron = (Cauldron) event.getClickedBlock().getState().getData();
						if (cauldron.isEmpty()) {
							event.getPlayer().sendMessage(this.dm.getTCM("coolant.empty"));
							return;
						}
						BlockState cauldronState = event.getClickedBlock().getState();
						cauldronState.getData().setData((byte) (cauldron.getData() - 1));
						cauldronState.update();
						String itemString = nbti.getString(NBTIResult);
						if (itemString.equals(MatchType.NONE.name())) {
							event.getPlayer().sendMessage(this.dm.getTCM("coolant.dust"));
						} else {
							ItemStack itemStack = stringToItem(itemString);
							if (itemStack == null) {
								event.getPlayer().sendMessage(this.dm.getTCM("coolant.crystallize"));
								itemAmountMinus(is);
								return;
							} else {
								event.getPlayer().sendMessage(this.dm.getTCM("coolant.harden"));
								addItemtoPlayer(player, itemStack);
							}
						}
						event.getPlayer().setItemInHand(new ItemStack(Material.BUCKET, 1));


					}
				}
			}
			else if (isTannery(loc) ) {
				if (is.getType() == Material.WATCH) {
					Furniture furniture = chunkData.getTannery(loc);
					if (furniture == null) player.sendMessage(this.dm.getTCM("tannery.notDrying"));
					else player.sendMessage(this.dm.getTCM("tannery.dryTimer",(int) (furniture.getTimePassed() / 60)));
					event.setCancelled(true);
					return;
				}
				NBTItem nbti;
				if(is.getType() == Material.RABBIT_HIDE && (nbti = new NBTItem(is)).hasKey(NBTIRecipe)){
					Tannery furniture = chunkData.getTannery(loc);
					if (furniture != null) {
						player.sendMessage(this.dm.getTCM("tannery.isDrying"));
						event.setCancelled(true);
						return;
					}
					itemAmountMinus(is);
					furniture = (Tannery)chunkData.getOrNewFurniture(loc, FurnitureType.TANNERY);
					furniture.addPossibleRecipes(nbti.getString(NBTIRecipe));
					furniture.setAmountMatches(nbti.getBoolean(NBTIAmountMatches));
					Block block = event.getClickedBlock().getRelative(BlockFace.UP);
					block.setType(Material.STANDING_BANNER);
					Banner banner = (Banner) block.getState();
					banner.setBaseColor(DyeColor.BROWN);
					((Directional) banner.getData()).setFacingDirection(
							com.Arhke.WRCore.Lib.Utils.Direction.
									directionOf(event.getPlayer().getEyeLocation().getYaw()+180).getBlockFace());
					banner.update();
					event.setCancelled(true);
					return;
				}
				Furniture furniture = chunkData.getTannery(loc);
				if (furniture == null) {
					player.sendMessage(this.dm.getTCM("tannery.notDrying"));
				} else {
					player.sendMessage(this.dm.getTCM("tannery.isDrying"));
				}
				event.setCancelled(true);
				return;
			}
		}
		else if (event.getAction() == Action.RIGHT_CLICK_AIR){
			if(event.getHand()!= EquipmentSlot.HAND) return;

			final UUID id = event.getPlayer().getUniqueId();
			if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Settings.StructureBoxItem) &&
					event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null ||
					!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
				Bukkit.broadcastMessage(Settings.StructureBoxItem+"");
				return;
			}
			List<String> lore = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore();
			assert lore != null;
			String schematicID = ChatColor.stripColor(lore.get(0));
			if (!schematicID.startsWith(ChatColor.stripColor(Settings.StructureBoxPrefix))) {
				boolean hasAlternativePrefix = false;
				for (String prefix : Settings.AlternativePrefixes) {
					if (!schematicID.startsWith(prefix)) {
						continue;
					}
					hasAlternativePrefix = true;
					schematicID = schematicID.replace(prefix, "");
					break;
				}
				if (!hasAlternativePrefix) {
					return;
				}
			} else {
				schematicID = schematicID.replace(ChatColor.stripColor(Settings.StructureBoxPrefix), "");
			}
			if (Settings.RequirePermissionPerStructureBox && !event.getPlayer().hasPermission("structureboxes.place." + schematicID)) {
				event.getPlayer().sendMessage(String.format(COMMAND_PREFIX + I18nSupport.getInternationalisedString("Place - No permission for this ID"), schematicID));
				return;
			}
			if (playerTimeMap.containsKey(id) && playerTimeMap.get(id) != null && (System.currentTimeMillis() - playerTimeMap.get(id)) < Settings.PlaceCooldownTime) {
				event.getPlayer().sendMessage(COMMAND_PREFIX + I18nSupport.getInternationalisedString("Place - Cooldown"));
				return;
			}
			Clipboard clipboard = StructureBoxes.getInstance().getWorldEditHandler().loadClipboardFromSchematic(new BukkitWorld(event.getPlayer().getWorld()), schematicID);
			if (clipboard == null && schematicID.endsWith("_#")) {
				final String start = schematicID.replace("#", "");
				File schemDir = StructureBoxes.getInstance().getWorldEditHandler().getSchemDir();
				final String[] foundFiles = schemDir.list((file, name) ->
						(name.endsWith(".schematic") || name.endsWith(".schem")) &&
								name.startsWith(start) &&
								isInteger(name.replace(start, "").replace(".schematic", "").replace(".schem", ""))
				);
				if (foundFiles.length == 0)
					return;
				final Random random = new Random();
				String schemID = foundFiles[random.nextInt(foundFiles.length)].replace(".schematic", "").replace(".schem", "");
				clipboard = StructureBoxes.getInstance().getWorldEditHandler().loadClipboardFromSchematic(new BukkitWorld(event.getPlayer().getWorld()), schemID);
			}
			if (clipboard == null) {
				return;
			}
			Craft oldCraft = CraftManager.getInstance().getCraftByPlayerName(event.getPlayer().getName());
			if (oldCraft != null) {
				event.getPlayer().sendMessage(ChatColor.RED+"You are already piloting a ship.");
				event.setCancelled(true);
				return;
			}
			final Location placed = event.getPlayer().getLocation().getBlock().getLocation();
			placed.setY(getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG).getInt("seaLevel"));
			Direction clipboardDir = StructureBoxes.getInstance().getWorldEditHandler().getClipboardFacingFromOrigin(clipboard,
					io.github.eirikh1996.structureboxes.utils.MathUtils.bukkit2SBLoc(placed));
			Direction playerDir = Direction.fromYaw(event.getPlayer().getLocation().getYaw());
			int angle = playerDir.getAngle(clipboardDir);
			boolean exemptFromRegionRestriction = false;
			if (!Settings.RestrictToRegionsExceptions.isEmpty()) {
				for (String exception : Settings.RestrictToRegionsExceptions) {
					if (exception == null) {
						continue;
					}
					if (ChatColor.stripColor(lore.get(0)).toLowerCase().contains(exception.toLowerCase())) {
						exemptFromRegionRestriction = true;
						break;
					}

				}
			}
			if (Settings.RestrictToRegionsEnabled && !RegionUtils.isWithinRegion(placed) && !exemptFromRegionRestriction && !event.getPlayer().hasPermission("structureboxes.bypassregionrestriction")) {
				event.getPlayer().sendMessage(COMMAND_PREFIX + I18nSupport.getInternationalisedString("Place - Must be within region"));
				event.setCancelled(true);
				return;
			}

			if (!StructureBoxes.getInstance().getWorldEditHandler().pasteClipboard(event.getPlayer().getUniqueId(), schematicID, clipboard, angle, new IWorldEditLocation(placed))) {
				event.setCancelled(true);
				return;
			}
			final Structure structure = StructureManager.getInstance().getLatestStructure(event.getPlayer().getUniqueId());
			event.getPlayer().sendMessage("Registering Boat, Please wait.");
			new BukkitRunnable() {
				@Override
				public void run() {
					doMovecraftRegister(event, structure);
				}
			}.runTaskLater(getPlugin(), 20L);
			playerTimeMap.put(id, System.currentTimeMillis());
			structure.setExpiry(Integer.MAX_VALUE);
			itemAmountMinus(event.getPlayer().getInventory().getItemInMainHand());
			event.setCancelled(true);


		}

	}
	@EventHandler
	public void onThrowDropTnT(PlayerInteractEvent event){
		if(event.getHand()!= EquipmentSlot.HAND) return;
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (event.getItem().getType()!=Material.TNT){
				return;
			}
			itemAmountMinus(event.getItem());
			TNTPrimed tnt = event.getPlayer().getWorld().spawn(event.getPlayer().getLocation(), TNTPrimed.class);
			tnt.setFuseTicks(40);
			event.setCancelled(true);
			return;
		}
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
			for (Entity entity:event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 1, 1, 1)){
				if (entity instanceof TNTPrimed){
					TNTPrimed tnt = (TNTPrimed)entity;
					tnt.setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(1.0));
					return;
				}
			}
		}
	}
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event){
		for (Entity entity:event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 1, 1, 1)){
			if (entity instanceof TNTPrimed){
				TNTPrimed tnt = (TNTPrimed)entity;
				tnt.remove();
				addItemtoPlayer(event.getPlayer(), new ItemStack(Material.TNT, 1));
				return;
			}
		}
	}
	@EventHandler(ignoreCancelled = true)
	public void onBannerBreak(BlockBreakEvent event){
		if(event.getBlock().getType() != Material.STANDING_BANNER){
			return;
		}
		Location tanneryFurnitureLoc = event.getBlock().getRelative(BlockFace.DOWN).getLocation();
		if (!isTannery(tanneryFurnitureLoc)){
			return;
		}
		ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(tanneryFurnitureLoc);
		Tannery tannery = cd.getTannery(tanneryFurnitureLoc);
		if(tannery == null){
			return;
		}
		event.setCancelled(true);
		cd.removeFurniture(tanneryFurnitureLoc);

		ItemStack itemStack = tannery.getOutputItem();
		if (itemStack == null) {
			event.getPlayer().sendMessage(this.dm.getTCM("tannery.dust"));
			event.getBlock().setType(Material.AIR);
		} else {
			event.getBlock().getWorld().dropItem(event.getBlock().getLocation(),itemStack);
		}


	}

	@EventHandler
	public void onCraftRelease(CraftReleaseEvent event){
		if(event.getCraft().getSinking()){
			return;
		}
//		String schematicName = event.getCraft().getName();
//		Player player = event.getCraft().getNotificationPlayer();
//		createStructureBox(player, player == null?event.getCraft().getPhaseBlocks().keySet().stream().findFirst().orElse(null):player.getLocation(),schematicName);
		event.setCancelled(true);
		event.getCraft().sink();

	}
	private final String schematicDir;
	private boolean createStructureBox(Player player, Location loc, @NotNull String schematicName){
		File schematicFile = new File(StructureBoxes.getInstance().getWorldEditPlugin().getDataFolder().getAbsolutePath() + "/" + schematicDir + "/" + schematicName + ".schematic");

		if (!schematicFile.exists()){
			schematicFile = new File(StructureBoxes.getInstance().getWorldEditPlugin().getDataFolder().getAbsolutePath() + "/" + schematicDir + "/" + schematicName + ".schem");
		}
		boolean noSchematic = !schematicFile.exists();
		if (schematicName.endsWith("_#"))  {
			final String start = schematicName.replace("_#", "");
			final File schemDir = new File(StructureBoxes.getInstance().getWorldEditPlugin().getDataFolder().getAbsolutePath() + "/" + schematicDir);
			final String[] foundFiles = schemDir.list(
					(file, name) -> (name.endsWith(".schematic") || name.endsWith(".schem")) &&
							name.startsWith(start) && isInteger(name.replace(start + "_", "").replace(".schematic", "").replace(".schem", "")));
			noSchematic = foundFiles == null || foundFiles.length == 0;
		}
		if (noSchematic) return true;
		ItemStack structureBox = new ItemStack((Material) Settings.StructureBoxItem);
		List<String> lore = new ArrayList<>();
		ItemMeta meta = structureBox.getItemMeta();
		meta.setDisplayName(Settings.StructureBoxLore);
		lore.add(Settings.StructureBoxPrefix + schematicName);
		lore.addAll(Settings.StructureBoxInstruction);
		meta.setLore(lore);
		structureBox.setItemMeta(meta);
		if (player != null){
			addItemtoPlayer(player, structureBox);
			return true;
		}
		loc.getWorld().dropItem(loc,structureBox);
		return true;
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onBlockPlace(final BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		final UUID id = event.getPlayer().getUniqueId();
		if (!event.getBlockPlaced().getType().equals(Settings.StructureBoxItem) &&
				event.getItemInHand().getItemMeta() == null ||
				!event.getItemInHand().getItemMeta().hasLore()) {
			return;
		}
		List<String> lore = event.getItemInHand().getItemMeta().getLore();
		assert lore != null;
		String schematicID = ChatColor.stripColor(lore.get(0));
		if (!schematicID.startsWith(ChatColor.stripColor(Settings.StructureBoxPrefix))) {
			boolean hasAlternativePrefix = false;
			for (String prefix : Settings.AlternativePrefixes) {
				if (!schematicID.startsWith(prefix)) {
					continue;
				}
				hasAlternativePrefix = true;
				schematicID = schematicID.replace(prefix, "");
				break;
			}
			if (!hasAlternativePrefix) {
				return;
			}
		} else {
			schematicID = schematicID.replace(ChatColor.stripColor(Settings.StructureBoxPrefix), "");
		}
		if (Settings.RequirePermissionPerStructureBox && !event.getPlayer().hasPermission("structureboxes.place." + schematicID)) {
			event.getPlayer().sendMessage(String.format(COMMAND_PREFIX + I18nSupport.getInternationalisedString("Place - No permission for this ID"), schematicID));
			return;
		}
		if (playerTimeMap.containsKey(id) && playerTimeMap.get(id) != null && (System.currentTimeMillis() - playerTimeMap.get(id)) < Settings.PlaceCooldownTime) {
			event.getPlayer().sendMessage(COMMAND_PREFIX + I18nSupport.getInternationalisedString("Place - Cooldown"));
			return;
		}
		Clipboard clipboard = StructureBoxes.getInstance().getWorldEditHandler().loadClipboardFromSchematic(new BukkitWorld(event.getBlockPlaced().getWorld()), schematicID);
		if (clipboard == null && schematicID.endsWith("_#")) {
			final String start = schematicID.replace("#", "");
			File schemDir = StructureBoxes.getInstance().getWorldEditHandler().getSchemDir();
			final String[] foundFiles = schemDir.list((file, name) ->
					(name.endsWith(".schematic") || name.endsWith(".schem")) &&
							name.startsWith(start) &&
							isInteger(name.replace(start, "").replace(".schematic", "").replace(".schem", ""))
			);
			assert foundFiles != null;
			if (foundFiles.length == 0)
				return;
			final Random random = new Random();
			String schemID = foundFiles[random.nextInt(foundFiles.length)].replace(".schematic", "").replace(".schem", "");
			clipboard = StructureBoxes.getInstance().getWorldEditHandler().loadClipboardFromSchematic(new BukkitWorld(event.getBlockPlaced().getWorld()), schemID);
		}
		if (clipboard == null) {
			return;
		}
		Craft oldCraft = CraftManager.getInstance().getCraftByPlayerName(event.getPlayer().getName());
		if (oldCraft != null) {
			event.getPlayer().sendMessage(ChatColor.RED+"You are already piloting a ship.");
			event.setCancelled(true);
			return;
		}
		final Location placed = event.getBlockPlaced().getLocation();
		placed.setY(getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG).getInt("seaLevel"));
		Direction clipboardDir = StructureBoxes.getInstance().getWorldEditHandler().getClipboardFacingFromOrigin(clipboard,
				io.github.eirikh1996.structureboxes.utils.MathUtils.bukkit2SBLoc(placed));
		Direction playerDir = Direction.fromYaw(event.getPlayer().getLocation().getYaw());
		int angle = playerDir.getAngle(clipboardDir);
		boolean exemptFromRegionRestriction = false;
		if (!Settings.RestrictToRegionsExceptions.isEmpty()) {
			for (String exception : Settings.RestrictToRegionsExceptions) {
				if (exception == null) {
					continue;
				}
				if (ChatColor.stripColor(lore.get(0)).toLowerCase().contains(exception.toLowerCase())) {
					exemptFromRegionRestriction = true;
					break;
				}

			}
		}
		if (Settings.RestrictToRegionsEnabled && !RegionUtils.isWithinRegion(placed) && !exemptFromRegionRestriction && !event.getPlayer().hasPermission("structureboxes.bypassregionrestriction")) {
			event.getPlayer().sendMessage(COMMAND_PREFIX + I18nSupport.getInternationalisedString("Place - Must be within region"));
			event.setCancelled(true);
			return;
		}

		if (!StructureBoxes.getInstance().getWorldEditHandler().pasteClipboard(event.getPlayer().getUniqueId(), schematicID, clipboard, angle, new IWorldEditLocation(placed))) {
			event.setCancelled(true);
			return;
		}
		final Structure structure = StructureManager.getInstance().getLatestStructure(event.getPlayer().getUniqueId());
		event.getPlayer().sendMessage("Registering Boat, Please wait.");
		new BukkitRunnable() {
			@Override
			public void run() {
				doMovecraftRegister(event, structure);
			}
		}.runTaskLater(getPlugin(), 20L);
		playerTimeMap.put(id, System.currentTimeMillis());
		structure.setExpiry(Integer.MAX_VALUE);
		itemAmountMinus(event.getItemInHand());
		event.setCancelled(true);


	}
	public void doMovecraftRegister(BlockPlaceEvent event, Structure structure){
		int minx, maxx, minz, maxz, miny, maxy;
		minx=minz=miny=Integer.MAX_VALUE;maxx=maxy=maxz=Integer.MIN_VALUE;
		World world = event.getBlockPlaced().getWorld();
		for(io.github.eirikh1996.structureboxes.utils.Location loc: structure.getStructure()){
			if(!world.getBlockAt(loc.getX(), loc.getY(), loc.getZ()).getType().isSolid()) continue;
			minx = Math.min(minx, loc.getX());
			maxx = Math.max(maxx, loc.getX());
			miny = Math.min(miny, loc.getY());
			maxy = Math.max(maxy, loc.getY());
			minz = Math.min(minz, loc.getZ());
			maxz = Math.max(maxz, loc.getZ());
		}
		maxx++;maxy++;maxz++;
		if(minx == Integer.MAX_VALUE) return;
		int x = (minx+maxx)/2, z = (minz+maxz)/2, y = miny;
		while(y <= maxy){
			if(!world.getBlockAt(x, y, z).getType().isSolid() && !world.getBlockAt(x, y+1, z).getType().isSolid() || y == maxy) {
				event.getPlayer().teleport(new Location(world, x + 0.5, y, z + 0.5));
				break;
			}
			y++;
		}

		Player player = event.getPlayer();
		CraftType craftType = CraftManager.getInstance().getCraftTypeFromString("ship");
		if (craftType != null) {
			Craft oldCraft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
			if (oldCraft != null) {
				CraftManager.getInstance().removeCraft(oldCraft, CraftReleaseEvent.Reason.FORCE);
			}
			ICraft newCraft = new ICraft(craftType, player.getWorld());
			MovecraftLocation startPoint = MathUtils.bukkit2MovecraftLoc(player.getLocation());
			newCraft.detect(player, player, startPoint);
			newCraft.setName(structure.getSchematicName());
			Bukkit.broadcastMessage(structure.getSchematicName());
			Bukkit.getServer().getPluginManager().callEvent(new CraftPilotEvent(newCraft, CraftPilotEvent.Reason.PLAYER));
		}
		StructureManager.getInstance().removeStructure(structure);
	}
	public void doMovecraftRegister(PlayerInteractEvent event, Structure structure){
		int minx, maxx, minz, maxz, miny, maxy;
		minx=minz=miny=Integer.MAX_VALUE;maxx=maxy=maxz=Integer.MIN_VALUE;
		World world = event.getPlayer().getWorld();
		for(io.github.eirikh1996.structureboxes.utils.Location loc: structure.getStructure()){
			if(!world.getBlockAt(loc.getX(), loc.getY(), loc.getZ()).getType().isSolid()) continue;
			minx = Math.min(minx, loc.getX());
			maxx = Math.max(maxx, loc.getX());
			miny = Math.min(miny, loc.getY());
			maxy = Math.max(maxy, loc.getY());
			minz = Math.min(minz, loc.getZ());
			maxz = Math.max(maxz, loc.getZ());
		}
		maxx++;maxy++;maxz++;
		if(minx == Integer.MAX_VALUE) return;
		int x = (minx+maxx)/2, z = (minz+maxz)/2, y = miny;
		while(y <= maxy){
			if(!world.getBlockAt(x, y, z).getType().isSolid() && !world.getBlockAt(x, y+1, z).getType().isSolid() || y == maxy) {
				event.getPlayer().teleport(new Location(world, x + 0.5, y, z + 0.5));
				break;
			}
			y++;
		}

		Player player = event.getPlayer();
		CraftType craftType = CraftManager.getInstance().getCraftTypeFromString("ship");
		if (craftType != null) {
			Craft oldCraft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
			if (oldCraft != null) {
				CraftManager.getInstance().removeCraft(oldCraft, CraftReleaseEvent.Reason.FORCE);
			}
			ICraft newCraft = new ICraft(craftType, player.getWorld());
			MovecraftLocation startPoint = MathUtils.bukkit2MovecraftLoc(player.getLocation());
			newCraft.detect(player, player, startPoint);
			newCraft.setName(structure.getSchematicName());
			Bukkit.broadcastMessage(structure.getSchematicName());
			Bukkit.getServer().getPluginManager().callEvent(new CraftPilotEvent(newCraft, CraftPilotEvent.Reason.PLAYER));
		}
		StructureManager.getInstance().removeStructure(structure);
	}

		@EventHandler
	public void onCraft(PrepareItemCraftEvent event) {
		ItemStack[] itemstacks = event.getInventory().getContents();
		Material type = null;
		String temp = null;
		for (int i = 1; i < itemstacks.length; i++) {
			if(itemstacks[i] == null||itemstacks[i].getType() == Material.AIR) {
				continue;
			}
			String s = new NBTItem(itemstacks[i]).getString(NBTIMaterial);
			if (temp == null) {
				if (s == null)
					continue;
				temp = s;
				type = itemstacks[i].getType();
			} else if (type == itemstacks[i].getType() && !temp.equals(s) ||
					type != itemstacks[i].getType() && s != null) {
				event.getInventory().setItem(0, null);
				return;
			}


		}
		if(temp != null) {
			CustomItem ci = getPlugin().getCustomItemManager().getItem(temp);
			if (ci != null) {
				event.getInventory().setItem(0,ci.getEquipment(itemstacks[0].getType()));
			}
		}
	}

	@EventHandler
	public void durabilityEvent(PlayerItemDamageEvent event) {
		NBTItem nbti = new NBTItem(event.getItem());
		if (nbti.hasKey(CustomItem.NBTIDurability)) {
			double damage = event.getDamage();
			double durabilityMultiplier = nbti.getDouble(CustomItem.NBTIDurability);
			double dura;
			if (nbti.hasKey("Durability")) {
				dura = nbti.getDouble("Durability");
			} else {
				dura = durabilityMultiplier;
			}

			if (damage % durabilityMultiplier >= dura) {
				event.setDamage((int) (damage / durabilityMultiplier) + 1);
				damage = damage % durabilityMultiplier;
				nbti.setDouble("Durability", durabilityMultiplier - damage + dura);
			} else {
				event.setDamage((int) (damage / durabilityMultiplier));
				damage = damage % durabilityMultiplier;
				nbti.setDouble("Durability", dura - damage);
			}
			event.getItem().setItemMeta(nbti.getItem().getItemMeta());
		}
	}
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event){
		if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
			if(event.getDamager() instanceof Arrow && ((Arrow)event.getDamager()).getShooter() instanceof Player && event.getDamage() > 5) {
				event.setDamage(event.getDamage() + Utility.getPlayerSlotInteger((Player)((Arrow)event.getDamager()).getShooter(), CustomItem.NBTIRangedDmg));
			}
			if (event.getEntity() instanceof Player) {
				Player player = ((Player)event.getEntity());
				if (randNum(100)<Utility.getPlayerSlotInteger(player, CustomItem.NBTIDeflect)/6d){
					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 3,3);
					event.setCancelled(true);
				}

			}
		}else if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
			if(event.getDamager() instanceof Player && event.getDamage() > 1.5){
				Player player = ((Player)event.getDamager());
				if(player.getFallDistance() > 0.0F && !player.hasPotionEffect(PotionEffectType.BLINDNESS)){
					event.setDamage(event.getDamage()+ Utility.getPlayerSlotInteger(player, CustomItem.NBTICriticalDmg));
				}
			}
			if (event.getEntity() instanceof Player) {
				Player player = ((Player)event.getEntity());
				if (randNum(100)<Utility.getPlayerSlotInteger(player, CustomItem.NBTIDodge)/6d){
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 50 , 3);
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void damaged(EntityDamageEvent event){
		if ((event.getCause() == EntityDamageEvent.DamageCause.MAGIC ||
				event.getCause() == EntityDamageEvent.DamageCause.WITHER ||
				event.getCause() == EntityDamageEvent.DamageCause.POISON) && event.getEntity() instanceof Player) {
			Player player = ((Player) event.getEntity());
			event.setDamage(event.getDamage() * (1 - Math.min(1, 0.02 * Utility.getPlayerSlotInteger(player, "MagicResist"))));

		}else if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && event.getEntity() instanceof IslandWither) {
			event.setCancelled(true);
		}
	}

	public boolean isTannery(Location location){
		WrappedLocation loc = new WrappedLocation(location);
		Material mat = location.getBlock().getType();
		if (mat == Material.WORKBENCH) {
			if (loc.add(0, 3, 0).getBlock().getType() == Material.FENCE) {
				if (loc.add(1, 0, 0).getBlock().getType() == Material.FENCE &&
						loc.add(-1, 0, 0).getBlock().getType() == Material.FENCE &&
						loc.add(1, 1, 0).getBlock().getType() == Material.FENCE &&
						loc.add(-1, 1, 0).getBlock().getType() == Material.FENCE &&
						loc.add(1, 2, 0).getBlock().getType() == Material.FENCE &&
						loc.add(-1, 2, 0).getBlock().getType() == Material.FENCE &&
						loc.add(1, 3, 0).getBlock().getType() == Material.FENCE &&
						loc.add(-1, 3, 0).getBlock().getType() == Material.FENCE) {
					return true;
				}
				else if (loc.add(0, 0, 1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 0, -1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 1, 1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 1, -1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 2, 1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 2, -1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 3, 1).getBlock().getType() == Material.FENCE &&
						loc.add(0, 3, -1).getBlock().getType() == Material.FENCE) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isSmeltery(Location location){
		WrappedLocation loc = new WrappedLocation(location);
		Material mat = location.getBlock().getType();
		if (mat == Material.FURNACE || mat == Material.BURNING_FURNACE) {
			if (loc.add(0, 1, 0).getBlock().getType() == Material.COBBLE_WALL &&
					(loc.add(0, -1, 0).getBlock().getType() == Material.LAVA ||
							loc.add(0, -1, 0).getBlock().getType() == Material.STATIONARY_LAVA)) {
				return true;
			}
		}
		return false;
	}
	public boolean isCauldron(Location location){
		WrappedLocation loc = new WrappedLocation(location);
		Material mat = location.getBlock().getType();
		if (mat == Material.CAULDRON) {
			if (loc.add(0, 1, 0).getBlock().getType() == Material.COBBLE_WALL &&
					loc.add(0, 2, 0).getBlock().getType() == Material.WEB) {
				return true;
			}
		}
		return false;
	}
	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
