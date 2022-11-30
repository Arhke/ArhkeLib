package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.api.events.ClaimLandEvent;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 *
 * @author William Lin
 * @date 2/14/2020
 */
public class TownCreateCommand extends SubCommandsBase {
    public TownCreateCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData", ClaimedLandKey = "claimedLand",
            NoRankPermKey = "noRankPerm", NotMinLengthKey = "notMinLength", NotMaxLengthKey = "notMaxLength",
            BannedNameKey = "bannedName", NoSymbolsKey = "noSymbols", TownAlreadyExistsKey = "townAlreadyExists",
            AlreadyInTownKey = "alreadyInTown", CantBuildKey = "notEnoughMoney", FailedCreateKey = "failedCreate",
            TownSaveFailedKey = "kingdomSaveFailed",	SuccessFeedBackKey = "successFeedBack", SuccessPaidKey = "successPaid",
            SuccessBroadCastKey = "successBroadCast", WrongArgsKey = "wrongArgs";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if(!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t create <Kingdom> &7- Create a new Town");
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(ClaimedLandKey))
            cs.set(ClaimedLandKey, "&cThat chunk of land is already claimed.");
        if(!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYour rank in your town does not allow you to create a kingdom");
        if(!cs.isString(NotMinLengthKey))
            cs.set(NotMinLengthKey, "&cA kingdom name must be at least &6{0}&c characters long.");
        if(!cs.isString(NotMaxLengthKey))
            cs.set(NotMaxLengthKey, "&cA kingdom name's length must be less than or equal to &6{0}&c characters.");
        if(!cs.isString(BannedNameKey))
            cs.set(BannedNameKey, "&cThe name &6{0}&c is banned on this server.");
        if(!cs.isString(NoSymbolsKey))
            cs.set(NoSymbolsKey, "&cA kingdom name must be characters only. No numbers or symbols.");
        if(!cs.isString(TownAlreadyExistsKey))
            cs.set(TownAlreadyExistsKey, "&cA Town with that name already exists.");
        if(!cs.isString(AlreadyInTownKey))
            cs.set(AlreadyInTownKey, "&cYou are already in a town!");
        if(!cs.isString(CantBuildKey))
            cs.set(CantBuildKey, "&cYou cannot create town here as you cannot build here.");
        if(!cs.isString(FailedCreateKey))
            cs.set(FailedCreateKey, "&cFailed to create town!");
        if(!cs.isString(TownSaveFailedKey))
            cs.set(TownSaveFailedKey, "&cCould not save data, Please Notify The Developer");
        if(!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&a&lTown {0} successfully created!");
        if(!cs.isString(SuccessPaidKey))
            cs.set(SuccessPaidKey, "&d${0} &aused to create a town.");
        if(!cs.isString(SuccessBroadCastKey))
            cs.set(SuccessBroadCastKey, "&7{0} &acreated a new town: &7{1}");
        if(!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&cYou must enter a town name.");


    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.create")){
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }
            TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
            if(u == null){
                p.sendMessage(this.dm.getTCM(NoUserDataKey));
                return true;
            }
            ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
            if(cd.getTown() != null){
                p.sendMessage(this.dm.getTCM(ClaimedLandKey));
                return true;
            }
            ConfigManager cm = getPlugin().getConfig(ConfigLoader.ConfigFile.DISTRICTCONFIG);

            if(args.length == 2){
                if(args[1].length() < cm.getInt("town.name.minLength")){
                    p.sendMessage(this.dm.getTCM(NotMinLengthKey, cm.getInt("town.name.minLength")));
                    return true;
                }
                if(args[1].length() > cm.getInt("town.name.maxLength")){
                    p.sendMessage(this.dm.getTCM(NotMaxLengthKey, cm.getInt("town.name.maxLength")));
                    return true;
                }
                String checkBase = args[1].toLowerCase();
                for(String str : cm.getStringList("town.name.banned")){
                    if (checkBase.indexOf(str.toLowerCase()) != -1){
                        p.sendMessage(this.dm.getTCM(BannedNameKey, args[1]));
                        return true;
                    }
                }
                if(!isAlpha(args[1]) && !cm.getBoolean("town.name.allowSymbols")){
                    p.sendMessage(this.dm.getTCM(NoSymbolsKey));
                    return true;
                }
                if(getPlugin().getTownsManager().getTown(args[1])!=null){
                    p.sendMessage(this.dm.getTCM(TownAlreadyExistsKey));
                    return true;
                }
                if(u.getKingdom() != null){
                    p.sendMessage(this.dm.getTCM(AlreadyInTownKey));
                    return true;
                }

                //Cost for kingdom
                double townCost = 0d/*cm.getDouble("town.cost")*/;
//                if(townCost > 0 && getPlugin().getHook().getBalance(p) < townCost) {
//                    p.sendMessage(this.dm.getTCM(CantBuildKey, String.format("%.2f", townCost)));
//                    return true;
//                }
                if(getPlugin().getHook().isLandProtected(p, cd)) {
                    p.sendMessage(this.dm.getTCM(CantBuildKey, String.format("%.2f", townCost)));
                    return true;
                }

                Town town;
                try {
                    town = getPlugin().getTownsManager().createTown(u, args[1], p.getLocation());
                    ClaimLandEvent event = new ClaimLandEvent(town, u, cd);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if(event.isCancel()){
                        return true;
                    }
                    cd.show(p);
                    cd.setTown(town);
                } catch (Exception e) {
                    e.printStackTrace();
                    p.sendMessage(this.dm.getTCM(FailedCreateKey));
                    return true;
                }
                try {
                    town.save();
                } catch (Exception e) {
                    p.sendMessage(this.dm.getTCM(TownSaveFailedKey));
                    return true;
                }
                p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, town.getName()));
                p.setCompassTarget(p.getLocation());
//                if(townCost > 0) {
//                    getPlugin().getHook().withdrawMoney(p, townCost);
//                    p.sendMessage(this.dm.getTCM(SuccessPaidKey, String.format("%.2f", townCost)));
//                }

//                Bukkit.getPluginManager().callEvent(new KingdomCreateEvent(k , u));

                Bukkit.broadcastMessage(this.dm.getTCM(SuccessBroadCastKey, p.getName(), town.getName()));
                getPlugin().getNameTag().addPlayersToAllies(p, Collections.singletonList(p.getName()));
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
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.towns.create",
                HelpKey)};
    }

}
