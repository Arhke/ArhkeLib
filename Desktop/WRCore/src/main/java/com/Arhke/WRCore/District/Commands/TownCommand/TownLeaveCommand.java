package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.District.util.ErrorManager;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author William Lin
 * @date 3/17/2021
 *
 */
public class TownLeaveCommand extends SubCommandsBase {


    public TownLeaveCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",  NotInTownKey = "notInTown",
    NoRankPermKey = "noRankPerm", LastMemberKey = "lastMember", LeaderKey = "leader", SuccessFeedBackKey = "successFeedBack",
    SuccessTownKey = "successTown";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if(!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t leave &7- leave your town");
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(NotInTownKey))
            cs.set(NotInTownKey, "&cYou are not in a town.");
        if(!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "Your town rank does not allow you to have your town leave the kingdom");
        if(!cs.isString(LastMemberKey))
            cs.set(LastMemberKey, "&cYou are the only member in your town. Delete your town to leave.");
        if(!cs.isString(LeaderKey))
            cs.set(LeaderKey, "&cYou are the leader of your town. Give leadership to another member to leave or /town disband");
        if(!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&aYour have left your town");
        if(!cs.isString(SuccessTownKey))
            cs.set(SuccessTownKey, "&6{0} has left your town");


    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.leave")){
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
            if(kingdom.getMembers().size() == 1){
                p.sendMessage(this.dm.getTCM(LastMemberKey));
                return true;
            }
            if(kingdom.getRank(p.getUniqueId()) == Rank.LEADER){
                p.sendMessage(this.dm.getTCM(LeaderKey));
                return true;
            }
            u.setKingdom(null);
            try {
                kingdom.save();
            } catch (Exception e) {
                p.sendMessage("&cFailed to save kingdom.");
                ErrorManager.error(35, e);
                return true;
            }
            p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, kingdom.getName()));
            kingdom.messageMembers(this.dm.getTCM(SuccessTownKey, p.getName()), false);
            getPlugin().getNameTag().unregisterPlayerNameTag(p);
            return true;
        }
        return false;
    }



    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.town.leave",
                this.dm.getTCM(HelpKey))};
    }

}
