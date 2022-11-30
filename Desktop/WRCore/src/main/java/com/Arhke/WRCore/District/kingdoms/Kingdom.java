package com.Arhke.WRCore.District.kingdoms;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.District.util.OnlineTime;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

/**
 *
 * @author William Lin
 *
 */
public class Kingdom extends MainBase {
	private FileManager fm;
	private DataManager dm;
	private UUID id;
	private HashMap<UUID, Rank> members = new HashMap<>(); //UUID, rank
	private List<Town> towns = new ArrayList<>();
	private boolean[] banTime = new boolean[24];
	private long createDate;
	private long lastOnline;
	private boolean online = false;

	private short challengesWon = 0;
	private short challengesLost = 0;

	private List<UUID> allies = new ArrayList<>();//Kingdom UUID
	private Set<UUID> wishes = new HashSet<>();// KingdomUUID, WISH - Wishes are requests for ally, truce, or neutral. Wishes last until plugin reset.
	//0 for neutral, 1 for allies.
	private HashSet<UUID> invites = new HashSet();
	private boolean kingdomOpen;
	private List<String> kingdomLogs = new ArrayList<>();

	private volatile boolean change = false;
	private volatile boolean changeMembers = false;
	private volatile boolean changeLand = false;
	private volatile boolean changeRelations = false;


	/**
	 * Used for creating a new kingdom. Call save directly after calling this.
	 *
	 *
	 * @throws Exception
	 */
	public Kingdom(Main instance, FileManager fm, UUID uuid, Town town) throws Exception{
		super(instance);
		this.fm = fm;
		this.dm = fm.getDataManager();
		this.id = uuid;
		town.setKingdom(this);
		this.createDate = System.currentTimeMillis();
		this.lastOnline = System.currentTimeMillis();
	}
	public static final String MembersKey = "Members", BanTimeKey = "BanTime",
			CreateDateKey = "CreateDate", LastOnlineKey = "LastOnline",ChallengesWonKey = "ChallengesWon",
			ChallengesLostKey = "ChallengesLost", AlliesKey = "Allies", KingdomLogKey = "KingdomLogs";
	/**
	 * Load kingdom from config
	 * @throws Exception
	 */
	public Kingdom(Main instance, FileManager fm) throws Exception{
		super(instance);
		this.fm = fm;
		this.dm = fm.getDataManager();
		try {
			this.id = UUID.fromString(fm.getFileNameNoExt());
			DataManager memberDM = dm.getDataManager(MembersKey);
			for(String key: memberDM.getConfig().getKeys(false)){
				Rank rank = Rank.getRank(memberDM.getInt(-1, key));
				if(rank == null){
					continue;
				}
				this.members.put(UUID.fromString(key), rank);

			}
			DataManager banTimeDM = dm.getDataManager(BanTimeKey);
			for (int i = 0; i < banTime.length;i++){
				this.banTime[i] = banTimeDM.getBoolean(i+"");
			}
			this.createDate = dm.getLong(CreateDateKey);
			this.lastOnline = dm.getLong(LastOnlineKey);
			this.challengesWon = (short)dm.getInt(ChallengesWonKey);
			this.challengesLost = (short)dm.getInt(ChallengesLostKey);


			dm.getStringList(AlliesKey).forEach((ally)->this.allies.add(UUID.fromString(ally)));
			this.kingdomLogs = dm.getStringList(KingdomLogKey);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	//=======<Getter and Setters>======
	public UUID getId() {
		return this.id;
	}

	/**
	 * Get kingdom name
	 * @return
	 */
	public String getName() {
		return this.towns.get(0).getName();
	}

	public List<UUID> getAlliesList() {
		return allies;
	}

	//========<Member>=========
	/**
	 * register the user in the user list in the kingdom
	 * assumes member rank
	 * @param user
	 */
	public void registerMember(TUser user){
		this.registerMember(user, Rank.GUEST);
	}
	/**
	 * register the user in the user list in the kingdom
	 * autocorrects the kingdom value for the town if that info is not correct
	 * @param user
	 */
	public void registerMember(TUser user, Rank rank){
		this.members.putIfAbsent(user.getID(), rank);
	}
	/**
	 * unregister the user in the user list in the kingdom
	 * @param user
	 */
	public void unregisterMember(@NotNull TUser user){
		this.members.remove(user.getID());
	}


	/**
	 * Get the kingdom rank of a player with their uuid. If the member is not registered somehow
	 * leave an error, and autofill the rank as guest
	 * @param uuid
	 * @return Member rank if member rank was found, Guest if entry not found
	 */
	public Rank getRank(UUID uuid) {
		return members.get(uuid);
	}
	/**
	 *  Sets the Kingdom Rank of a Player with their UUID
	 * @param uuid
	 * @param rank
	 */
	public void setRank(UUID uuid, Rank rank) {
		warn(rank.getName());
		members.put(uuid, rank);
		changeMembers = true;
	}

	/**
	 * Change leaders
	 * @param promote
	 */
	public void setLeader(TUser promote) {
		this.members.entrySet().stream().filter((entry)->entry.getValue().equals(Rank.LEADER))
				.forEach((entry)->entry.setValue(Rank.EXECUTIVE));
		members.put(promote.getID(), Rank.LEADER);




		changeMembers = true;
		change = true;
	}
	/**
	 * Get hashmap of members. uuid, rank
	 * @return
	 */
	public HashMap<UUID, Rank> getMembers() {
		return members;
	}
	public List<Player> getOnlinePlayers(){
		List<Player> playerK1 = new ArrayList<>();
		for(final UUID mem : members.keySet()){
			OfflinePlayer offP = Bukkit.getOfflinePlayer(mem);
			if(offP.isOnline()){
				playerK1.add(offP.getPlayer());
			}
		}
		return playerK1;
	}
	/**
	 * Promote a member (does not check for perms)
	 * @param uuid
	 * @return returns if the operation was successful (e.g. promoting people up to executive)
	 */
	public boolean promote(UUID uuid) {
		Rank rank = members.get(uuid);
		if(rank == null) {
			return false;
		}
		rank = Rank.getRank(rank.getID() + 1);
		if (rank == Rank.LEADER) {
			return false;
		}
		members.put(uuid, rank);
		changeMembers = true;
		return true;
	}
	/**
	 * Demote a member
	 * @param uuid
	 * @return returns if the operation was successful(e.g. not demoting people under guest )
	 */
	public boolean demote(UUID uuid) {
		Rank rank = members.get(uuid);
		if(rank == null) {
			return false;
		}
		rank = Rank.getRank(rank.getID() - 1);
		if (rank == null) {
			return false;
		}
		members.put(uuid, rank);
		changeMembers = true;
		return true;
	}

	//==========<Invites>===========
	/**
	 * Add user to invite list.
	 * @param uuid
	 */
	public void invite(UUID uuid) {
		invites.add(uuid);
	}
	/**
	 * Removed invited person
	 * @param uuid
	 */
	public void unInvite(UUID uuid) {
		invites.remove(uuid);
	}
	/**
	 * Check if a player has been invited with their uuid.
	 * @param uuid
	 * @return
	 */
	public boolean hasInvited(UUID uuid) {
		return invites.contains(uuid);
	}
	/**
	 * Check if player was invited or is allowed to join.
	 * @param uuid
	 * @return whether or not player can join
	 */
	public boolean canJoin(UUID uuid) {
		return this.kingdomOpen || hasInvited(uuid);
	}

	//=======<Register Towns>=======
	/**
	 * adds the town to the town list of this kingdom
	 * @param town
	 */
	public void registerTown(Town town) {
		this.towns.add(town);
	}
	/**
	 * removes the town from the town list of the kingdom
	 * and unregisters the members from the kingdom list
	 * @param town
	 */
	public void unregisterTown(Town town) {
		this.towns.remove(town);
	}
	/**
	 * Gets the town list from the specified Kingdom
	 * @return
	 */
	public List<Town> getTowns(){
		return this.towns;
	}


	public void setKingdomOpen(boolean open){
		this.kingdomOpen = open;
	}
	public boolean isKingdomOpen(){
		return this.kingdomOpen;
	}

	/**
	 * Remove all of the towns from the kingdom and unregister town
	 */
	public void removeAllTowns() {
		for (Town town:this.towns){
			town.setKingdom(null);
		}
		towns.clear();
	}
	//==========<Utility>============
	public int howManyHoursBanned() {
		int count = 0;
		for(boolean b:banTime)
			if (b)
				count++;
		return count;
	}
	public boolean getBanTime(int time){
		while(time < 0){
			time += 24;
		}
		return this.banTime[time%24];
	}
	public boolean[] getBanTime(){
		return this.banTime;
	}
	public boolean setBanTime(int time){
		while(time < 0){
			time += 24;
		}

		boolean org = this.banTime[time%24];
		return this.banTime[time%24] = !org;
	}
	public boolean canBanTime(int time){
		while(time < 0){
			time += 24;
		}time%=24;
		boolean[] temp = Arrays.copyOf(this.banTime, 24);
		temp[time] = !temp[time];
		for(int i = 0; i < temp.length; i++){
			if(temp[i]){
				continue;
			}
			if(!temp[i+1%24] && !temp[i+2%24]){
				return true;
			}
		}
		return false;
	}
	/**
	 * Message all members
	 * @param string
	 * @param save
	 */
	public void messageMembers(final String string, boolean save) {
		for(final UUID mem : members.keySet()){
			try {
				OfflinePlayer player = Bukkit.getOfflinePlayer(mem);
				if(player.isOnline()){
					player.getPlayer().sendMessage(string);
				}else if(save){
					new Thread(new Runnable() {
						public void run() {
							TUser user = getPlugin().getTUserManager().tempGetTUser(mem);
							if(user != null) {
								user.sendOfflineMessage(string);
								try{
									user.save();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
					}).start();
				}
			}catch(Exception e) {
				ErrorManager.error(146392018, e);
			}
		}
	}
	/**
	 * Get kingdom's last online time in ms.
	 * @return
	 */
	public long getLastOnline() {
		return lastOnline;
	}
	/**
	 * Check if the kingdom is currently online.
	 */
	public void checkOnline() {
		boolean o = false;
		for(UUID id: members.keySet()){
			Player p = Bukkit.getPlayer(id);
			if(p != null && p.isOnline()){
				o = true;
				break;
			}
		}
		online = o;
		if(o){
			lastOnline = System.currentTimeMillis();
		}
		change = true;
	}
	/**
	 * Returns if the kingdom is online.  Different from checkOnline() because this can only be used after checkOnline() checks.
	 * @return
	 */
	public boolean isOnline(){
		return online;
	}





	/**
	 * Delete this kingdom with name being the person who caused the deletion.
	 */
	public void delete(){
		this.towns.forEach((town)-> town.setKingdom(null));
		fm.deleteFile();
	}



	/**
	 * Add kingdom as ally
	 * @param kingdom
	 */
	public boolean ally(Kingdom kingdom) {
		if (isAllied(kingdom)){
			return false;
		}
		this.allies.add(kingdom.getId());
		changeRelations = true;
		return true;
	}
	/**
	 * Set kingdom as neutral
	 * @param kingdom
	 */
	public boolean neutral(Kingdom kingdom) {
		if (!isAllied(kingdom)){
			return false;
		}
		allies.remove(kingdom.getId());
		changeRelations = true;
		return true;
	}

	/**
	 * Check if allied
	 * @param other
	 * @return
	 */
	public boolean isAllied(Kingdom other) {
		return this.allies.contains(other.getId());
	}


	//============<Wishes & Requests>===========
	/**
	 * Request kingdom to be allies
	 * @param requester
	 */
	public void requestAlly(Kingdom requester) {
		if(isAllied(requester)){
			return;
		}
		wishes.add(requester.getId());
	}

	/**
	 * @param other
	 * @return returns whether or not the specified kingdom has already requested ally
	 * with this kingdom
	 */
	public boolean hasAllyRequestFrom(Kingdom other){
		return this.wishes.contains(other.getId());
	}

	/**
	 * Get all member's online times.
	 * @return
	 */
	public ArrayList<OnlineTime> getMembersOnlineTime() {
		ArrayList<OnlineTime> time = new ArrayList<>();
		for(UUID uuid : members.keySet()){
			if(uuid != null){
				TUser u = getPlugin().getTUserManager().tempGetTUser(uuid);
				if(u != null){
					time.add(u.getOnlineTime());
				}
			}
		}
		return time;
	}

	/**
	 * wipe this kingdom from existence
	 */
	public void remove() {
		this.getTowns().clear();
		Set<UUID> membersCopy = new HashSet<>(this.getMembers().keySet());
		this.getMembers().clear();
		membersCopy.forEach((entry)->{ TUser u = getPlugin().getTUserManager().tempGetTUser(entry);
		if (u == null)return;
		u.setKingdom(null);
		});

		this.fm.deleteFile();
	}

	/**
	 * Get number of members online
	 * @return
	 */
	public Integer getOnline() {
		int ret = 0;
		return ret;
	}

	/**
	 * Check if a location is on the kingdom land.
	 * @param location
	 * @return
	 */
	public boolean isOnLand(Location location) {
		ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(location);
		return cd.getTown() != null && this.equals(cd.getTown().getKingdom());
	}



	private String getLastOnlineTime() {
		if(online){
//			return Feudal.getMessage("landUpdate.onlineNow");
		}
		long rem = System.currentTimeMillis() - lastOnline;
		long years = rem / 31557600000L;
		long weeks = (rem - (years * 31557600000L)) / 604800000L;
		long days = (rem-(years * 31557600000L)-(weeks * 604800000L)) / 86400000;
		long hours = ((rem - (days * 86400000) - (years * 31557600000L)-(weeks * 604800000L)) / 3600000);
		long minutes = (rem - (days * 86400000) - (hours * 3600000)-(years * 31557600000L)-(weeks * 604800000L)) / 60000;
		long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)-(years * 31557600000L)-(weeks * 604800000L)) / 1000;
		if(years == 0){
			if(weeks == 0){
//				return days + (days == 1 ? Feudal.getMessage("landUpdate.day") : Feudal.getMessage("landUpdate.days")) + " " + hours + ":" + minutes + ":" + seconds;
			}else{
//				return weeks + (weeks == 1 ? Feudal.getMessage("landUpdate.week") : Feudal.getMessage("landUpdate.weeks")) + ", " + days + (days == 1 ? Feudal.getMessage("landUpdate.day") : Feudal.getMessage("landUpdate.days")) + " " + hours + ":" + minutes + ":" + seconds;
			}
		}else{
//			return years + (years == 1 ? Feudal.getMessage("landUpdate.year") : Feudal.getMessage("landUpdate.years")) + ", " + weeks + (weeks == 1 ? Feudal.getMessage("landUpdate.week") : Feudal.getMessage("landUpdate.weeks")) + ", " + days + (days == 1 ? Feudal.getMessage("landUpdate.day") : Feudal.getMessage("landUpdate.days")) + " " + hours + ":" + minutes + ":" + seconds;
		}
		return  "";
	}


	public String getMembersDisplay() {
		StringBuilder ret = new StringBuilder();
		for(Map.Entry<UUID, Rank> entry: members.entrySet()){
			Rank rank = entry.getValue();
			if(ret.toString().length() != 0) {
				ret.append("&7, ");
			}
			if(rank.equals(Rank.LEADER)){
				ret.append("&bL:");
			}else if(rank.equals(Rank.EXECUTIVE)){
				ret.append("&aE:");
			}else if(rank.equals(Rank.MEMBER)){
				ret.append("&2M:");
			}else{
				ret.append("&eG:");
			}
			ret.append("&c");
			try {
				OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
				if(player != null){
					if(player.isOnline()){
						ret.append("&a");
					}
					ret.append(player.getName());

				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ret.toString();
	}










	/**
	 * Check if kingdom is protected or in a challenge fight.
	 * @return
	 */
	public boolean hasProtection() {
//		for(Challenge w : getrPlugin){
//			if(c.isFighting() && c.getDefender().equals(this)){
//				return false;
//			}
//		}
		return true;
	}

	//====================<Challenges>=====================
	public int getChallengesWon() {
		return challengesWon;
	}
	protected void setChallengesWon(int challengesWon) {
		this.challengesWon = (short) challengesWon;
		change = true;
	}
	public int getChallengesLost() {
		return challengesLost;
	}
	protected void setChallengesLost(int challengesLost) {
		this.challengesLost = (short) challengesLost;
		change = true;
	}


	protected double getPercentMembersOnline() {
		double x = 0;
		for(UUID uuid : members.keySet()){
			Player p = Bukkit.getPlayer(uuid);
			if(p != null && p.isOnline()){
				x++;
			}
		}
		return x/members.size()*100;
	}











	public long getCreateDate(){
		return createDate;
	}


	//SQL Stuff
	public void setChange(boolean b) {
		change = b;
	}
	public boolean isChange() {
		return change;
	}
	public boolean isChangeMembers() {
		return changeMembers;
	}
	public boolean isChangeLand() {
		return changeLand;
	}
	public boolean isChangeRelations() {
		return changeRelations;
	}
	public void setChangeMembers(boolean changeMembers) {
		this.changeMembers = changeMembers;
	}
	public void setChangeLand(boolean changeLand) {
		this.changeLand = changeLand;
	}
	public void setChangeRelations(boolean changeRelations) {
		this.changeRelations = changeRelations;
	}
	/**
	 * Save kingdom to config
	 * @throws Exception
	 */
	public void save(){



		this.setChange(false);
		this.setChangeMembers(false);
		this.setChangeLand(false);
		this.setChangeRelations(false);

		DataManager mem = dm.getDataManager(MembersKey);
		for(Map.Entry<UUID, Rank> entry: members.entrySet()){
			mem.set(entry.getValue().getID(), entry.getKey().toString());
		}
		DataManager banTimeDM = dm.getDataManager(BanTimeKey);
		for (int i = 0; i < banTime.length;i++){
			banTimeDM.set(banTime[i], i+"");
		}
		dm.set(this.createDate, CreateDateKey);
		dm.set(this.lastOnline, LastOnlineKey);
		dm.set(this.challengesWon, ChallengesWonKey);
		dm.set(this.challengesLost, ChallengesWonKey);


		ArrayList<String> allyArrayList = new ArrayList<>();
		this.allies.forEach((a)->allyArrayList.add(a.toString()));
		dm.set(allyArrayList, AlliesKey);
		dm.set(this.kingdomLogs, KingdomLogKey);
		fm.save();
	}


}
