package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.Commands.Arg;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.core.Feudal;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.util.Utils;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * 
 * @author William Lin
 * @date 2/28/2021
 *
 */
public class SpawnCommand extends SubCommandsBase {
	public SpawnCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}
	public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData", NotInTownKey = "NotInKingdom",
	SpawnInKingdomOrTownKey = "spawnInKingdomOrTown", SetSpawnFeedBackKey = "setSpawnFeedback", NoRankPermKey = "noRankPerm";



	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();

		if(!cs.isString(HelpKey))
			cs.set(HelpKey, "&6&l> {0}/t spawn &7- set your town spawn point at your current position");
		if(!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if(!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if(!cs.isString(NotInTownKey))
			cs.set(NotInTownKey, "&cYou are not in a town.");
		if(!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYour rank in the town does not allow you to set the home for the town");
		if(!cs.isString(SpawnInKingdomOrTownKey))
			cs.set(SpawnInKingdomOrTownKey, "&cYou may place your user spawn only on your town land or your kingdom land");
		if(!cs.isString(SetSpawnFeedBackKey))
			cs.set(SetSpawnFeedBackKey, "&6You have successfully set your respawn point to that position.");

	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.spawn")){
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
			if(!rank.checkPerm(Rank.RankPerm.SETHOME)){
				p.sendMessage(this.dm.getTCM(NoRankPermKey));
				return true;
			}
			ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
			if (cd.getTown() == null || !cd.getTown().getKingdom().equals(u.getKingdom())){
				p.sendMessage(this.dm.getTCM(SpawnInKingdomOrTownKey));
				return true;
			}
			Location loc = p.getLocation().clone();
			loc.setY(Math.max(loc.getY(), 63));
			loc.setY(Math.min(loc.getY(), 130));
			p.setCompassTarget(loc);
			p.setBedSpawnLocation(loc);
			p.sendMessage(this.dm.getTCM(SetSpawnFeedBackKey));
			cd.getTown().setHeartLocation(loc);
			return true;
		}
		return false;
	}

	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.home", 
				HelpKey)};
	}

}
