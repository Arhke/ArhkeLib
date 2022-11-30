package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.Commands.Arg;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.core.Feudal;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import com.sun.net.httpserver.Authenticator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * //first one done.
 * @author William Lin
 * @date 3/1/2021
 *
 */
public class AllyCommand extends SubCommandsBase {
	public AllyCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}
	public static final String NoPermKey = "noPerm", WrongArgsKey = "wrongArgs", NoUserDataKey = "noUserData",
	NoKingdomKey = "noKingdom",	NoRankKey = "noRank", NoRankPermKey = "noRankPerm", NoKingdomFoundKey = "noKingdomFound",
	AllySelfKey = "allySelf", AlreadyAlliedKey = "alreadyAllied", AlreadyRequestedKey = "alreadyRequested",
	SuccessAlliedKey = "successAllied", SuccessAlliedMemberKey = "successAlliedMember",
	SuccessRequestedMemberKey = "successRequestedMember", SuccessRequestedKey = "successRequested",
	HelpKey = "help";
	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();
		if (!cs.isString(HelpKey))
			cs.set(HelpKey,  "&6&l> {0}/t ally <kingdom> &7- set a kingdom as an ally");
		if(!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if(!cs.isString(WrongArgsKey))
			cs.set(WrongArgsKey, "&cEnter a kingdom to set as an ally.");
		if(!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if(!cs.isString(NoKingdomKey))
			cs.set(NoKingdomKey, "&cYou are not in a kingdom.");
		if (!cs.isString(NoRankKey))
			cs.set(NoRankKey, "&cError getting rank for your kingdom.");
		if (!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYou have permissions in your kingdom to do this.");
		if(!cs.isString(NoKingdomFoundKey))
			cs.set(NoKingdomFoundKey, "&cThat kingdom does not exist.");
		if(!cs.isString(AllySelfKey))
			cs.set(AllySelfKey, "&cYou can not ally yourself.");
		if(!cs.isString(AlreadyAlliedKey))
			cs.set(AlreadyAlliedKey, "&cYou are already allied with that kingdom.");
		if(!cs.isString(AlreadyRequestedKey))
			cs.set(AlreadyRequestedKey, "&aYou have already sent an ally request to that kingdom");
		if(!cs.isString(SuccessAlliedKey))
			cs.set(SuccessAlliedKey, "&aYour Kingdom is now allied with {0}");
		if (!cs.isString(SuccessAlliedMemberKey))
			cs.set(SuccessAlliedMemberKey, "&aYour kingdom is now allied with {0}");
		if(!cs.isString(SuccessRequestedMemberKey))
			cs.set(SuccessRequestedMemberKey, "&a{0} has requested to be allies with your kingdom."
					+" Do /t ally {0} to accept their request.");
		if(!cs.isString(SuccessRequestedKey))
			cs.set(SuccessRequestedKey, "&aAlly request sent.");

	}

	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}
	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.ally")){
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
					p.sendMessage(this.dm.getTCM(NoKingdomKey));
					return true;
				}
				Kingdom kingdom = u.getKingdom();
				Rank rank = kingdom.getRank(p.getUniqueId());
				if(rank == null){
					p.sendMessage(this.dm.getTCM(NoRankKey));
					return true;
				}
				if(!rank.checkPerm(Rank.RankPerm.ALLY)) {
					p.sendMessage(this.dm.getTCM(NoRankPermKey));
					return true;
				}
				Town town = getPlugin().getTownsManager().getTown(args[1]);
				if (town == null) {
					p.sendMessage(this.dm.getTCM(NoKingdomFoundKey));
					return true;
				}
				Kingdom other = town.getKingdom();
				if (other.equals(kingdom)) {
					p.sendMessage(this.dm.getTCM(AllySelfKey));
					return true;
				}
				if (kingdom.isAllied(other)) {
					p.sendMessage(this.dm.getTCM(AlreadyAlliedKey));
					return true;
				}
				if (other.hasAllyRequestFrom(kingdom)) {
					p.sendMessage(this.dm.getTCM(AlreadyRequestedKey));
					return true;
				}
				if (kingdom.hasAllyRequestFrom(other)) {
					other.ally(kingdom);
					other.messageMembers(this.dm.getTCM(SuccessAlliedMemberKey, kingdom.getName()), false);
					kingdom.ally(other);
					kingdom.messageMembers(this.dm.getTCM(SuccessAlliedMemberKey, other.getName()), false);
					getPlugin().getNameTag().setKingdomsToNeutrals(kingdom.getOnlinePlayers(), other.getOnlinePlayers());
				} else {
					other.requestAlly(kingdom);
					other.messageMembers(this.dm.getTCM(SuccessRequestedMemberKey, kingdom.getName()), false);
					p.sendMessage(this.dm.getTCM(SuccessRequestedKey));
				}
				return true;
			}
			else{
				p.sendMessage(this.dm.getTCM(WrongArgsKey));
			}
			return true;
		}
		return false;
	}



	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("districts.commands.user.kingdoms.ally",
				this.dm.getTCM(HelpKey))};
	}

}
