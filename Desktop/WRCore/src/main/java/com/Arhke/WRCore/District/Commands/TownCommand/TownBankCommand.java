package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author William Lin
 * @date 3/20/2021
 *
 */
public class TownBankCommand extends SubCommandsBase {

    public TownBankCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
    NotInTownKey = "notInTown", NoRankPermKey = "noRankPerm", SuccessFeedBackKey = "successFeedBack",
    NotOnTownKey = "notOnTown", NotYourTownKey = "notYourTown";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/f treasury &7- view, your town's treasury");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if (!cs.isString(NotInTownKey))
            cs.set(NotInTownKey, "&cYou are not in a town.");
        if(!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYour rank in the town does not allow you to view coffer balance");
        if(!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&6town treasury: &7${0}");
        if(!cs.isString(NotYourTownKey))
            cs.set(NotYourTownKey, "&cYou do not own this town, you may not view their bank balance");
        if (!cs.isString(NotOnTownKey))
            cs.set(NotOnTownKey, "&cYou are not on a town chunk, please query your bank info on a specific town or use /t info <town>");
    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.treasury")){
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }

            TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
            if(u == null){
                p.sendMessage(this.dm.getTCM(NoUserDataKey));
                return true;
            }
            Kingdom kingdom = u.getKingdom();
            if (kingdom == null){
                p.sendMessage(this.dm.getTCM(NotInTownKey));
                return true;
            }
            Rank rank = kingdom.getRank(u.getID());
            if(!rank.checkPerm(Rank.RankPerm.VIEWBANK)){
                p.sendMessage(this.dm.getTCM(NoRankPermKey));
                return true;
            }
            ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
            if (cd.getTown() == null){
                p.sendMessage(this.dm.getTCM(NotOnTownKey));
                return true;
            }
            Town town = cd.getTown();
            if(!town.getKingdom().equals(kingdom)) {
                p.sendMessage(this.dm.getTCM(NotYourTownKey));
                return true;
            }
            p.sendMessage(this.dm.getTCM(SuccessFeedBackKey,  town.getBank()));
            return true;
        }
        return false;
    }


    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.town.treasury",
                HelpKey)};
    }

}
