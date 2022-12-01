package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author William Lin
 * @date 5/1/2021
 *
 */
public class KingdomChatCommand extends SubCommandsBase{
    private List<Town> townsOrdered = new ArrayList<>();
    private long lastUpdate = 0;
    public KingdomChatCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoKingdomKey = "noKingdom",
    SuccessKey = "success";
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t chat-&7Toggle town Chat.");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoKingdomKey))
            cs.set(NoKingdomKey, "&cYou are not in a kingdom.");

        if (!cs.isString(SuccessKey))
            cs.set(SuccessKey, "&6Chat set to: &7{0}");

    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.chat")){
                p.sendMessage(NoPermKey);
                return true;
            }
            TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
            if(u.getKingdom() == null){
                p.sendMessage(this.dm.getTCM(NoKingdomKey));
                return true;
            }
            byte b = u.getChat();
            String chat;
            if(b != 2){
                b = 2;
                chat = "TOWN";
            }else{
                b=0;
                chat = "NORMAL";
            }
            u.setChat(b);
            p.sendMessage(this.dm.getTCM(SuccessKey, chat));
            return true;
        }
        return false;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.chat",
                HelpKey)};
    }
}


