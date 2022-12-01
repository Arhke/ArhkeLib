package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * 
 * @author William Lin
 * @date 2/22/2021
 *
 */
public class NeutralCommand extends SubCommandsBase {
	public NeutralCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}
	public static final String HelpKey = "help", NoPermKey = "noPerm", WrongArgsKey = "wrongArgs", NoUserDataKey = "noUserData",
			NotInKingdomKey = "noKingdom",	NoRankKey = "noRank", NoRankPermKey = "noRankPerm", NoKingdomFoundKey = "noKingdomFound",
			NeutralSelfKey = "neutralSelf", AlreadyRequestedKey = "alreadyRequested", SuccessNeutralKey = "successNeutral",
			SuccessNeutralMemberKey = "successAlliedMember",
			SuccessRequestedMemberKey = "successRequestedMember", SuccessRequestedKey = "successRequested", AlreadyNeutralKey = "alreadyNeutral";



	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();
		if (!cs.isString(HelpKey))
			cs.set(HelpKey,  "&6&l> {0}/f neutral <kingdom> &7- set a kingdom as an ally");
		if(!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if(!cs.isString(WrongArgsKey))
			cs.set(WrongArgsKey, "&cEnter a kingdom to set as an neutral.");
		if(!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if(!cs.isString(NotInKingdomKey))
			cs.set(NotInKingdomKey, "&cYou are not in a kingdom.");
		if (!cs.isString(NoRankKey))
			cs.set(NoRankKey, "&cError getting rank for your kingdom.");
		if (!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYour rank in your kingdom does not allow you to set kingdoms as neutral.");
		if(!cs.isString(NoKingdomFoundKey))
			cs.set(NoKingdomFoundKey, "&cThat kingdom does not exist.");
		if(!cs.isString(NeutralSelfKey))
			cs.set(NeutralSelfKey, "&cYou can not neutral yourself.");
		if(!cs.isString(AlreadyRequestedKey))
			cs.set(AlreadyRequestedKey, "&aYou have already sent an neutral request to that kingdom");
		if(!cs.isString(SuccessNeutralKey))
			cs.set(SuccessNeutralKey, "&aYour Kingdom is now neutral with {0}");
		if (!cs.isString(SuccessNeutralMemberKey))
			cs.set(SuccessNeutralMemberKey, "&aYour kingdom is now neutral with {0}");
		if(!cs.isString(SuccessRequestedKey))
			cs.set(SuccessRequestedKey, "&aNeutral request sent.");
		if(!cs.isString(SuccessRequestedMemberKey))
			cs.set(SuccessRequestedMemberKey, "&a{0} has requested to be allies with your kingdom."
					+" Do /f neutral {0} to accept their request.");
		if(!cs.isString(AlreadyNeutralKey))
			cs.set(AlreadyNeutralKey, "&cYou are already neutral with that kingdom.");


	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.neutral")){
				p.sendMessage(NoPermKey);
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
				if (!rank.checkPerm(Rank.RankPerm.NEUTRAL)){
					p.sendMessage(this.dm.getTCM(NoRankPermKey));
					return true;
				}
				Town town = getPlugin().getTownsManager().getTown(args[1]);
				if (town == null) {
					p.sendMessage(this.dm.getTCM(NoKingdomFoundKey));
					return true;
				}
				Kingdom neutral = town.getKingdom();
				if (neutral.equals(kingdom)) {
					p.sendMessage(this.dm.getTCM(NeutralSelfKey));
					return true;
				}
				if (kingdom.isAllied(neutral)) {
					neutral.messageMembers(this.dm.getTCM(SuccessNeutralMemberKey, kingdom.getName()), false);
					kingdom.messageMembers(this.dm.getTCM(SuccessNeutralMemberKey, neutral.getName()), false);
					neutral.neutral(kingdom);
					kingdom.neutral(neutral);
					getPlugin().getNameTag().setKingdomsToNeutrals(kingdom.getOnlinePlayers(), neutral.getOnlinePlayers());
				}
				else {
					p.sendMessage(this.dm.getTCM(AlreadyNeutralKey));
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
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.neutral", 
				"commandHelp.92")};
	}

}
