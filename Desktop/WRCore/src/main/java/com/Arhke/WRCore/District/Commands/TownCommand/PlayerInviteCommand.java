package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader.ConfigFile;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Rank;
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
 * @date 2/28/2021
 *
 */
public class PlayerInviteCommand extends SubCommandsBase {

    public PlayerInviteCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
    NotInTownKey = "notInTown", NoRankPermKey = "noRankPerm", InvalidPlayerKey = "invalidPlayer",
    AlreadyInTownKey = "alreadyInTown", InviteReputationKey = "inviteReputation", InviteNotifKey = "inviteNotif",
    InviteOnlineKey = "inviteOnline", InviteOfflineKey = "inviteOffline", UninviteKey = "uninvite",
    WrongArgsKey = "wrongArgs";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if(!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t invite <player> &7- invite a player to join your Town");
        if(!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if(!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if(!cs.isString(NotInTownKey))
            cs.set(NotInTownKey, "&cYou are not in a Town.");
        if(!cs.isString(NoRankPermKey))
            cs.set(NoRankPermKey, "&cYour Town Rank does not have permissions to invite player.");
        if(!cs.isString(InvalidPlayerKey))
            cs.set(InvalidPlayerKey, "&cInvalid Player");
        if(!cs.isString(AlreadyInTownKey))
            cs.set(AlreadyInTownKey, "&c{0} is currently in a Town.");
        if(!cs.isString(InviteReputationKey))
            cs.set(InviteReputationKey, "&7{0} &ehas &7{1} &ereputation. Reputation starts at &7{2}.");
        if(!cs.isString(InviteNotifKey))
            cs.set(InviteNotifKey, "&a{0} invited you to join his Town: &e{1}");
        if(!cs.isString(InviteOnlineKey))
            cs.set(InviteOnlineKey, "&a{0} has been invited to your Town.");
        if(!cs.isString(InviteOfflineKey))
            cs.set(InviteOfflineKey, "&e{0} has been invited to your Town.  " +
                    "&cThey did not receive a notification because they are not online.");
        if(!cs.isString(UninviteKey))
            cs.set(UninviteKey, "&cThat user has already been invited to your Town. " +
                    "&6{0} has been removed from the invite list.");
        if(!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&cYou must enter a player's name.");

    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("districts.commands.invite",
                this.dm.getTCM(HelpKey))};
    }
    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.invite")){
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
            Rank rank = kingdom.getRank(u.getID());
            if (!rank.checkPerm(Rank.RankPerm.INVITE)) {
                p.sendMessage(this.dm.getTCM(NoRankPermKey));
                return true;
            }
            if(args.length == 2){
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                TUser invite = getPlugin().getTUserManager().tempGetTUser(offlinePlayer);
                if (invite == null){
                    p.sendMessage(this.dm.getTCM(InvalidPlayerKey));
                    return true;
                }
                if(invite.getKingdom() != null){
                    p.sendMessage(this.dm.getTCM(AlreadyInTownKey, offlinePlayer.getName()));
                    return true;
                }

                if (!kingdom.hasInvited(invite.getID())) {
                    kingdom.invite(invite.getID());
                    p.sendMessage(this.dm.getTCM(InviteReputationKey, offlinePlayer.getName(),
                            invite.getReputation(), getPlugin().getConfig(ConfigFile.DISTRICTCONFIG).getInt("reputation.start")));
                    if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                        offlinePlayer.getPlayer().sendMessage(this.dm.getTCM(InviteNotifKey,p.getName(), kingdom.getName()));
                        p.sendMessage(this.dm.getTCM(InviteOnlineKey, offlinePlayer.getName()));
                    } else {
                        p.sendMessage(this.dm.getTCM(InviteOfflineKey, offlinePlayer.getName()));
                    }
                } else {
                    kingdom.unInvite(invite.getID());
                    p.sendMessage(this.dm.getTCM(UninviteKey, offlinePlayer.getName()));
                }
            }else{
                p.sendMessage(this.dm.getTCM(WrongArgsKey));
            }
            return true;
        }
        return false;
    }





}
