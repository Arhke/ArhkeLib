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
 * @date 2/14/2021
 *
 */
public class WithdrawCommand extends SubCommandsBase {
    public WithdrawCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
    NotInKingdomKey = "notInKingdom", NoRankPermKey = "noRankPerm", InvalidAmountKey = "invalidAmount",
    NotEnoughMoneyKey = "notEnoughMoney", TransactionFailedKey = "transactionFailed", FailedSaveKey = "failedSave",
    SuccessFeedBackKey = "successFeedBack", NotInHeartKey = "notInHeart", NotYourTownKey = "notYourTown";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/f withdraw <amount> &7- withdraw from your kingdom's treasury");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if (!cs.isString(NotInKingdomKey))
            cs.set(NotInKingdomKey, "&cYou are not in a town.");
        if (!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYour rank in your town does not allow you to withdraw from coffer");
        if (!cs.isString(InvalidAmountKey))
            cs.set(InvalidAmountKey, "&cYou must enter a valid number for the amount.");
        if (!cs.isString(NotEnoughMoneyKey))
            cs.set(NotEnoughMoneyKey, "&cThe Kingdom does not have enough money to withdraw: &7${0} &cBalance: &7${1}");
          if (!cs.isString(TransactionFailedKey))
            cs.set(TransactionFailedKey, "&cThe transaction failed, please make sure you have enough inventory space");
        if (!cs.isString(NotInHeartKey))
            cs.set(NotInHeartKey, "&cYou must be in your town's heart in order to withdraw from/deposit to the town bank");
        dm.isOrDefault("&cYou are not a member of this town.", NotYourTownKey);
        if (!cs.isString(FailedSaveKey))
            cs.set(FailedSaveKey, "&cFailed to save town or user data!");
        if (!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&7${0} &6added to the treasury. New treasury balance: &7${1}");

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
            if(u.getKingdom() == null){
                p.sendMessage(this.dm.getTCM(NotInKingdomKey));
                return true;
            }
            Kingdom kingdom = u.getKingdom();
            Rank rank = kingdom.getRank(u.getID());
            if(!rank.checkPerm(Rank.RankPerm.COFFERWITHDRAW)){
                p.sendMessage(this.dm.getTCM(NoRankPermKey ));
                return true;
            }
            ChunkData cd = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation());
            if (cd.getTown() == null){
                p.sendMessage(this.dm.getTCM(NotInHeartKey));
                return true;
            }
            Town town = cd.getTown();
            if(!town.getKingdom().equals(kingdom)){
                p.sendMessage(this.dm.getTCM(NotYourTownKey));
                return true;
            }
            if(!town.isOnHeart(p.getLocation())){
                p.sendMessage(this.dm.getTCM(NotInHeartKey));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
                if (amount < 0) {
                    p.sendMessage(this.dm.getTCM(InvalidAmountKey));
                    return true;
                }
            } catch (Exception e) {
                p.sendMessage(this.dm.getTCM(InvalidAmountKey));
                return true;
            }
            if(town.getBank() < amount) {
                p.sendMessage(this.dm.getTCM(NotEnoughMoneyKey, roundHundredth(amount), roundHundredth(town.getBank())));
                return true;
            }
            if(!getPlugin().getHook().depositMoney(p, amount).transactionSuccess()){
                p.sendMessage(TransactionFailedKey);
                return true;
            }

            town.bankDeposit(-1*amount);
            info("Treasury: " + u.getName() + " ADDED $" + amount + " to " + town.getName());
            try {
                town.save();
            } catch (Exception e) {
                p.sendMessage(this.dm.getTCM(FailedSaveKey));
                e.printStackTrace();
                return true;
            }
            p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, roundInt(amount), roundHundredth(town.getBank())));
            return true;
        }
        return false;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.treasury",
                HelpKey)};
    }

}
