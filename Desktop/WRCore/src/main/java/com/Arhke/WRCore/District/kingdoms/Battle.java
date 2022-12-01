package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 *
 * @author William Lin
 *
 */
public class Battle extends MainBase {
	private final Town town;
	private long warningTimeStamp;
	private UUID islandWither;
	UUID islandWitherSeat;



	public Battle(Main instance, Town town){
		super(instance);
		this.town = town;
	}
	public void start() {
		DataManager dm = getPlugin().getConfig(ConfigLoader.ConfigFile.DLISTENERLANG);
		Location heartLoc = getDefenderTown().getHeartLocation().clone();
		ArmorStand as = (ArmorStand)heartLoc.getWorld().spawnEntity(heartLoc, EntityType.ARMOR_STAND);
		this.islandWitherSeat = as.getUniqueId();
		as.setSmall(true);
		as.setVisible(false);
		as.setInvulnerable(true);
		as.setGravity(false);
		IslandWither wither = new IslandWither(heartLoc);
		((CraftWorld)heartLoc.getWorld()).getHandle().addEntity(wither);
		CraftEntity ce = wither.getBukkitEntity();
		((LivingEntity)ce).setRemoveWhenFarAway(false);
//		ce.setSilent(true);
		this.islandWither = ce.getUniqueId();
		as.addPassenger(ce);
		as.setRemoveWhenFarAway(false);

		ce.setGlowing(true);
		ce.setCustomName(dm.getTCM("battle.coreName", town.getName()));


	}
	public void end(){
		getDefenderTown().getHeartLocation().getChunk().load();
		Bukkit.getEntity(islandWither).remove();
		Bukkit.getEntity(islandWitherSeat).remove();
		queueRemove();
	}

	public boolean needsReWarn(){
		return this.warningTimeStamp < System.currentTimeMillis();
	}
	private void setWarningTimeStamp(){
		this.warningTimeStamp = System.currentTimeMillis() + 60000;
	}
	public void warnPlayers(){
		if (needsReWarn()){
			this.getDefenderTown().getKingdom().messageMembers(
					tcm("&7[&6Warning&7]&c&lIll intentions were detected at town &r&7{0}", this.getDefenderTown().getName()), true);
		}else{
			setWarningTimeStamp();
		}
	}

	private void queueRemove(){
		final Battle battle = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				getPlugin().getBattleManager().battles.remove(battle.getDefenderTown(), battle);
			}
		}.runTaskLater(getPlugin(), 1);
	}
	public void defenderWin(){
		DataManager dm = getPlugin().getConfig(ConfigLoader.ConfigFile.DLISTENERLANG);
		Bukkit.broadcastMessage(dm.getTCM("battle.defenderWin", this.getDefenderTown().getName()));
		getDefenderTown().getHeartLocation().getChunk().load();
		Bukkit.getEntity(islandWither).remove();
		Bukkit.getEntity(islandWitherSeat).remove();
	}
	public Town getDefenderTown(){
		return this.town;
	}
	public UUID getWither(){
		return this.islandWither;
	}
	public void witherDeathEvent(EntityDeathEvent event){
		if (!event.getEntity().getUniqueId().equals(getWither())){
			return;
		}
		DataManager dm = getPlugin().getConfig(ConfigLoader.ConfigFile.DLISTENERLANG);
		if (event.getEntity().getKiller() == null){
			getPlugin().getTownsManager().removeTown(town);
			Bukkit.broadcastMessage(dm.getTCM("battle.defenderLostTown", this.getDefenderTown().getKingdom().getName(),
					this.getDefenderTown().getName(), "an Unknown Source!"));
			return;
		}
		String attacker;
		TUser killer = getPlugin().getTUserManager().getOrNewTUser(event.getEntity().getKiller());
		Kingdom kingdom = this.getDefenderTown().getKingdom();
		String defendingKingdom = this.getDefenderTown().getKingdom().getName();
		String defendingTown = this.getDefenderTown().getName();
		if (killer.getKingdom() == null){
			getPlugin().getTownsManager().removeTown(getDefenderTown());
			attacker = killer.getName();
		}else{
			this.getDefenderTown().setKingdom(killer.getKingdom());
			attacker = killer.getKingdom().getName();
		}
		Bukkit.broadcastMessage(dm.getTCM("battle.defenderLostTown", defendingKingdom,
				defendingTown, attacker));
		if (kingdom.getTowns().size() == 0) {
			for(UUID uuid: kingdom.getMembers().keySet()) {
				TUser user = getPlugin().getTUserManager().tempGetTUser(uuid);
				if(user!=null){
					user.setKingdom(null);
				}
			}
			getPlugin().getKingdomsManager().removeKingdom(kingdom);
		}
		end();
	}



	//===================<Utility>===================
	@Override
	public boolean equals(Object o){
		return o instanceof Battle && this.town.equals(((Battle)o).getDefenderTown());
	}
	@Override
	public int hashCode(){
		return this.town.getId().hashCode();
	}
	public enum BattleType{
		WARCRY, NIGHTRAID
	}

}
