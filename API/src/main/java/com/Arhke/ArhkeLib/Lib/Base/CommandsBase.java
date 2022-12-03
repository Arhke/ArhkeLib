package com.Arhke.ArhkeLib.Lib.Base;


import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpMessage;
import com.Arhke.ArhkeLib.Lib.Base.HelpCommand.HelpPerm;
import com.Arhke.ArhkeLib.Lib.FileIO.ConfigManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class CommandsBase<T extends JavaPlugin> extends MainBase<T> implements CommandExecutor {
    protected final String commandName;
    protected String[] helpString = new String[0];
    protected final HelpMessage helpMessage;
    protected final List<SubCommandsBase<T>> subCommands = new ArrayList<>();
    protected ConfigManager dm;
    public static final String HelpStringKey = "helpList", HelpHeaderKey = "helpHeaderKey";
    /**
     * Input the General Config DataManager for TownCommands
     */
    @SafeVarargs
    public CommandsBase(T instance, String command, ConfigManager dm, SubCommandsBase<T>... subCommands) {
        super(instance);
        this.commandName = command.toLowerCase();
        this.dm = dm;
        dm.isOrDefault(Collections.emptyList(), HelpStringKey);
        dm.isOrDefault("&6====<Page &7{0} &6out of &7{1}&6>====", HelpHeaderKey);
        List<String> helpList = dm.getStringList(HelpStringKey);
        if(helpList.size() != 0){
            helpString = helpList.toArray(new String[0]);
            tcm(helpString);
        }
        helpMessage = new HelpMessage(dm.getString(HelpHeaderKey));
        this.subCommands.addAll(Arrays.asList(subCommands));
        setDefaults();
        for(SubCommandsBase<T> subcommand: this.subCommands){
            subcommand.addHelpMessage(this::setHelpBuilder);
        }
    }

    public String getCmd() {
        return commandName;
    }
    public List<SubCommandsBase<T>> getSubCommands(){
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
            sender.sendMessage(this.helpString);
            return;
        }
        sender.sendMessage(this.helpMessage.getMessage(sender, page));
    }
    public abstract void setDefaults();
    private void setHelpBuilder(HelpPerm hp){
        hp.setMsg(tcm(hp.getMsg(), this.getCmd()));
    }
    protected boolean processSubCommands(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendHelp(sender, 0);
            return true;
        }
        if(sender instanceof Player){
            Player p = (Player) sender;
            for(SubCommandsBase<T> subCmd: this.subCommands){
                 if(subCmd.getType().equals(SubCommandsBase.CommandType.PLAYER)&&
                         subCmd.getCommandName().equalsIgnoreCase(args[0]) && subCmd.run(args, p)){
                        return true;
                }
            }
        }else{
            for(SubCommandsBase<T> subCmd: this.subCommands){
                if(subCmd.getType().equals(SubCommandsBase.CommandType.SERVER)&&
                        subCmd.getCommandName().equalsIgnoreCase(args[1]) && subCmd.run(args, null)){
                    return true;
                }
            }
        }
        if(args[0].equalsIgnoreCase("help")){
            int page;
            if (args.length >= 2 && (page = Integer.parseInt(args[1])) > 0){
                sendHelp(sender, page);
            }else{
                sendHelp(sender, 0);
            }
            return true;
        }
        return false;
    }

}