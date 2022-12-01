package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.ConfigLoader.ConfigFile;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author William Lin
 * @date 2/18/2021
 *
 */
public class PlayerJoinCommand extends SubCommandsBase {


    public PlayerJoinCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoUserDataKey = "noUserData",
            AlreadyInTownKey = "alreadyInTown", InvalidTownKey = "invalidTown", NotInvitedKey = "notInvited",
            MaxMembersKey = "maxTowns", SuccessMessageTownMemberKey = "successMessageTownMember",
            FailedKey = "failed", SuccessFeedBackKey = "successFeedBack", WrongArgsKey = "wrongArgs";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t join <town> &7- You have joined a town");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoUserDataKey))
            cs.set(NoUserDataKey, "&cFailed to load your user data.");
        if (!cs.isString(AlreadyInTownKey))
            cs.set(AlreadyInTownKey, "&cYou are already in a town.");
        if (!cs.isString(InvalidTownKey))
            cs.set(InvalidTownKey, "&cInvalid Town");
        if (!cs.isString(NotInvitedKey))
            cs.set(NotInvitedKey, "&cYou must be invited to that town to join.");
        if (!cs.isString(MaxMembersKey))
            cs.set(MaxMembersKey, "&cThis town has reached its maximum amount of members: {0} players");
        if (!cs.isString(SuccessMessageTownMemberKey))
            cs.set(SuccessMessageTownMemberKey, "&a{0} has joined your town.");
        if (!cs.isString(FailedKey))
            cs.set(FailedKey, "&cFailed to save town join! Please Contact Admins");
        if (!cs.isString(SuccessFeedBackKey))
            cs.set(SuccessFeedBackKey, "&aYou have successfully joined town {0}");
        if (!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&aPlease enter a town name");




    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.join")){
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }
            if(args.length == 2){
                TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
                if (u == null) {
                    p.sendMessage(this.dm.getTCM(NoUserDataKey));
                    return true;
                }
                if(u.getKingdom() != null){
                    p.sendMessage(this.dm.getTCM(AlreadyInTownKey));
                    return true;
                }
                Town town = getPlugin().getTownsManager().getTown(args[1]);
                if(town == null){
                    p.sendMessage(this.dm.getTCM(InvalidTownKey));
                    return true;
                }
                Kingdom kingdom = town.getKingdom();
                if (!kingdom.canJoin(p.getUniqueId()) && !p.hasPermission("feudal.commands.admin.town.join")){
                    p.sendMessage(this.dm.getTCM(NotInvitedKey));
                    return true;
                }
                if (kingdom.getMembers().size() >= getPlugin().getConfig(ConfigFile.DISTRICTCONFIG).getInt("town.maxMembers")) {
                    p.sendMessage(this.dm.getTCM(MaxMembersKey, kingdom.getMembers().size()));
                    return true;
                }
//                JoinKingdomEvent event = new JoinKingdomEvent(kingdom, u);
//                Bukkit.getPluginManager().callEvent(event);
//                if (event.isCancelled()) {
//                    return true;
//                }
                kingdom.messageMembers(this.dm.getTCM(SuccessMessageTownMemberKey, p.getName()), false);
                u.setKingdom(kingdom);
                try {
                    kingdom.save();
                } catch (Exception e) {
                    p.sendMessage(this.dm.getTCM(FailedKey));
                    return true;
                }

                p.sendMessage(this.dm.getTCM(SuccessFeedBackKey, kingdom.getName()));
                getPlugin().getNameTag().registerPlayerNameTag(p);
            }else{
                p.sendMessage(this.dm.getTCM(WrongArgsKey));
            }
            return true;
        }
        return false;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.join",
                HelpKey)};
    }

}
