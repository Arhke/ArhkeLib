package com.Arhke.WRCore.Lib.Base;

import com.Arhke.WRCore.Main;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import de.tr7zw.itemnbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.Map;


public abstract class Base {
    public static long TimeOut = 5000L;
    //string
    public static String translateColorCodes(String Msg) {
        return Msg.replace('&', ChatColor.COLOR_CHAR).replace("\\n", "\n").replace(ChatColor.COLOR_CHAR+""+ChatColor.COLOR_CHAR,"&");
    }
    public static String tcm(String msg, Object... objList){
        String ret = translateColorCodes(msg);
        for (int i = 0; i < objList.length; i++){
            ret = ret.replace("{"+i+"}", objList[i].toString());
        }
        return ret;
    }
    public static String[] tcm(String[] msg, Object... objList){
        for(int j = 0; j< msg.length; j++) {
            msg[j] = tcm(msg[j], objList);
        }
        return msg;
    }

    //reflection
    public static Object getPrivateField(String fieldName, Class clazz, Object object){
        Field field;
        Object o = null;

        try
        {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return o;
    }
    //math
    public static boolean isInBetween(double a, double b, double between){
        return (a >= between && b <= between) || (a <= between && b >= between);
    }
    public static int roundInt(double num){
        return (int)Math.floor(num+0.5);
    }
    /**
     * Round double to hundredth place
     *
     * @param price
     * @return
     */
    public static double roundHundredth(double price) {
        return roundInt(price * 100) / 100d;
    }
    public static double floorHundredth(double price){
        return ((int)(price*100))/100d;
    }
    public static String capitalize(String input){
        if (input == null || input.length() < 1) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    /**
     * Random Integer from 0 to this number non inclusive
     * @param num
     * @return
     */
    public static int randInt(int num){
        return (int)(Math.random()*num);
    }
    /**
     * Random Double Number from 0 to this number non inclusive
     */
    public static double randNum(double num){
        return Math.random()*num;
    }
//    public static void updateNameTag(Player player) {
//        Scoreboard scoreboard = player.getScoreboard();
//        if (scoreboard.getTeam("RED") == null) {
//            scoreboard.registerNewTeam("RED");
//        }
//        if (scoreboard.getTeam("BLUE") == null) {
//            scoreboard.registerNewTeam("BLUE");
//        }
//        Team redTeam = scoreboard.getTeam("RED");
//        Team blueTeam = scoreboard.getTeam("BLUE");
//        redTeam.setPrefix(tcm("&c"));
//        redTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
//        redTeam.setCanSeeFriendlyInvisibles(true);
//        blueTeam.setPrefix(tcm("&9"));
//        blueTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
//        blueTeam.setCanSeeFriendlyInvisibles(true);
//        if (!Main.getInstance().team.isInTeam(player)) {
//            player.sendMessage("Not part of a team");
//            return;
//        }
//        if (Main.getInstance().team.isInBlueTeam(player)) {
//            blueTeam.addPlayer(player);
//            player.sendMessage("Blue name tag");
//        }
//        if (Main.getInstance().team.isInRedTeam(player)) {
//            redTeam.addPlayer(player);
//            player.sendMessage("Red name tag");
//        }
//        player.setScoreboard(scoreboard); // Just testing shit
//    }
    //ItemStacks Shit
    public static void addItemtoPlayer(Player player, ItemStack is) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(is);
        } else {
            player.sendMessage(ChatColor.RED + "Your Inventory was Full, Dropping Item On Ground!");
            player.getLocation().getWorld().dropItem(player.getLocation(), is);
        }
    }
    public static void itemAmountMinus(ItemStack is){
        is.setAmount(is.getAmount()-1);
    }
    public static String itemToString(ItemStack is){
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("itemstack", is);

        return yml.saveToString();
    }
    public static ItemStack stringToItem(String string){
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.loadFromString(string);
        } catch (InvalidConfigurationException e) {
            return null;
        }
        return yml.getItemStack("itemstack");
    }

    public static ItemStack setDisplayName(ItemStack is, String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(translateColorCodes(name));
        is.setItemMeta(im);
        return is;
    }

    //target ability
    public static boolean isTargeteableEntity(Entity entity){
        return isTargeteableLivingEntity(entity) || entity instanceof Item;
    }
    public static boolean isTargeteableLivingEntity(Entity entity){
        return entity instanceof Player && !entity.hasMetadata("NPC") && !isInProtectedRegion((Player)entity);
    }
    @SuppressWarnings("deprecation")
    public static boolean isInProtectedRegion(Player player){
        return !WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).allows(DefaultFlag.PVP);
    }

    //config
    public static String pathOf(String... Path) {
        if (Path.length == 0)
            return "";
        else if (Path.length == 1)
            return Path[0];
        else {
            String ret = Path[0];
            for (int i = 1; i < Path.length; i++) {
                ret += "." + Path[i];
            }
            return ret;
        }
    }
    public static String pathOf(String[] ParentPath, String... Path){
        String parentpath = pathOf(ParentPath);
        String path = pathOf(Path);
        if (parentpath.length() == 0)
            return path;
        else
            return path.length() == 0? parentpath:parentpath + "." + path;
    }

    //time
    public static String longToTimeString(long rem){
        if (rem < 0){
            rem = 0;
        }
        long days = rem / 86400000;
        long hours = ((rem - (days * 86400000)) / 3600000);
        long minutes = (rem - (days * 86400000) - (hours * 3600000)) / 60000;
        long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)) / 1000;
        String time = "";
        if(days == 0){
            time = hours + ":" + minutes + ":" + seconds;
        }else{
            time = days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds;
        }
        return time;
    }

    //message section
    public static void info(String Msg) {
        Bukkit.getLogger().info(Msg);
    }
    public static void warn(String Msg) {
        Bukkit.getLogger().warning(Msg);
    }
    public static void error(String Msg){
        Bukkit.getLogger().severe(Msg);
    }
    public static void except(String Msg) throws RuntimeException {
        throw new RuntimeException(Msg);
    }
    public static void sendActionBar(Player player, String msg){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(tcm(msg)).create());
    }
    protected class LevelUtil{
        int _level;
        double _points;
        double _extrapoints;
        public LevelUtil(double initial, double multiplier, double increment, double points){
            _points = points;
            double levelpass = initial;
            int level = 1;
            long timestamp = System.currentTimeMillis();
            while(points >= levelpass){
                level++;
                points-=levelpass;
                levelpass*=multiplier;
                levelpass+=increment;
                if(System.currentTimeMillis()-timestamp > TimeOut || levelpass < 1){
                    _level = 1;
                    _extrapoints = 0;
                    return;
                }
            }
            _level = level;
            _extrapoints = points;
        }
        public int getLevel() {
            return _level;
        }
        public double getExtraPoints() {
            return _extrapoints;
        }
        public double getPoints() {
            return _points;
        }
    }
}