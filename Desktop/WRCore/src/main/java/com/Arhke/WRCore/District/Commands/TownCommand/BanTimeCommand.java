package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author William Lin
 * @date 4/14/2021
 *
 */
public class BanTimeCommand extends SubCommandsBase {
    public BanTimeCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String NoPermKey = "noPerm", WrongArgsKey = "wrongArgs", NoUserDataKey = "noUserData",
    NoKingdomKey = "noKingdom",	NoRankKey = "noRank", NoRankPermKey = "noRankPerm", HelpKey="help", NotIntegerKey = "notInteger";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey,  "&6&l> {0}/t banTime <Time> &7- open up a gui relative to your current time a kingdom as an ally");
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&cPlease specify your current time in hours.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(NoKingdomKey))
            cs.set(NoKingdomKey, "&cYou are not in a town.");
        if (!cs.isString(NoRankKey))
            cs.set(NoRankKey, "&cError getting rank for your town.");
        if (!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYou don't have permissions in your town to set war ban time.");
        if (!cs.isString(NotIntegerKey))
            cs.set(NotIntegerKey, "&cPlease enter an integer between 1 and 24.");



    }

    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }
    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.bantime")){
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
                if(!rank.checkPerm(Rank.RankPerm.BANTIME)) {
                    p.sendMessage(this.dm.getTCM(NoRankPermKey));
                    return true;
                }
                int curTime;
                try{
                    curTime = Integer.parseInt(args[1]);
                }catch(NumberFormatException e){
                    p.sendMessage(this.dm.getTCM(NotIntegerKey));
                    return true;
                }
                if(curTime < 1 || curTime > 24){
                    p.sendMessage(this.dm.getTCM(NotIntegerKey));
                    return true;
                }
                int hourUTC = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY);
                getPlugin().getGUIManager().add(p, new BanTimeGUI(getPlugin(), p, curTime-hourUTC, u.getKingdom().getName()));
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
