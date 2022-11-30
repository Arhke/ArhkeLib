package com.Arhke.WRCore.District.TUsers;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.api.events.ReputationChangeEvent;
import com.Arhke.WRCore.District.core.Feudal;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.KingdomLog;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.District.util.OnlineTime;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TUser extends MainBase {
	private final FileManager fm;
	private final DataManager dm;
	private UUID id;
	private String name;
	private Kingdom kingdom;
	private Location spawnLoc;
	private double taxRedux;



	private volatile short reputation; //Default reputation = 1000. MAX REPUTATION BEFORE ERROR 32000
	private volatile ArrayList<KingdomLog> kingdomLogs = new ArrayList<KingdomLog>();//Stores kingdom join and leave log.
	private volatile long firstJoin, lastJoin;
	private volatile List<String> offlineMessages = new ArrayList<String>();
	private volatile long lastLandMessage;
	private volatile UUID lastLandTown;
	private boolean isOnline = false;
	//SQL stuff
	private volatile boolean change = false;
	private volatile boolean changeOfflineMessage = false;
	//

	private byte chat = 0;
	private long instanceCreateTime = System.currentTimeMillis();

	private OnlineTime onlineTime = new OnlineTime();
	public static final String NameKey = "name", KingdomKey = "kingdom", SpawnLocKey="spawnLoc",
			TaxReduxKey = "taxRedux", ReputationKey = "reputation", FirstJoinKey = "firstJoin",
			LastJoinKey = "lastJoin", OfflineMessageKey = "offlineMessage", OnlineTimeKey = "OnlineTime";
	/**
	 * from yml
	 * @param Instance
	 * @param fm
	 */
	TUser(Main Instance, FileManager fm) throws Exception{
		super(Instance);
		this.fm = fm;
		this.dm = fm.getDataManager();
		this.id = UUID.fromString(fm.getFileNameNoExt());
		this.name = this.dm.getString(NameKey);
		try {
			this.kingdom = getPlugin().getKingdomsManager().getKingdom(UUID.fromString(this.dm.getString(KingdomKey)));
			if(this.kingdom != null) this.kingdom.registerMember(this);
		}catch(IllegalArgumentException e){
			this.kingdom = null;
		}
		this.spawnLoc = this.dm.getLocation(SpawnLocKey);
		this.taxRedux = this.dm.getDouble(TaxReduxKey);
		this.reputation = (short) this.dm.getInt(ReputationKey);
		this.firstJoin = this.dm.getLong(FirstJoinKey);
		this.lastJoin = this.dm.getLong(LastJoinKey);
		this.offlineMessages = this.dm.getStringList(OfflineMessageKey);
		DataManager onlineTimeDM = this.dm.getDataManager(OnlineTimeKey);
		this.onlineTime = new OnlineTime(onlineTimeDM);
	}







	//===============<Getters and Setters>==============
	/**
	 *
	 * @return the UUID of this player
	 */
	public UUID getID() {
		return id;
	}
	/**
	 * Gets user's name
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * Get the player's kingdom.  This SHOULD be null if they are not in a town.
	 * @return
	 */
	public Kingdom getKingdom() {
		return kingdom;
	}

	/**
	 * Sets the player's kingdom, also manages registering the member and unregistering the member
	 * @param kingdom
	 */
	public void setKingdom(Kingdom kingdom){
		if(this.kingdom != null){
			this.kingdom.unregisterMember(this);
		}
		if(kingdom != null){
			kingdom.registerMember(this);
		}
		this.kingdom = kingdom;
	}

	public boolean isInKingdom(){
		return kingdom != null;
	}

	public void setOnline(boolean online, Player player){
		this.isOnline = online;
		if(!isOnline) {
			lastJoin = System.currentTimeMillis();
		}

	}


	public boolean isOnline() {
		return this.isOnline;
	}


	/**
	 * Saves a user.
	 */
	public void save(){
		dm.wipeAllKeys();
		dm.set(this.name, NameKey);
		dm.set(this.kingdom == null?null:this.kingdom.getId().toString(), KingdomKey);
		dm.set(this.spawnLoc, SpawnLocKey);
		dm.set(this.taxRedux, TaxReduxKey);
		dm.set(this.reputation, ReputationKey);
		dm.set(this.firstJoin, LastJoinKey);
		dm.set(this.lastJoin, LastJoinKey);
		dm.set(offlineMessages, OfflineMessageKey);
		fm.save();




	}


	/**
	 * Gets reputation
	 * @return
	 */
	public int getReputation() {
		return reputation;
	}


	/**
	 * Add past kingdom to this user's kingdom log
	 * @param kingdomLog
	 */
	public void addPastKingdom(KingdomLog kingdomLog) {
		this.kingdomLogs.add(kingdomLog);
	}

	/**
	 * Get this user's kingdom log. (Joining and leaving of kingdoms)
	 * @return
	 */
	public ArrayList<KingdomLog> getPastKingdoms(){
		return this.kingdomLogs;
	}

	/**
	 * Gets this users @OnlineTime
	 * @return
	 */
	public OnlineTime getOnlineTime() {
		return onlineTime;
	}

	/**
	 * Get when this user was last online.  Last online is the last time they joined.
	 * @return
	 */
	public long getLastOnline() {
		return this.lastJoin;
	}

	/**
	 * Get the time when a user first joined.
	 * @return
	 */
	public long getFirstJoinTime() {
		return this.firstJoin;
	}



	/**
	 * Sends this user a message which they will get the next time they join.
	 * @param string
	 */
	public void sendOfflineMessage(String string) {
		offlineMessages.add(string);
		changeOfflineMessage = true;

		try {
			this.save();
		}catch(Exception e) {
			ErrorManager.error(630322218, e);
		}
	}

	public void sendOfflineMessages(List<String> messages) {
		offlineMessages.addAll(messages);
		changeOfflineMessage = true;

		try {
			this.save();
		}catch(Exception e) {
			ErrorManager.error(631322218, e);
		}
	}

	/**
	 * Get this user's last land message.  The last time they were notified of changing land claims. This is used to avoid spaming the player.
	 * @return
	 */
	public long getLastLandMessage() {
		return lastLandMessage;
	}
	/**
	 * Sets this user's last land message.
	 * @param currentTimeMillis
	 */
	public void setLastLandMessage(long currentTimeMillis) {
		lastLandMessage = currentTimeMillis;
	}
	/**
	 * Sets this user's last land message.
	 * @param town
	 */
	public void setLastLandTown(Town town) {
		this.lastLandTown = town == null?null:town.getId();
	}
	/**
	 * @return if town is equal to the uuid of the last land town
	 */
	public boolean isLastLandTown(@Nullable Town town){
		if (town == null || this.lastLandTown == null)
			return town == null && this.lastLandTown == null;
		return this.lastLandTown.equals(town.getId());
	}



	//===============<Chat Section>=============
	/**
	 * Check if this user is using kingdom chat, ally chat, or normal chat
	 * @return
	 */
	public byte getChat() {
		return chat;
	}
	/**
	 * Sets this user to using kingdom chat or ally chat.
	 * Normal = 0
	 * Kingdom = 1
	 * Allies and kingdom = 2
	 * @param chat
	 */
	public void setChat(byte chat){
		this.chat = chat;
	}





	public List<String> getOfflineMessages(){
		return offlineMessages;
	}



	public void setReputation(short reputation) {
		this.setChange(true);
		this.reputation = reputation;
	}
	public void setFirstJoin(long firstJoin) {
		this.setChange(true);
		this.firstJoin = firstJoin;
	}

	public void addKingdomLogs(KingdomLog kingdomLog) {
		this.kingdomLogs.add(kingdomLog);
	}


	public void setLastJoin(long lastJoin) {
		this.setChange(true);
		this.lastJoin = lastJoin;
	}



	public boolean isChange() {
		return change;
	}


	public void setChange(boolean b) {
		this.change = b;
	}


	
}
