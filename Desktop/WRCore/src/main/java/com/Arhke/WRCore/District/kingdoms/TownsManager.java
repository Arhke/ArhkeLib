package com.Arhke.WRCore.District.kingdoms;



import com.Arhke.WRCore.Lib.Base.MainBase;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.*;

public class TownsManager extends MainBase {


	/**
	 * Key is the Name of the Town
	 * Value is the Town Object
	 */
	Map<String,Town> towns = new HashMap<>();
	DirectoryManager dirMan;
	
	public TownsManager(Main Instance, DirectoryManager dm) {
		super(Instance);
		dirMan = dm;
		for(FileManager fm: dm.getFMList()){
			Town t;
			try {
				t = new Town(getPlugin(), fm);
				this.towns.put(t.getName(), t);
			} catch (Exception e) {
				error("Error Loading Town Data for " + fm.getFileName() + " Skipping this file");
			}


		}
	}
	
	public Town createTown(TUser user, String name, Location loc) throws Exception{
		Town t = new Town(getPlugin(), user, name, loc, this.dirMan.getOrNewFM(UUID.randomUUID().toString() + ".yml"));
		towns.put(name, t);
		getPlugin().getKingdomsManager().addNewKingdom(t, user);
		return t;
	}

	/**
	 * remove a town
	 * @param t town that is to be removed
	 */
	public void removeTown(Town t) {
		if (t.getKingdom().getTowns().size() == 1) {
			t.getKingdom().getOnlinePlayers().forEach(p->getPlugin().getNameTag().unregisterPlayerNameTag(p));
			getPlugin().getKingdomsManager().removeKingdom(t.getKingdom());
		}
		this.towns.remove(t.getName(), t);
		t.remove();
	}
	public void saveTowns() {
		this.towns.values().forEach(Town::save);
	}

	public void saveTown(String name) {
		Town t;
		if((t = getTown(name)) != null){
			info("Successfully saved data for Town " + t.getName());
			t.write();
			t.save();
		}else{
			warn("You are trying to save data for an User ID that Doesn't exist");
		}
	}

	/**
	 *
	 * @param name name of the town to be found
	 * @return a town instance if a town matches the specified name
	 */
	@Nullable
	public Town getTown(String name) {
		return this.towns.get(name);
	}
	public Town getTown(UUID id) {
		return this.towns.values().stream().filter(town -> town.getId().equals(id)).findFirst().orElse(null);
	}

	public Collection<Town> getTowns() {
		return this.towns.values();
	}
	
}
