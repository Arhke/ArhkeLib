package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author William Lin
 * @date 2/20/2021
 *
 */
public class PromoteCommand extends SubCommandsBase {


	public PromoteCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}
	public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
			NotInKingdomKey = "notInKingdom", NoRankPermKey = "noRankPerm", PlayerNotFoundKey = "playerNotFound",
			NoHigherRankKey = "noHigherRank", ToLeaderKey = "toLeader",	SuccessFeedBackKey = "successFeedBack",
			SuccessTargetKey = "successTarget", WrongArgsKey = "wrongArgs";

	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();
		if (!cs.isString(HelpKey))
			cs.set(HelpKey, "&6&l> {0}/f promote <player> &7- promote a player's rank in your kingdom");
		if (!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if (!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if (!cs.isString(NotInKingdomKey))
			cs.set(NotInKingdomKey, "&cYou are not in a kingdom.");
		if (!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYour rank in the kingdom does not allow you to promote a player in your kingdom");
		if (!cs.isString(PlayerNotFoundKey))
			cs.set(PlayerNotFoundKey, "&cNo Player with name {0} was found in your kingdom");
		if (!cs.isString(NoHigherRankKey))
			cs.set(NoHigherRankKey, "&cYou may not promote a player to or above your rank");
		if (!cs.isString(ToLeaderKey))
			cs.set(ToLeaderKey, "&cTarget player already highest rank possible. Use /f leader <Player> to promote him to leader");
		if (!cs.isString(SuccessFeedBackKey))
			cs.set(SuccessFeedBackKey, "&7{0} &6was promoted to &7{1}");
		if (!cs.isString(SuccessTargetKey))
			cs.set(SuccessTargetKey, "&cYou have been promoted by &7{0} &6New Rank: &7{1}");
		if (!cs.isString(WrongArgsKey))
			cs.set(WrongArgsKey, "Incorrect Number of Args. Usage: /f promote <Player>");

	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.promote")){
				p.sendMessage(this.dm.getTCM(NoPermKey));
				return true;
			}
			if(args.length == 2){
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
				if (!rank.checkPerm(Rank.RankPerm.PROMOTEKINGDOM)){
					p.sendMessage(this.dm.getTCM(NoRankPermKey));
					return true;
				}
				@SuppressWarnings("deprecated")
				OfflinePlayer promote = Bukkit.getOfflinePlayer(args[1]);
				if (!kingdom.getMembers().containsKey(promote.getUniqueId())){
					p.sendMessage(this.dm.getTCM(PlayerNotFoundKey, args[1]));
					return true;
				}
				Rank promoteRank = kingdom.getRank(promote.getUniqueId());
				if (promoteRank.getID()+1 >= rank.getID()){
					p.sendMessage(this.dm.getTCM(NoHigherRankKey));
					return true;
				}
				if(!kingdom.promote(promote.getUniqueId())){
					p.sendMessage(this.dm.getTCM(ToLeaderKey));
					return true;
				}
				String nr = kingdom.getRank(promote.getUniqueId()).getName();
				p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, promote.getName(),  nr));
				if (promote.getPlayer() != null && promote.getPlayer().isOnline()) {
					promote.getPlayer().sendMessage(this.dm.getTCM(SuccessTargetKey, u.getName(), nr));
				}

			}else{
				p.sendMessage(this.dm.getTCM(WrongArgsKey));
			}
			return true;
		}
		return false;
	}

	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.promote",
				HelpKey)};
	}

}
