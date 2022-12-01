package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * 
 * @author William Lin
 * @date 2/28/2021
 *
 */
public class SetOpenCommand extends SubCommandsBase {

	public SetOpenCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}
	public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData", NotInTownKey = "NotInKingdom",
	NoRankPermKey = "noRankPerm", FeedBackOpenKey = "feedbackOpen", FeedBackCloseKey = "feedbackClose", FeedBackKey = "feedback",
	InvalidUsageKey = "invalidUsage", SuccessFeedBackKey = "successFeedBack";


	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();
		if (!cs.isString(HelpKey))
			cs.set(HelpKey, "&6&l> {0}/f setOpen <true/false> &7- make it possible for anyone to join your town without an invite");
		if (!cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if (!cs.isString(NoUserDataKey))
			cs.set(NoUserDataKey, "&cFailed to load your user data.");
		if (!cs.isString(NotInTownKey))
			cs.set(NotInTownKey, "&cYou are not in a town.");
		if (!cs.isString(NoRankPermKey))
			cs.set(NoRankPermKey, "&cYour rank in the town does not allow change town open or close status");
		if (!cs.isString(FeedBackOpenKey))
			cs.set(FeedBackOpenKey, "&6Kingdom joining: &7OPEN");
		if (!cs.isString(FeedBackCloseKey))
			cs.set(FeedBackCloseKey, "&6Kingdom joining: &7INVITE ONLY");
		if (!cs.isString(FeedBackKey))
			cs.set(FeedBackKey, "&aDo &7/f setOpen <true/false> &ato have people join [without/only with] an invite.");
		if (!cs.isString(InvalidUsageKey))
			cs.set(InvalidUsageKey, "&cCommand usage: /f setOpen [true/false]");
		if (!cs.isString(SuccessFeedBackKey))
			cs.set(SuccessFeedBackKey, "&aTown Status is now: &7{0}");
	}
	@Override
	protected void setArgumentsDefault() {
		ConfigurationSection cs = dm.getConfig();
		if(!cs.isString(ArgKey))
			cs.set(ArgKey, getCommandName());
		if(!cs.isString("setOpenFalse"))
			cs.set("setOpenFalse", "false");
		if(!cs.isString("setOpenTrue"))
			cs.set("setOpenTrue", "true");
	}
	
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("districts.commands.setopen")){
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
			Rank rank = kingdom.getRank(p.getUniqueId());
			if(args.length == 2){
				if (!rank.checkPerm(Rank.RankPerm.OPENTOWN)){
					p.sendMessage(this.dm.getTCM(NoRankPermKey));
					return true;
				}
				boolean open;
				if(isArgument("setOpenFalse", args[1])){
					open = false;
				}else if(isArgument("setOpenTrue", args[1])){
					open = true;
				}else{
					p.sendMessage(this.dm.getTCM(InvalidUsageKey));
					return true;
				}
				kingdom.setKingdomOpen(open);

				p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, open?"Open":"Invite Only"));
			}else{
				if(kingdom.isKingdomOpen()){
					p.sendMessage(this.dm.getTCM(FeedBackOpenKey));
				}else{
					p.sendMessage(this.dm.getTCM(FeedBackCloseKey));
				}
				p.sendMessage(this.dm.getTCM(FeedBackKey));
			}
			return true;
		}
		return false;
	}

	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.towns.setopen",
				HelpKey)};
	}

}
