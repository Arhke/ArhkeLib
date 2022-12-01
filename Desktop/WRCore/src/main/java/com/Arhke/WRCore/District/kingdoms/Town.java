package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.*;

import java.util.*;

public class Town extends MainBase implements Comparable<Town>{
	private UUID id;
	private String name;
	private volatile int bank;
	private long createTimeStamp;
	private long lastOnlineTimeStamp;
	private double tax;
	private UUID founderId;
	private String founderName;
	private volatile ArrayList<ChunkData> land = new ArrayList<>();
	private Location heartLocation;
	/**
	 * add the members each time the plugin is loaded
	 */
	private boolean townOpen;
	private Kingdom kingdom;
	private DataManager dm;
	private FileManager fm;
	Town(Main Instance, FileManager fm) throws Exception{
		super(Instance);
		this.dm = fm.getDataManager();
		this.fm = fm;
		this.id = UUID.fromString(fm.getFileNameNoExt());
		this.name = dm.getDefString(this.id.toString(), NameKey);
		this.bank = dm.getInt(BankKey);
		this.createTimeStamp = dm.getLong(CreateKey);
		this.lastOnlineTimeStamp = dm.getLong(LastOnlineKey);
		this.founderId = dm.getUUID(FounderIDKey);
		this.founderName = Bukkit.getOfflinePlayer(founderId).getName();
		this.heartLocation = dm.getLocation(HeartKey);
		UUID kingdomId = dm.getUUID(KingdomKey);
		if (kingdomId == null){
			except("[WRCore|Town.java] KingdomId can't be null.");
			return;
		}
		Kingdom kingdom = getPlugin().getKingdomsManager().getKingdom(kingdomId);
		if (kingdom == null){
			except("[WRCore|Town.java] Kingdom can't be null.");
			return;
		}
		setKingdom(kingdom);
	}
	Town(Main Instance, TUser founder, String name, Location spawn, FileManager fm) throws Exception {
		super(Instance);
		id = UUID.fromString(fm.getFileNameNoExt());
		this.name = name;
		this.bank = 0;
		this.createTimeStamp = System.currentTimeMillis();
		this.lastOnlineTimeStamp = System.currentTimeMillis();
		this.founderId = founder.getID();
		this.founderName = founder.getName();
		this.heartLocation = spawn;
		this.townOpen = false;
		this.fm = fm;
		this.dm = fm.getDataManager();
	}

	//================<Getters and Setters>=====================
	public UUID getId() {return this.id;}
	public String getName() {
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
	/**
	 * Removes/Adds the amount from the bank.
	 * @param amount amount to be added/removed from teh kingdom bank
	 */
	public void bankDeposit(int amount){
		bank = bank+amount;
	}
	public int getBank(){
		return bank;
	}
	public long getAge() {
		return System.currentTimeMillis()-this.createTimeStamp;
	}
	public long getLastOnline() {
		return System.currentTimeMillis()-this.lastOnlineTimeStamp;
	}
	public double getTax() {
		return this.tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public String getFounderName() {
		return founderName;
	}

	//=================<Land Management>=================
	/**
	 * Get total amount of land
	 * @return
	 */
	public int getLandAmount(){
		return land.size();
	}
	/**
	 * check if user is on the IslandHeart
	 * @param location
	 * @return
	 */
	public boolean isOnHeart(Location location){
		return this.heartLocation.getChunk().equals(location.getChunk());
	}
	/**
	 * set the home for the town and the heart chunk for the town
	 * @param location
	 */
	public void setHeartLocation(Location location){
		this.heartLocation = location;
	}
	public Location getHeartLocation(){
		return this.heartLocation;
	}
	/**
	 * register land to this town
	 * @param cd
	 */
	public void registerLand(ChunkData cd) {
		land.add(cd);
	}
	/**
	 * remove land from this town
	 * @param cd
	 */
	public void unRegisterLand(ChunkData cd) {
		land.remove(cd);
	}
	/**
	 * Get current land claimed
	 * @return
	 */
	public ArrayList<ChunkData> getLand() {
		return land;
	}
	public boolean isLandConnected(ChunkData land2) {
		//fixme you're checking more than necessary

		if(land.size() == 0){
			return true;
		}
		for(ChunkData l : land){
			if(l.isConnected(land2)){
				return true;
			}
		}
		return false;
	}
	//=============<Town Open>=============


	public Kingdom getKingdom(){
		return this.kingdom;
	}
	public void setKingdom(Kingdom k){
		if(this.kingdom != null){
			this.kingdom.unregisterTown(this);
		}
		if(k != null){
			k.registerTown(this);
		}
		this.kingdom = k;
	}

	/**
	 * wipe this town from existence
	 */
	public void remove() {
		Battle battle = getPlugin().getBattleManager().getDefendingBattle(this);
		if (battle !=null){
			battle.end();
		}
		this.getKingdom().unregisterTown(this);
		List<ChunkData> landCopy = new ArrayList<>(land);
		land.clear();
		for(ChunkData cd: landCopy){
			cd.setTown(null);
		}
		this.fm.deleteFile();
	}
	public static final String NameKey = "name", BankKey = "bank", FounderIDKey = "founderid", TownOpenKey = "townopen",
			HeartKey = "heart", InviteKey = "invite", KingdomKey = "kingdom", RankKey = "rank", CreateKey = "create", LastOnlineKey = "lastOnline";
	public void write() {
		this.dm.set(this.name, NameKey);
		this.dm.set(this.bank, BankKey);
		this.dm.set(this.createTimeStamp, CreateKey);
		this.dm.set(this.lastOnlineTimeStamp, LastOnlineKey);
		this.dm.set(this.founderId.toString(), FounderIDKey);
		dm.setLocation(this.heartLocation, HeartKey);
		this.dm.set(this.townOpen, TownOpenKey);
		if (this.kingdom != null){
			this.dm.set(this.kingdom.getId().toString(), KingdomKey);
		}else
			this.dm.set(null, KingdomKey);


	}
	public boolean save() {
		write();
		this.fm.save();
		return true;
	}


	@Override
	public int compareTo(Town o) {
		if(o == null) {
			return -1;
		}

		if(this.getLandAmount() > o.getLandAmount()) {
			return o.getLandAmount() - this.getLandAmount();
		}else {
			return (int) ((o.getBank() - this.getBank())*100);
		}
	}
	@Override
	public boolean equals(Object o) {
		return o instanceof Town && ((Town)o).getId().equals(this.getId());
	}
}
