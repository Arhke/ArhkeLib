package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
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
 * @date 2/23/2021 #birthdayboy :)
 *
 */
public class    TownLeaderCommand extends SubCommandsBase {
    public TownLeaderCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
            NotInTownKey = "notInTown", NoRankPermKey = "noRankPerm", PlayerNotFoundKey = "playerNotFound",
            NoHigherRankKey = "noHigherRank", SuccessFeedBackKey = "successFeedBack",
            SuccessMemberKey = "successMember", WrongArgsKey = "wrongArgs";

    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/town promote <player> &7- promote a player's rank in your town");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if (!cs.isString(NotInTownKey))
            cs.set(NotInTownKey, "&cYou are not in a town.");
        if (!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYour rank in the town does not allow you to promote a player in your town");
        if (!cs.isString(PlayerNotFoundKey))
            cs.set(PlayerNotFoundKey, "&cNo player with name {0} was found in your town");
        if (!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&7{0} &6was promoted &7Leader");
        if (!cs.isString(SuccessMemberKey))
            cs.set(SuccessMemberKey, "&7{0}&6 was promoted to the top, &7{0}&6 is your new leader");
        if (!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "Incorrect Number of Args. Usage: /town leader <Player>");

    }

    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.leader")){
                p.sendMessage(HelpKey);
                return true;
            }
            if(args.length == 2){
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
                if(!rank.equals(Rank.LEADER) && !p.hasPermission("districts.commands.admin.leader")){
                    p.sendMessage(this.dm.getTCM(NoRankPermKey));
                    return true;
                }
                TUser promote = getPlugin().getTUserManager().tempGetTUser(args[1]);
                if (promote == null || !kingdom.equals(promote.getKingdom())){
                    p.sendMessage(this.dm.getTCM(PlayerNotFoundKey, args[1]));
                    return true;
                }
                kingdom.setLeader(promote);
                p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, args[1]));
                kingdom.messageMembers(this.dm.getTCM(SuccessMemberKey, args[1]), false);
            }else{
                p.sendMessage(this.dm.getTCM(WrongArgsKey));
            }
            return true;
        }
        return false;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.town.leader",
                HelpKey)};
    }

}
