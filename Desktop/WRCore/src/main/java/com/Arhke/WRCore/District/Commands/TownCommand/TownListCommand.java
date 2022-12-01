package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author William Lin
 * @date 2/18/2021
 *
 */
public class TownListCommand extends SubCommandsBase {
    private List<Town> townsOrdered = new ArrayList<>();
    private long lastUpdate = 0;
    public TownListCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoTownKey = "noTown", NotAnIntegerKey = "notAnInteger", LoadingKey = "loading", MinPageKey = "minPage",
    MaxPageKey = "maxPageKey", SuccessTitleKey = "successTitle", SuccessEntryKey = "successEntry", WrongArgsKey = "wrongArgs";
    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t list <page> &7- List Towns");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoTownKey))
            cs.set(NoTownKey, "&cThere are no towns available for display.");
        if (!cs.isString(NotAnIntegerKey))
            cs.set(NotAnIntegerKey, "&c{0} is not a valid number. Please input a positive integer.");
        if (!cs.isString(LoadingKey))
            cs.set(LoadingKey, "&6Currently reordering the list. Please wait...");
        if (!cs.isString(MinPageKey))
            cs.set(MinPageKey, "&cThe page number may not be smaller than 1.");
        if (!cs.isString(MaxPageKey))
            cs.set(MaxPageKey, "&cThe page number may not be bigger than the maximum number of pages: {0}");
        if (!cs.isString(WrongArgsKey))
            cs.set(WrongArgsKey, "&aWrong Number of Arguments /t list <page>");
        if (!cs.isString(SuccessTitleKey))
            cs.set(SuccessTitleKey, "&9&l===[&c&lTown List {0}/{1}&9&l]===");
        if (!cs.isString(SuccessEntryKey))
            cs.set(SuccessEntryKey, "&6{0}: &7{1}/{2} online, Land:{3}, ${4}");

    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }

    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.list")){
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }
            if (getPlugin().getTownsManager().getTowns().size() == 0) {
                p.sendMessage(this.dm.getTCM(NoTownKey));
                return true;
            }
            int page;
            if(args.length == 2){
                try{
                    page = Integer.parseInt(args[1]);
                }catch(Exception e){
                    p.sendMessage(this.dm.getTCM(NotAnIntegerKey, args[1]));
                    return true;
                }
            }else if (args.length == 1){
                page = 1;
            }else{
                p.sendMessage(this.dm.getTCM(WrongArgsKey));
                return true;
            }
            if (townsOrdered.size() != getPlugin().getTownsManager().getTowns().size() || System.currentTimeMillis() - lastUpdate > 300000) {//5 min
                p.sendMessage(this.dm.getTCM(LoadingKey));
                new Thread(() -> {
                    final List<Town> towns = orderTowns();
                    try {
                        new BukkitRunnable() {
                            public void run() {
                                lastUpdate = System.currentTimeMillis();
                                townsOrdered = towns;
                                continueList(p, page);
                            }
                        }.runTaskLater(getPlugin(), 0L);
                    } catch (IllegalPluginAccessException e) {
                        //Nothing
                    }
                }).start();
                return true;
            }
            continueList(p, page);
            return true;
        }
        return false;
    }

    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.join",
                HelpKey)};
    }
    private void continueList(Player p, int page) {
        int maxPage = getMaxTownPage();
        if(page < 1){
            p.sendMessage(this.dm.getTCM(MinPageKey));
            return;
        }
        if(page > maxPage){
            p.sendMessage(this.dm.getTCM(MaxPageKey, maxPage));
            return;
        }
        page--;
        List<Town> towns = getTowns(page*8, (page*8)+8);
        p.sendMessage(this.dm.getTCM(SuccessTitleKey, page+1, maxPage));
        for(Town town: towns){
            p.sendMessage(this.dm.getTCM(SuccessEntryKey, town.getName(), town.getKingdom().getOnline(), town.getKingdom().getMembers().size(), town.getLandAmount(), town.getBank()));
        }
    }

    /**
     * Get array list of towns based on what page they will be shown on /f
     * list.
     *
     * @param i being start index,
     * @param j being end index.
     * @return a list of the towns got from the indexes
     */
    private List<Town> getTowns(int i, int j) {
        if (townsOrdered.size() <= j) {
            j = townsOrdered.size();
        }
        return townsOrdered.subList(i, j);
    }

    private List<Town> orderTowns() {
        List<Town> towns = new ArrayList<>(getPlugin().getTownsManager().getTowns());
        Collections.sort(towns);
        return towns;
    }
    /**
     * Get how many pages there will be for /f list.
     *
     * @return Number of pages for /f list
     */
    public int getMaxTownPage() {
        double divide = ((double) getPlugin().getTownsManager().getTowns().size()) / 8;
        if (((int) divide) == divide) {
            return ((int) divide);
        } else {
            return ((int) divide) + 1;
        }
    }
}
