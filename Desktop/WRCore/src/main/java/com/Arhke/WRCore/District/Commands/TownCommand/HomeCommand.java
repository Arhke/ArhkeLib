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
import com.earth2me.essentials.Trade;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author William Lin
 * @date 7/27/2021
 *
 */
public class HomeCommand extends SubCommandsBase {
    public HomeCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData", NotInTownKey = "NotInKingdom",
            SpawnInKingdomOrTownKey = "spawnInKingdomOrTown", SetSpawnFeedBackKey = "setSpawnFeedback", NoRankPermKey = "noRankPerm",
    WrongArgKey = "wrongArgKey", TownNotFoundKey = "townNotFound", NotYourTownKey = "notYourTown";



    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();

        if(!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t home <town> &7- tp to your town heart.");
        if(!cs.isString(WrongArgKey))
            cs.set(WrongArgKey, "&7Please note that to tp to other towns you own: /t home <town>");
        if(!cs.isString(TownNotFoundKey))
            cs.set(TownNotFoundKey, "&cNo town by the name of &7{0} was found.");
        if(!cs.isString(NotYourTownKey))
            cs.set(NotYourTownKey, "&cYou may not warp to a town that you do not own.");

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
            if(!p.hasPermission("districts.commands.home")){
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
            Town town;
            if (args.length != 2){
                town = u.getKingdom().getTowns().get(0);
                p.sendMessage(this.dm.getTCM(WrongArgKey));
            }else {
                town = getPlugin().getTownsManager().getTown(args[1]);
                if (town == null) {
                    p.sendMessage(this.dm.getTCM(TownNotFoundKey, args[1]));
                    return true;
                }
                Kingdom kingdom = u.getKingdom();
                if (!kingdom.equals(town.getKingdom())) {
                    p.sendMessage(this.dm.getTCM(NotYourTownKey));
                    return true;
                }
            }
            try {
                getPlugin().getHook().getEssentials().getUser(p).getTeleport().teleport(town.getHeartLocation(),null, PlayerTeleportEvent.TeleportCause.COMMAND);;
            } catch (Exception e) {
                e.printStackTrace();
            }
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
