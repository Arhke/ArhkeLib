package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author William Lin
 * @date 3/19/2021
 *
 */
public class TownDisbandCommand extends SubCommandsBase {

    private static final HashMap<UUID, Long> deleteTown = new HashMap<>();

    public TownDisbandCommand(Main instance, String commandName, DataManager dm){
        super(instance, commandName, dm);
    }
    public static final String NoPermKey = "noPerm", NoAdminPermKey = "noAdminPerm", TownNotFound = "kingdomNotFound",
    DeletedByAdminKey = "deletedByAdmin", AdminConfirmDelete = "adminConfirmDelete", NoUserDataKey = "userNotFound",
    NotInTownKey = "notInTown", NoRankPermKey = "noRankPerm", SuccessDeleteKey = "successDelete", RuinsBroadcastKey = "ruinsBroadcast",
    ConfirmDeleteKey = "confirmDelete", DisbandWildKey = "disbandWild", NotYourTownKey = "notYourTown";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(NoAdminPermKey))
            cs.set(NoAdminPermKey, "&cYou do not have the admin permission required to delete other towns.");
        if(!cs.isString(TownNotFound))
            cs.set(TownNotFound, "&cThere were no towns found by the name {0}.");
        if(!cs.isString(DeletedByAdminKey))
            cs.set(DeletedByAdminKey, "&6Town {0} has been sent to ruins by {1}.");
        if(!cs.isString(AdminConfirmDelete))
            cs.set(AdminConfirmDelete, "&6Please run the command again within the next 30 seconds to confirm delete.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(NotInTownKey))
            cs.set(NotInTownKey, "&cYou are not in a town.");
        dm.isOrDefault("&cYou cannot disband wild", DisbandWildKey);
        dm.isOrDefault("&cThis town is not yours to disband", NotYourTownKey);
        if(!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYour rank in the town does not allow to disband the town.");
        if(!cs.isString(SuccessDeleteKey))
            cs.set(SuccessDeleteKey, "&6You have deleted your town.");
        if(!cs.isString(RuinsBroadcastKey))
            cs.set(RuinsBroadcastKey, "&6Town {0} has fallen to ruins!");
        dm.isOrDefault("&6Please run the command again within the next 30 seconds to confirm delete.", ConfirmDeleteKey);


    }

    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.delete")){
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }
            deleteTown.entrySet().removeIf((entry)->System.currentTimeMillis() - entry.getValue() > 30000);
            if(args.length == 2){
                if(!p.hasPermission("districts.commands.admin.disband")){
                    p.sendMessage(this.dm.getTCM(NoAdminPermKey));
                    return true;
                }
                Town town = getPlugin().getTownsManager().getTown(args[1]);
                if(town == null) {
                    p.sendMessage(this.dm.getTCM(TownNotFound, args[1]));
                    return true;
                }
                Long deleteConfirm = deleteTown.get(p.getUniqueId());
                if (deleteConfirm != null && System.currentTimeMillis() - deleteConfirm < 30000) {

                    p.sendMessage(this.dm.getTCM(DeletedByAdminKey, town.getName(), p.getName()));
                    getPlugin().getTownsManager().removeTown(town);
                } else {
                    deleteTown.put(p.getUniqueId(), System.currentTimeMillis());
                    p.sendMessage(this.dm.getTCM(AdminConfirmDelete));
                }
            } else {
                TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
                if(u == null){
                    p.sendMessage(this.dm.getTCM(NoUserDataKey));
                    return true;
                }
                if(u.getKingdom() == null){
                    p.sendMessage(this.dm.getTCM(NotInTownKey));
                    return true;
                }
                Town town = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation()).getTown();
                if (town == null){
                    p.sendMessage(this.dm.getTCM(DisbandWildKey));
                    return true;
                }
                if(!town.getKingdom().equals(u.getKingdom())){
                    p.sendMessage(this.dm.getTCM(NotYourTownKey));
                    return true;
                }
                Rank rank = town.getKingdom().getRank(p.getUniqueId());
                if(rank != Rank.LEADER){
                    p.sendMessage(this.dm.getTCM(NoRankPermKey));
                    return true;
                }
                if(deleteTown.containsKey(p.getUniqueId()) && (System.currentTimeMillis() - deleteTown.get(p.getUniqueId()) < 30000)){
                    deleteTown.remove(p.getUniqueId());
                    p.sendMessage(this.dm.getTCM(SuccessDeleteKey, town.getName(), p.getName()));
                    Bukkit.broadcastMessage(this.dm.getTCM(RuinsBroadcastKey, town.getName()));
                    getPlugin().getTownsManager().removeTown(town);

                }else{
                    deleteTown.put(p.getUniqueId(), System.currentTimeMillis());
                    p.sendMessage(this.dm.getTCM(ConfirmDeleteKey));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.delete",
                "commandHelp.42")};
    }

}