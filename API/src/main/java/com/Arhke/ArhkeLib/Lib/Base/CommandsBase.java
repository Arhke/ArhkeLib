package com.Arhke.ArhkeLib.Lib.Base;


import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpMessage;
import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpPerm;
import com.Arhke.ArhkeLib.Lib.FileIO.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class CommandsBase implements CommandExecutor, TabCompleter {
    protected final String commandName;
    protected String permission = "";
    protected String[] helpString = new String[0];
    protected final HelpMessage helpMessage;
    protected final List<SubCommandsBase> subCommands = new ArrayList<>();
    protected ConfigManager dm;
    public static final String HelpListKey = "helpList", HelpHeaderKey = "helpHeader", PermissionKey = "permissionNode";
    /**
     */
    public CommandsBase(JavaPlugin plugin, String command, ConfigManager dm) {
        this.commandName = command.toLowerCase();
        this.dm = dm;
        dm.isOrDefault(Collections.emptyList(), HelpListKey);
        dm.isOrDefault("&6====<Page &7{0} &6out of &7{1}&6>====", HelpHeaderKey);
        List<String> helpList = dm.getStringList(HelpListKey);
        if(helpList.size() != 0){
            helpString = Base.tcm(helpList.toArray(new String[0]));
        }
        this.helpMessage = new HelpMessage(dm.getString(HelpHeaderKey));
        this.permission = dm.getDefString("", PermissionKey);

        setDefaults();

    }
    public void registerSubCommand(SubCommandsBase scb){
        this.subCommands.add(scb);
        setHelpBuilder(scb.getHelpPerm());
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
                int max = helpMessage.getHelpPermList().size() / helpMessage.getLinePerPage();
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
        return helpString;
    }
    public void sendHelp(@Nonnull CommandSender sender, int page){

        if(page == 0){
            if(this.helpString.length == 0){
                page = 1;
            }else {
                sender.sendMessage(this.helpString);
                return;
            }
        }
        sender.sendMessage(this.helpMessage.getMessage(sender, page));
    }
    public abstract void setDefaults();
    private void setHelpBuilder(HelpPerm hp){
        helpMessage.addMessage(Base.tcm(hp.getMsg(), this.getCmd()));
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