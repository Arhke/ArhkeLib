package com.Arhke.WRCore.District.TUsers;


import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DirectoryManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class TUserManager extends MainBase {

	
	Map<UUID, TUser> userData = new HashMap<UUID, TUser>();
	DirectoryManager dm;
	volatile List<TUser> saving;
	public TUserManager(Main Instance, DirectoryManager DM) {
		super(Instance);
		dm = DM;
		for (FileManager fm: DM.getFMList()){
			try {
				TUser tu = new TUser(getPlugin(), fm);
				userData.put(tu.getID(), tu);
			}catch(Exception e){
			}
		}
	}

	//==========<GetUsers>===========
	/**
	 * Getting Members you know are Online
	 * @param id
	 * @return TUser if ID found, null If not found
	 */
	private TUser getTUser(UUID id) {
		return userData.get(id);
	}
	/**
	 * Used when the player already exists and online and you need
	 * @param player
	 * @return
	 */
	public TUser getOrNewTUser(Player player) {
		TUser tu;
		if((tu = getTUser(player.getUniqueId())) == null){
			try {
				userData.put(player.getUniqueId(), tu = new TUser(getPlugin(), dm.getOrNewFM(player.getUniqueId().toString()+".yml")));
				tu.setOnline(true, player);
			} catch (Exception e) {
				player.kickPlayer("Your file was corrupted, please contact admins");
				return null;
			}
			tu.save();
		}
		return tu;
	}

	/**
	 * Get TUser and load it temporarily if there is a file for it
	 * @param name the name for the player being requested
	 * @return
	 */
	@Nullable
	public TUser tempGetTUser(String name){
		@SuppressWarnings("deprecated")
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
		return tempGetTUser(offlinePlayer);
	}
	/**
	 * Get TUser and load it temporarily if there is a file for it
	 * @param uuid The Unique ID for the player being requested
	 * @return
	 */
	@Nullable
	public TUser tempGetTUser(UUID uuid){
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		return tempGetTUser(offlinePlayer);
	}
	/**
	 * Get TUser and load it temporarily if there is a file for it
	 * @param offlinePlayer The OfflinePlayer instance for the player being requested
	 * @return
	 */
	@Nullable
	public TUser tempGetTUser(OfflinePlayer offlinePlayer){
		TUser u;
		if((u = getTUser(offlinePlayer.getUniqueId()))!=null)
			return u;
		FileManager fm = dm.getOrLoadFM(offlinePlayer.getUniqueId()+".yml");
		if(fm == null)
			return null;
		try {
			return new TUser(getPlugin(), fm);
		}catch(Exception e) {
			return null;
		}
	}

	public void loadPlayer(Player player) throws Exception{
		TUser tu;
		if((tu = getTUser(player.getUniqueId())) == null){
			userData.put(player.getUniqueId(), tu = new TUser(getPlugin(), dm.getOrNewFM(player.getUniqueId().toString()+".yml")));
			tu.save();
		}
		tu.setOnline(true, player);
	}


	/**
	 * Unload a player's data (Normally when they disconnect)
	 *
	 * @param player
	 *            The player
	 */
	public void unloadPlayer(Player player) throws Exception{
		TUser user = userData.remove(player.getUniqueId());
		if(user == null){
			return;
		}
		user.save();
	}
	

	
	public Collection<TUser> GetAllUsers() {
		return userData.values();
	}

	


	public void saveUser(UUID ID) {
		TUser tu;
		if((tu = getTUser(ID)) != null){
			info("Successfully saved data for userID " + ID.toString());
			tu.save();
		}else{
			warn("You are trying to save data for an User ID that Doesn't exist");
		}
	}
	public void asyncSaveUser(TUser u) {

		Thread thread = new Thread(new Runnable() {
			public void run() {
				synchronized(saving){
					saving.add(u);
				}
				try{
					u.save();
				}catch(Exception e) {
					ErrorManager.error(1056352018, e);
				}
				synchronized(saving){
					saving.remove(u);
				}

			}
		});
		thread.setName("asyncSaveUserLeave");
		thread.run();
	}
	
	public void saveAllUsers() {
		for(TUser tu: userData.values()){
			tu.save();
		}
		info("Sucessfully Saved Data for All Users");
	}
	
}
