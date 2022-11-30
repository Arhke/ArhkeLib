package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

/**
 * Land taxes, online times, offline messages, land removal, inactive kingdom deletion, shield updates, leader xp
 *
 * @author William Lin
 * @version August 1st 2021
 *
 */
public class Hourly extends MainBase {
	public Hourly(Main Instance) {
		super(Instance);
		getPlugin().getBattleManager().cleanUpWithers();
		new BukkitRunnable(){
			@Override
			public void run(){
				Collection<Kingdom> kingdoms;
				synchronized(getPlugin().getKingdomsManager().getKingdoms()){
					kingdoms = new ArrayList<>(getPlugin().getKingdomsManager().getKingdoms());
				}
				Calendar now = new GregorianCalendar();
				int index = now.get(Calendar.HOUR_OF_DAY)-1;
				try {
					for (Kingdom king : kingdoms) {
						if (king.getMembers().size() == 0 || king.getTowns().size() == 0) {
							syncRemoveKingdom(king);
							continue;
						}
						if (!king.getBanTime(index)){
							syncAddBattles(king);
						}

					}
				}catch(Exception e) {
					error(1041352018+"");
				}
			}
		}.runTask(getPlugin());
		hourlyUpdateAsync();
	}
	private void syncRemoveKingdom(Kingdom k){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
			info("Deleting kingdom for [Inactive]: " + k.getName());
			getPlugin().getKingdomsManager().removeKingdom(k);
		});
	}
	private void syncAddBattles(Kingdom k){
		new BukkitRunnable(){
			public void run(){
				getPlugin().getBattleManager().startBattle(k);
				info("Starting Battles for kingdom: " + k.getName());
			}
		}.runTask(getPlugin());
	}
	private void syncRemoveBattles(Kingdom k){
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
			getPlugin().getBattleManager().endKingdomBattles(k);
			info("Removing Battles for kingdom: " + k.getName());
		});
	}

	private void hourlyUpdateAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Calendar nextTask = new GregorianCalendar();

				nextTask.set(Calendar.MILLISECOND, 0);
				nextTask.set(Calendar.SECOND, 0);
				nextTask.set(Calendar.MINUTE, 0);
				nextTask.add(Calendar.HOUR_OF_DAY, 1);
				long till;
				if ((till = nextTask.getTimeInMillis() - System.currentTimeMillis()) < 600000) {
					new BukkitRunnable(){
						@Override
						public void run(){
							Collection<Kingdom> kingdoms;
							synchronized(getPlugin().getKingdomsManager().getKingdoms()){
								kingdoms = new ArrayList<>(getPlugin().getKingdomsManager().getKingdoms());
							}
							Calendar now = new GregorianCalendar();
							int index = now.get(Calendar.HOUR_OF_DAY)-1;
							try {
								for (Kingdom king : kingdoms) {
									if (king.getMembers().size() == 0 || king.getTowns().size() == 0) {
										syncRemoveKingdom(king);
										continue;
									}
									if (king.getBanTime(index)){
										if (!king.getBanTime(index-1)){
											syncRemoveBattles(king);
										}
									}else{
										if(king.getBanTime(index-1)){
											syncAddBattles(king);
										}
									}

								}
							}catch(Exception e) {
								error(1041352018+"");
							}
						}
					}.runTaskLaterAsynchronously(getPlugin(), till/50L);
				}

			}

		}.runTaskTimerAsynchronously(getPlugin(), 0, 20*59*10);


	}


}
