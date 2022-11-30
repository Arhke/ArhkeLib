package com.Arhke.WRCore.Lib.Base;


import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.Direction;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandsBase extends MainBase implements CommandExecutor {
    private String commandName;
    protected String[] helpString;
    private List<SubCommandsBase> subCommands = new ArrayList<>();
    protected DataManager dm;
    public static final String HelpKey = "help";
    /**
     * Input the General Config DataManager for TownCommands
     */
    public CommandsBase(Main instance, String command, DataManager dm, SubCommandsBase... subCommands) {
        super(instance);
        this.commandName = command.toLowerCase();
        List<String> helpList = dm.getStringList(HelpKey);
        if(helpList.size() != 0){
            helpString = new String[helpList.size()];
            for(int i = 0; i < helpList.size(); i++){
                helpString[i] = helpList.get(i);
            }
            helpString = tcm(helpString);
        }else{
            helpString = new String[]{ChatColor.RED + "[Error] Please refer to the Plugin Documentation for Help for /" + getCmd()      };
        }
        for (SubCommandsBase subCommand:subCommands){
            this.subCommands.add(subCommand);
        }
        this.dm = dm;
        setDefaults();
    }


    public String getCmd() {
        return commandName;
    }
    public List<SubCommandsBase> getSubCommands(){
        return this.subCommands;
    }
    public String[] getHelp() {
        return helpString;
    }
    public void setHelp(String... help){
        this.helpString = help;
    }
    public abstract void setDefaults();
    /**
     * Sets the help for msg with a little bit of help
     * @param commandHelp
     * @return returns the help that was set
     */
    protected String[] setHelpBuilder(String[] commandHelp){
        String[] ret = new String[commandHelp.length+1];
        ret[0] = ChatColor.GOLD + "[" + getPlugin().getName() + " Command Help] " + "/" + getCmd() + " Usage:";
        for(int i = 0; i < commandHelp.length; i++){
            ret[i+1] = ChatColor.AQUA + commandHelp[i].replace("-", ChatColor.DARK_AQUA + "-");
        }
        setHelp(ret);
        return ret;
    }

    public static boolean isPlayer(CommandSender Sender){
        return Sender instanceof Player;
    }



    // ==<Helper Methods>==

    /**
     * returns null if arguments to not meet the specified types
     * @param Sender
     * @param Arguments
     * @param Types
     * @param Usage
     * @return
     */
    public MultiArray verifyArgs(CommandSender Sender, String[] Arguments, Class[] Types, String Usage){
        if(Arguments.length < 1){
            Sender.sendMessage(getHelp());
            return null;
        }

        String[] argsList = Arrays.copyOfRange(Arguments, 1, Arguments.length);
        if(argsList.length == Types.length){
            MultiArray ma = new MultiArray();
            for(int i = 0; i < Types.length; i++){
                String arg = argsList[i];
                if (Types[i].equals(int.class) || Types[i].equals(Integer.class)){
                    try {
                        ma.add(Integer.parseInt(arg));
                    } catch (NumberFormatException exception) {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is not a Integer, Please Type an Integer.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(double.class) || Types[i].equals(Double.class)){
                    try {
                        ma.add(Double.parseDouble(arg));
                    } catch (NumberFormatException exception) {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is not a Double, Please Type an Double.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(boolean.class) || Types[i].equals(Boolean.class)){
                    if (arg.equalsIgnoreCase("true")){
                        ma.add(Boolean.TRUE);
                    }else if(arg.equalsIgnoreCase("false")){
                        ma.add(Boolean.FALSE);
                    }else {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is not Valid, Please Type True or False.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(OfflinePlayer.class)){
                    ma.add(Bukkit.getOfflinePlayer(argsList[i]));
                }
                else if(Types[i].equals(Player.class)){
                    Player player;
                    if ((player = Bukkit.getPlayerExact(argsList[i])) != null){
                        ma.add(player);
                    }else {
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is Not Valid Online Player, Please Try Again.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else if(Types[i].equals(Direction.class)){
                    try{
                        ma.add(Direction.valueOf(argsList[i].toUpperCase()));
                    }catch(IllegalArgumentException e){
                        Sender.sendMessage(ChatColor.RED + "[Error] \"" + arg + "\" is Not Valid Direction, Please Type N, E, S, or W.");
                        Sender.sendMessage(ChatColor.RED + "Usage: " + Usage);
                        return null;
                    }
                }
                else{
                    ma.add(arg);
                }
            }
            return ma;
        }else {
            Sender.sendMessage(ChatColor.RED + "[Error] Incorrect Number Of Arguments. Usage: " + Usage);
            return null;
        }
    }
    protected class MultiArray {
        ArrayList<Object> _list = new ArrayList<>();
        public void add(Object Object){
            _list.add(Object);
        }
        public Integer getInt(int Index){
            return (Integer)_list.get(Index);
        }
        public Double getDouble(int Index){
            return (Double)_list.get(Index);
        }
        public Boolean getBoolean(int Index){
            return (Boolean)_list.get(Index);
        }
        public String getString(int Index){
            return (String)_list.get(Index);
        }
        public OfflinePlayer getOfflinePlayer(int Index){
            return (OfflinePlayer)_list.get(Index);
        }
        public Player getPlayer(int Index){
            return (Player)_list.get(Index);
        }
        public Direction getDirection(int Index){
            return (Direction)_list.get(Index);
        }

    }

}