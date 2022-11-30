package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.ChunkDataManager.ChunkData;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ChunkCommand extends SubCommandsBase {
    public ChunkCommand(Main instance, String commandName, DataManager dm){
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserDataKey", DisplayChunkInfoKey = "displayChunkInfo",
    DisplayChunkHealthKey = "displayChunkHealth", WrongArgsKey = "wrongArgs", NotANumberKey = "notANumber", WildernessNoHealthKey = "wildernessNoHealth",
    NotEnoughMoneyKey = "notEnoughMoney", SuccessAddHealthKey = "successAddHealth", DuringBattleKey = "duringBattle";
    public void setDefaults(){
        ConfigurationSection cs = this.dm.getConfig();
        if(!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t chunk &7- chunk info and add Chunk Health");
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(DisplayChunkInfoKey))
            cs.set(DisplayChunkInfoKey, "&4&l==<&6&lChunk Info&4&l>==\n" +
                    "&6Coordinate X:&7{0} &6Z:&7{1}\n" +
                    "&6Owned By: &7{2}");
        if(!cs.isString(DisplayChunkHealthKey))
            cs.set(DisplayChunkHealthKey, "&6Chunk HP: &7{0}");
        if(!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&6Command usage:\n" +
                    "&6- /town chunk - &7display chunk info\n" +
                    "&6- /town chunk <amount of hp to add> - &7amount of hp to add");
        if(!cs.isString(NotANumberKey))
            cs.set(NotANumberKey, "&cPlease enter a decimal number amount of HP to add to the chunk health.");
        if(!cs.isString(WildernessNoHealthKey))
            cs.set(WildernessNoHealthKey, "&cYou may not add health to a wild chunk.");
        if(!cs.isString(NotEnoughMoneyKey))
            cs.set(NotEnoughMoneyKey, "&cYou do not have enough money to add &7{0} &chealth to this chunk.\n" +
                    "&6Cost: &7{1}   &6Current Balance: &7{2}.\n" +
                    "&6You are currently able to afford: &7{3}");
        if(!cs.isString(SuccessAddHealthKey))
            cs.set(SuccessAddHealthKey, "&aYou have successfully paid &7{0} &aand added &7{1}&a health to the chunk.\n" +
                    "&6The new chunk health is {2}");
        if(!cs.isString(DuringBattleKey))
            cs.set(DuringBattleKey, "&cYou may not add health during a battle if your chunk hp is 0");


    }
    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])) {
            if (!p.hasPermission("districts.commands.chunk")) {
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }
            TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
            if (u == null){
                p.sendMessage(this.dm.getTCM(NoUserDataKey));
                return true;
            }
            if (args.length == 1) {
                ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
                Town cdTown  = cd.getTown();
                p.sendMessage(this.dm.getTCM(DisplayChunkInfoKey,
                        cd.getX(), cd.getZ(), cdTown == null?"None":cdTown.getName()));
                    p.sendMessage(this.dm.getTCM(DisplayChunkHealthKey, cd.getHP()));
            }
            else if (args.length == 2) {
                double addHP = 0;
                boolean all = false;
                if(args[1].equalsIgnoreCase("all")){
                    all = true;
                }else {
                    try {
                        addHP = roundHundredth(Double.parseDouble(args[1]));
                    } catch (NumberFormatException e) {
                        p.sendMessage(this.dm.getTCM(NotANumberKey));
                        return true;
                    }
                }
                ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
                if(cd.getTown()==null || !cd.getTown().getKingdom().equals(u.getKingdom())){
                    p.sendMessage(this.dm.getTCM(WildernessNoHealthKey, cd.getHP()));
                    return true;
                }
                if(cd.getHP() == 0 && getPlugin().getBattleManager().getDefendingBattle(cd.getTown()) != null){
                    p.sendMessage(this.dm.getTCM(DuringBattleKey));
                    return true;
                }
                double chunkCost = 5.0;
                if (all){
                    addHP = floorHundredth(getPlugin().getHook().getBalance(p)/chunkCost);
                }
                int cost = (int)Math.ceil(addHP*chunkCost);
                if (!getPlugin().getHook().withdrawMoney(p, cost).transactionSuccess()){
                    double canBuy = floorHundredth(getPlugin().getHook().getBalance(p)/chunkCost);
                    p.sendMessage(this.dm.getTCM(NotEnoughMoneyKey, addHP, cost,
                            getPlugin().getHook().getBalance(p), canBuy));
                    return true;
                }

                cd.addHP(addHP);

                p.sendMessage(this.dm.getTCM(SuccessAddHealthKey, cost, addHP, cd.getHP()));
            } else {
                p.sendMessage(this.dm.getTCM(WrongArgsKey));
            }
            return true;
        }
        return false;
    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }
    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.claim",
                HelpKey)};
    }


}
