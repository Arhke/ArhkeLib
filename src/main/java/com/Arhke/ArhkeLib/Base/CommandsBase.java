package com.Arhke.ArhkeLib.Base;


import com.Arhke.ArhkeLib.Base.HelpCommand.HelpMessage;
import com.Arhke.ArhkeLib.Base.HelpCommand.HelpPerm;
import com.Arhke.ArhkeLib.FileIO.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class CommandsBase implements CommandExecutor, TabCompleter {
    protected final String commandName;
    protected String permission;
    protected String[] helpList = new String[0];
    protected final HelpMessage multiPageHelp;
    protected final List<SubCommandsBase> subCommands = new ArrayList<>();
    protected ConfigManager dm;
    public static final String HelpListKey = "singleLineHelp", HelpHeaderKey = "multiPageHelpHeader", PermissionKey = "permissionNode";
    /**
     */
    public CommandsBase(String command, ConfigManager dm) {
        this.commandName = command.toLowerCase();
        this.dm = dm;
        setDefaults();
        dm.isOrDefault(Collections.emptyList(), HelpListKey);
        dm.isOrDefault("", PermissionKey);
        dm.isOrDefault("&6====<Page &7{0} &6out of &7{1}&6>====", HelpHeaderKey);
        List<String> helpList = dm.getStringList(HelpListKey);
        if(helpList.size() != 0){
            this.helpList = Base.tcm(helpList.toArray(new String[0]));
        }
        this.multiPageHelp = new HelpMessage(dm.getString(HelpHeaderKey));
        this.permission = dm.getString(PermissionKey);
        dm.getFM().save();

    }
    public void registerSubCommand(SubCommandsBase scb){
        this.subCommands.add(scb);
        setHelpBuilder(scb.getHelpPerm());
        dm.getFM().save();
    }
    @Override
    @SuppressWarnings("NullableProblems")
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(permission.length() != 0 && !sender.hasPermission(permission)) return new ArrayList<>();
        List<String> ret = new ArrayList<>();
        if(args.length == 1){
            if(sender instanceof Player) {
                this.subCommands.stream()
                        .filter((sub)->sub.getType() == SubCommandsBase.CommandType.PLAYER)
                        .forEach((sub)->ret.add(sub.getCommandName()));
            }else{
                this.subCommands.stream()
                        .filter((sub)->sub.getType() == SubCommandsBase.CommandType.SERVER)
                        .forEach((sub)->ret.add(sub.getCommandName()));
            }
            ret.add("help");
            ret.removeIf((tab)->!tab.startsWith(args[0]));
        }else{
            if(args[0].equalsIgnoreCase("help") && args.length == 2){
                int max = multiPageHelp.getHelpPermList().size() / multiPageHelp.getLinePerPage();
                for(int i = 0; i <= max; i++){
                    ret.add(i+1+"");
                }
                return ret;
            }
            if(sender instanceof Player){
                Player p = (Player) sender;
                for(SubCommandsBase subCmd: this.subCommands){
                    if(subCmd.getType().equals(SubCommandsBase.CommandType.PLAYER)&&
                            subCmd.getCommandName().equalsIgnoreCase(args[0]) ){
                        return subCmd.tab(args, p);
                    }
                }
            }else{
                for(SubCommandsBase subCmd: this.subCommands){
                    if(subCmd.getType().equals(SubCommandsBase.CommandType.SERVER)&&
                            subCmd.getCommandName().equalsIgnoreCase(args[1])){
                        return subCmd.tab(args, null);
                    }
                }
            }


        }
        return ret;
    }
    public String getCmd() {
        return commandName;
    }
    public void setPermission(String perm){
        this.permission = perm;
    }
    public List<SubCommandsBase> getSubCommands(){
        return this.subCommands;
    }
    public ConfigManager getDM(){
        return dm;
    }
    public String[] getHelpString() {
        return helpList;
    }
    public void sendHelp(@Nonnull CommandSender sender, int page){

        if(page == 0){
            if(this.helpList.length == 0){
                page = 1;
            }else {
                sender.sendMessage(this.helpList);
                return;
            }
        }
        sender.sendMessage(this.multiPageHelp.getMessage(sender, page));
    }
    public abstract void setDefaults();
    private void setHelpBuilder(HelpPerm hp){
        multiPageHelp.addMessage(Base.tcm(hp.getMsg(), this.getCmd()));
    }
    protected boolean processSubCommands(CommandSender sender, String[] args) {
        if(permission.length() != 0 && !sender.hasPermission(permission)) return false;
        if(args.length == 0){
            sendHelp(sender, 0);
            return true;
        }
        if(sender instanceof Player){
            Player p = (Player) sender;
            for(SubCommandsBase subCmd: this.subCommands){
                 if(subCmd.getType().equals(SubCommandsBase.CommandType.PLAYER)&&
                         subCmd.getCommandName().equalsIgnoreCase(args[0]) && subCmd.run(args, p)){
                        return true;
                }
            }
        }else{
            for(SubCommandsBase subCmd: this.subCommands){
                if(subCmd.getType().equals(SubCommandsBase.CommandType.SERVER)&&
                        subCmd.getCommandName().equalsIgnoreCase(args[1]) && subCmd.run(args, null)){
                    return true;
                }
            }
        }
        if(args[0].equalsIgnoreCase("help")){

            int page = 0;
            if (args.length >= 2){
                try {
                     page = Integer.parseInt(args[1]);
                     if(page <= 0){
                         page = 1;
                     }
                }catch(NumberFormatException e){
                    page = 1;
                }
            }
            sendHelp(sender, page);
            return true;
        }
        return false;
    }

}