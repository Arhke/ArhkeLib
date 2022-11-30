package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.core.Feudal;
import com.Arhke.WRCore.District.util.Utils;
import com.Arhke.WRCore.District.util.UtilsAbove1_7;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Main;
import com.sun.istack.internal.NotNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

//Manages taxes, claiming, and unclaiming
/**
 * This class has no use for api usage.
 * @author Michael Forseth
 *
 */
public class LandManagement extends MainBase {

	public LandManagement(Main Instance) {
		super(Instance);
	}







	public static boolean piston(Location location) {
		ChunkData cd = Main.getPlugin().getWorldDataManager().getOrNewChunkData(location);
		Town town = cd.getTown();
		if(town == null) {
			return false;
		}
		if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.piston")) {
			return true;
		}else {
			return true;
		}
	}

	public static void explode(EntityExplodeEvent event) {
//		boolean cancel = false;
//		boolean cancelVac = false;
//		if(event.getEntityType() != null && event.getEntityType().equals(EntityType.CREEPER)){
//			if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.creeperExplosion")){
//				cancel = true;
//			}
//
//		}else{
//			if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.otherExplosion")){
//				cancel = true;
//			}
//			if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.shieldProtection.otherExplosion")){
//				cancelVac = true;
//			}
//		}
//
//		ArrayList<Block> remove = new ArrayList<Block>();
//		for(Block b : event.blockList()){
//			Land l = new ChunkData(b.getLocation());
//			Kingdom king = Feudal.getLandKingdom(l);
//			if(king != null){
//				if(!king.hasProtection()){
//					continue;
//				}
//				if(king.isShielded()){
//					if(cancelVac){
//						remove.add(b);
//					}
//				}else if(cancel){
//					remove.add(b);
//				}
//			}
//		}
//
//		for(Block b : remove){
//			event.blockList().remove(b);
//		}
	}

	public static void fireSpread(BlockIgniteEvent event) {
		if(event.getCause().equals(IgniteCause.FIREBALL) || event.getCause().equals(IgniteCause.FLINT_AND_STEEL)){
			return;
		}
		if(event.getBlock() != null){
//			ChunkData cd = Main.getPlugin().getWorldDataManager().getOrNewChunkData((event.getBlock().getLocation()));
//			Town town = cd.getTown();
//			if(town != null){
//				if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.fireSpread")){
//					event.setCancelled(true);
//					if(event.getIgnitingBlock() != null && event.getIgnitingBlock().getType().equals(Material.FIRE)){
//						event.getIgnitingBlock().setType(Material.AIR);
//					}
//				}
//			}
			event.setCancelled(true);
		}
	}

	public static void burn(BlockBurnEvent event) {
		if(event.getBlock() != null){
			ChunkData cd = Main.getPlugin().getWorldDataManager().getOrNewChunkData(event.getBlock().getLocation());
			Town town = cd.getTown();
			if (town == null){
				return;
			}
			if(Feudal.getConfiguration().getConfig().getBoolean("kingdom.landProtection.fireSpread")){
				event.setCancelled(true);
			}
		}
	}

	public static void entityChangeBlock(EntityChangeBlockEvent event) {
		if(event.getEntityType() != null && event.getBlock() != null){
			if(event.getEntityType().equals(EntityType.ENDER_DRAGON)){
				event.setCancelled(true);
			}else if(event.getEntityType().equals(EntityType.ENDERMAN)){
				event.setCancelled(true);
			}else if(event.getEntityType().equals(EntityType.VILLAGER)){
				event.setCancelled(true);
			}
		}
	}



}
