package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.Commands.Arg;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.api.events.ClaimLandEvent;
import com.Arhke.WRCore.District.api.events.UnclaimLandEvent;
import com.Arhke.WRCore.District.core.Feudal;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author William Lin
 * @date 2/24/2021
 */
public class UnclaimCommand extends SubCommandsBase {

	public UnclaimCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}

	public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData", NotInTownKey = "NotInKingdom",
	NoRankPermKey = "noRankPerm", NoClaimWorldKey = "noClaimWorld", BannedWorldKey = "bannedWorld", ChallengedKey = "challenged",
	NotYourTownKey = "notYourTown", HeartChunkKey = "heartChunk", SuccessFeedBackKey = "successFeedBack";
	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();

		if(!cs.isString(HelpKey))
			cs.set(HelpKey, "&6&l> {0}/t unclaim &7- unclaim a chunk of land from your town");
		if(!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if(!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if(!cs.isString(NotInTownKey))
			cs.set(NotInTownKey, "&cYou are not in a town.");
		if(!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYour rank in the town does not allow you unclaim land");
		if(!cs.isString(NoClaimWorldKey))
			cs.set(NoClaimWorldKey, "&cYou can not unclaim land in this world.");
		if(!cs.isString(BannedWorldKey))
			cs.set(BannedWorldKey, "&cYou can not unclaim land in this world.");
		if(!cs.isString(BannedWorldKey))
			cs.set(BannedWorldKey, "&cYou can not unclaim land while your kingdom is challenged.");
		if(!cs.isString(NotYourTownKey))
			cs.set(NotYourTownKey, "&cYour town does not own this land.");
		if(!cs.isString(SuccessFeedBackKey))
			cs.set(SuccessFeedBackKey, "&eUnclaimed land: {0}, {1}");
		dm.isOrDefault("&cCannot Unclaim Heart Chunk.", HeartChunkKey);

	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.unclaim")){
				p.sendMessage(this.dm.getTCM(NoPermKey));
				return true;
			}
			TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
			if(u == null){
				p.sendMessage(this.dm.getTCM(NoUserDataKey));
				return true;
			}
			if(u.getKingdom() == null){
				p.sendMessage(this.dm.getTCM(NotInTownKey));
				return true;
			}
			Kingdom kingdom = u.getKingdom();
			Rank rank = kingdom.getRank(u.getID());
			if(!rank.checkPerm(Rank.RankPerm.UNCLAIM)){
				p.sendMessage(this.dm.getTCM(NoRankPermKey));
				return true;
			}
			ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
			if (cd.getTown() == null){
				p.sendMessage(this.dm.getTCM(NotYourTownKey));
				return true;
			}
			Town town = cd.getTown();
			if (!town.getKingdom().equals(kingdom)){
				p.sendMessage(this.dm.getTCM(NotYourTownKey));
				return true;
			}
			if (getPlugin().getBattleManager().getDefendingBattle(town) != null) {
				p.sendMessage(this.dm.getTCM(ChallengedKey));
				return true;
			}
			Town townLand = cd.getTown();
			if (town.equals(townLand)){
				p.sendMessage(this.dm.getTCM(NotYourTownKey));
				return true;
			}
			if(cd.isIn(townLand.getHeartLocation())){
				p.sendMessage(this.dm.getTCM(HeartChunkKey));
				return true;
			}
			cd.setTown(null);
			UnclaimLandEvent event = new UnclaimLandEvent(town, u, cd);
			Bukkit.getServer().getPluginManager().callEvent(event);


			p.sendMessage(this.dm.getTCM(SuccessFeedBackKey,cd.getX(),cd.getZ()));
			return true;
		}
		return false;
	}



	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.unclaim",
				HelpKey)};
	}


}
