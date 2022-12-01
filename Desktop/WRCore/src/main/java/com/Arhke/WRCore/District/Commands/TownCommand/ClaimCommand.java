package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader.ConfigFile;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.api.events.ClaimLandEvent;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.ChunkDataManager.WorldDataManager;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * 
 * @author William Lin
 * @date 2/24/2021
 */
public class ClaimCommand extends SubCommandsBase {

	public ClaimCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}

	public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData", NotInKingdomKey = "NotInKingdom",
	NoRankPermKey = "noRankPerm", NoClaimWorldKey = "noClaimWorld", BannedWorldKey = "bannedWorld", ChallengedKey = "challenged",
	AlreadyYourTownKey = "alreadyYourTown", AlreadyYourKingdomKey = "alreadyYourKingdom", ClaimedByOtherKey = "claimedByOther",
	ClaimedByOtherKingdomKey = "claimedByOtherKingdom", ProtectedLandKey = "protectedLand", SuccessMemberKey = "successMember",
	SuccessFeedBackKey = "successFeedBack", LandNotConnectedKey = "landNotConnected";
	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();

		if(!cs.isString(HelpKey))
			cs.set(HelpKey, "&6&l> {0}/t claim &7- claim a chunk of land for your town.");
		if(!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if(!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if(!cs.isString(NotInKingdomKey))
			cs.set(NotInKingdomKey, "&cYou are not in a town.");
		if(!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYour rank in the town does not allow you to claim land.");
		if(!cs.isString(NoClaimWorldKey))
			cs.set(NoClaimWorldKey, "&cYou can not claim land in this world.");
		if(!cs.isString(BannedWorldKey))
			cs.set(BannedWorldKey, "&cYou can not claim land in this world.");
		if(!cs.isString(BannedWorldKey))
			cs.set(BannedWorldKey, "&cYou can not claim land while your town is in battle.");
		if(!cs.isString(AlreadyYourTownKey))
			cs.set(AlreadyYourTownKey, "&eYour town already owns this land: {0}, {1}");
		if(!cs.isString(AlreadyYourKingdomKey))
			cs.set(AlreadyYourKingdomKey, "&eYour town already owns this land: {0}, {1}");
		if(!cs.isString(ClaimedByOtherKey))
			cs.set(ClaimedByOtherKey, "&c{0} currently owns this land.");
		if(!cs.isString(ClaimedByOtherKingdomKey))
			cs.set(ClaimedByOtherKingdomKey, "&c{0} currently owns this land.  Battle them to take their land.");
		if(!cs.isString(ProtectedLandKey))
			cs.set(ProtectedLandKey, "&cLand: &7{0}, {1} &ccan not be claimed.");
		if(!cs.isString(SuccessMemberKey))
			cs.set(SuccessMemberKey,  "&7{0}&e claimed land for your kingdom. &7({1}, {2})");
		if(!cs.isString(SuccessFeedBackKey))
			cs.set(SuccessFeedBackKey,  "&cNOTICE &e> &6Hourly land tax: &7${0}");
		if(!cs.isString(LandNotConnectedKey))
			cs.set(LandNotConnectedKey, "&cYou may only claim land that is connected to your town claims");
	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.claim")){
				p.sendMessage(this.dm.getTCM(NoPermKey));
				return true;
			}
			TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
			if(u == null){
				p.sendMessage(this.dm.getTCM(NoUserDataKey));
				return true;
			}
			if(u.getKingdom() == null){
				p.sendMessage(this.dm.getTCM(NotInKingdomKey));
				return true;
			}
			Kingdom kingdom = u.getKingdom();
			Rank rank = kingdom.getRank(u.getID());
			if(!rank.checkPerm(Rank.RankPerm.CLAIM)){
				p.sendMessage(this.dm.getTCM(NoRankPermKey));
				return true;
			}
			String world = p.getWorld().getName();
			ConfigManager cm = getPlugin().getConfig(ConfigFile.DISTRICTCONFIG);
			for(String s : cm.getStringList("noClaimWorlds")){
				if(world.equalsIgnoreCase(s)){
					p.sendMessage(this.dm.getTCM(NoClaimWorldKey));
					return true;
				}
			}
			for(String s : cm.getStringList("bannedWorlds")){
				if(world.equalsIgnoreCase(s)){
					p.sendMessage(this.dm.getTCM(BannedWorldKey));
					return true;
				}
			}
			Chunk chunk = p.getLocation().getChunk();
			ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
			if (cd.getTown() != null) {
				if (cd.getTown().getKingdom().equals(u.getKingdom())) {
					p.sendMessage(this.dm.getTCM(AlreadyYourTownKey, cd.getX(), cd.getZ()));
				} else {
					p.sendMessage(this.dm.getTCM(ClaimedByOtherKey, cd.getTown().getName()));
				}
				return true;
			}
			if (getPlugin().getBattleManager().getDefendingBattle(cd.getTown())!= null) {
				p.sendMessage(this.dm.getTCM(ChallengedKey));
				return true;
			}
			int x = chunk.getX(), z = chunk.getZ();
			WorldDataManager wdm = getPlugin().getWorldDataManager();
			Town town;
			if ((town = wdm.getOrNewChunkData(p.getLocation().getWorld(), x+1, z).getTown()) != null && town.getKingdom().equals(u.getKingdom())){
			}else if ((town = wdm.getOrNewChunkData(p.getLocation().getWorld(), x, z+1).getTown()) != null && town.getKingdom().equals(u.getKingdom())){
			}else if ((town = wdm.getOrNewChunkData(p.getLocation().getWorld(), x, z-1).getTown()) != null && town.getKingdom().equals(u.getKingdom())){
			}else if ((town = wdm.getOrNewChunkData(p.getLocation().getWorld(), x-1, z).getTown()) != null && town.getKingdom().equals(u.getKingdom())){
			}else{
				p.sendMessage(this.dm.getTCM(LandNotConnectedKey));
				return true;
			}
			if(getPlugin().getHook().isLandProtected(p, cd)){
				p.sendMessage(this.dm.getTCM(ProtectedLandKey, cd.getX(), cd.getZ()));
				return true;
			}
			ClaimLandEvent event = new ClaimLandEvent(town, u, cd);
			Bukkit.getServer().getPluginManager().callEvent(event);

			if(event.isCancel()){
				return true;
			}
			cd.show(p);
			cd.setTown(town);
			try {
				town.save();
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
			town.getKingdom().messageMembers(this.dm.getTCM(SuccessMemberKey, p.getName(), cd.getX(), cd.getZ()), false);
//			p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, town.getLand().size()*getPlugin().getConfig(ConfigFile.DISTRICTCONFIG).getDouble("town.land.tax")) + "");
			return true;

		}
		return false;
	}



	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.claim", 
				HelpKey)};
	}

}
