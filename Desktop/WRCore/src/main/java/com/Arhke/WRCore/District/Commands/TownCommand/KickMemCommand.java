package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader.ConfigFile;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
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
 *
 */
public class KickMemCommand extends SubCommandsBase{
    public KickMemCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
    NotInKingdomKey = "notInKingdom", UserNotFoundKey = "userNotFound", NotYourOwnKingdomKey = "notYourOwnTown",
    NotYourSelfKey = "notYourSelf",
    HigherRankKey = "higherRank", FailedSaveKey = "failedSave", SuccessTownMemberKey = "successTownMember",
    SuccessFeedBackKey = "successFeedBack", WrongArgsKey = "wrongArgs";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if(!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t kick <user> &7- kick a user from your town");
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(UserNotFoundKey))
            cs.set(UserNotFoundKey, "&cInvalid user: {0}");
        if(!cs.isString(NotYourOwnKingdomKey))
            cs.set(NotYourOwnKingdomKey, "&cYou may not kick someone not in your town.");
        if(!cs.isString(NotYourSelfKey))
            cs.set(NotYourSelfKey, "&cYou may not kick yourself");
        if(!cs.isString(HigherRankKey))
            cs.set(HigherRankKey, "&cYou may not kick a member with higher rank than you");
        if(!cs.isString(FailedSaveKey))
            cs.set(FailedSaveKey, "&cSave failed, please contact the developer");
        if(!cs.isString(SuccessTownMemberKey))
            cs.set(SuccessTownMemberKey, "&6{0} has been kicked from the town by {1}");
        if(!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&aYou have successfully kicked {0} from the town");
        if(!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&cPlease input a player's name to kick.");
    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.kick")){
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
            if(args.length == 2){
                Kingdom kingdom = u.getKingdom();
                Rank rank = kingdom.getRank(u.getID());
                TUser kick = getPlugin().getTUserManager().tempGetTUser(args[1]);
                if(kick == null) {
                    p.sendMessage(this.dm.getTCM(UserNotFoundKey, capitalize(args[1])));
                    return true;
                }
                if (!kingdom.equals(kick.getKingdom())){
                    p.sendMessage(this.dm.getTCM(NotYourOwnKingdomKey));
                    return true;
                }
                if(u.equals(kick)){
                    p.sendMessage("Not Same Person");
                    return true;
                }
                if(kingdom.getRank(kick.getID()).getID()>=rank.getID()){
                    p.sendMessage(this.dm.getTCM(HigherRankKey));
                    return true;
                }
                kick.setKingdom(null);
                try {
                    kingdom.save();
                } catch (Exception e) {
                    p.sendMessage(this.dm.getTCM(FailedSaveKey));
                    ErrorManager.error(35, e);
                    return true;
                }
                kingdom.messageMembers(this.dm.getTCM(SuccessTownMemberKey, capitalize(args[1]), p.getName()), false);
                p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, capitalize(args[1])));
                getPlugin().getNameTag().unregisterPlayerNameTag(p);

            }else{
                p.sendMessage(this.dm.getTCM(WrongArgsKey));
            }
            return true;
        }
        return false;
    }



    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.kick",
                "commandHelp.48")};
    }

}
