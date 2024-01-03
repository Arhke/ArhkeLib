package com.Arhke.ArhkeLib.Lib.Base;

import at.pavlov.cannons.listener.Commands;
import com.Arhke.ArhkeLib.Lib.CustomEvents.TrueDamageEvent;
import com.Arhke.ArhkeLib.Lib.FileIO.ConfigManager;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public abstract class Base {
    static boolean isPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null;
    static Random rand = new Random();
    public static String tcm(String msg, Object... objList){
        String ret = msg;
        for (int i = 0; i < objList.length; i++){
            ret = ret.replace("{"+i+"}", objList[i].toString());
        }
        ret = ret.replace('&', ChatColor.COLOR_CHAR)
                .replace("\\s", " ")
                .replace("\\n", "\n")
                .replace(ChatColor.COLOR_CHAR+""+ChatColor.COLOR_CHAR,"&");


        return ret;
    }
    public static String tcm(OfflinePlayer p, String msg, Object... objList){
        String ret = msg;
        for (int i = 0; i < objList.length; i++){
            ret = ret.replace("{"+i+"}", objList[i].toString());
        }
        ret = ret.replace('&', ChatColor.COLOR_CHAR)
                .replace("\\s", " ")
                .replace("\\n", "\n")
                .replace(ChatColor.COLOR_CHAR+""+ChatColor.COLOR_CHAR,"&");
        if(isPlaceholderAPI) return ret;
        return PlaceholderAPI.setPlaceholders(p, ret);
    }
    public static String[] tcm(String[] msg, Object... objList){
        for(int j = 0; j< msg.length; j++) {
            msg[j] = tcm(msg[j], objList);
        }
        return msg;
    }
    public static String placeAppender(int place){
        if (place %10 == 1){
            return place +"st";
        }else if(place%10 == 2){
            return place + "nd";
        }else if(place %10 == 3){
            return place + "rd";
        }else{
            return place + "th";
        }
    }
    public static String clickcableButton(){
//        BaseComponent[] component =
//                new ComponentBuilder("Hello ").color(net.md_5.bungee.api.ChatColor.RED)
//                        .append("world").color(net.md_5.bungee.api.ChatColor.DARK_RED).bold(true)
//                        .append("!").color(net.md_5.bungee.api.ChatColor.RED).create();
//        TextComponent message = new TextComponent("Click me");
//        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org"));
//        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Visit the Spigot website!").create()));
    return "";
    }



    //reflection
    public static Object getPrivateField(String fieldName, Class<?> clazz, Object object){
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        }catch(NoSuchFieldException | IllegalAccessException e){
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
     * @param num integer number marking the max number
     * @return random Integer from 0 to this number non inclusive
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
    public static <T> T randElement(Collection<T> collection){
        if (collection== null || collection.size() == 0){return null;}
        return collection.stream().skip(rand.nextInt(collection.size())).findFirst().orElse(null);
    }
    public static void addItemToPlayer(Player player, ItemStack is) {
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
    public static String serializeString(ItemStack is){
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("itemstack", is);
        return yml.saveToString();
    }
    public static ItemStack unserializeItem(String string){
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.loadFromString(string);
        } catch (InvalidConfigurationException e) {
            return null;
        }
        return yml.getItemStack("itemstack");
    }
    public static boolean itemIsSimilar(ItemStack is1, ItemStack is2){
        ItemStack is = new ItemStack(is1);
        is.setAmount(is2.getAmount());
        return is2.toString().equals(is.toString());
    }

    public static void trueDamage(LivingEntity attacker, LivingEntity target, double damage){
        if(damage <= 0) return;
        if(target.isDead()) return;
        damage = Math.min(target.getHealth(), damage);
        TrueDamageEvent event;
        Bukkit.getPluginManager().callEvent(event = new TrueDamageEvent(attacker, target, damage));
        if(event.isCancelled()) return;
        target.setHealth(Math.max(0,target.getHealth()-event.getDamage()));
        target.setLastDamageCause(new EntityDamageEvent(attacker, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));

    }
    public static boolean isTargeteableLivingEntity(Entity entity) {
        return entity instanceof Player && !entity.hasMetadata("NPC") && !isInProtectedRegion((Player)entity) && !entity.isDead();
    }

    public static boolean isTargeteableEntity(Entity entity) {
        return isTargeteableLivingEntity(entity) || entity instanceof Item;
    }

    public static boolean isInProtectedRegion(Player player) {
        return !WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).allows(DefaultFlag.PVP);
    }

    public static boolean isInProtectedRegion(Location loc) {
        return !WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc).allows(DefaultFlag.PVP);
    }
    public static void setDisplayName(ItemStack is, String name) {
        ItemMeta im = is.getItemMeta();
        Objects.requireNonNull(im).setDisplayName(tcm(name));
        is.setItemMeta(im);
    }
    public static void highlightItem(ItemStack is){

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
    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }
    public String convertTimeWithTimeZome(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC")/*.getRawOffset()*/);
        cal.setTimeInMillis(time);
        return (cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.MONTH) + 1) + " "
                + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE));

    }
    //message section
    public static void bc(String msg) {Bukkit.broadcastMessage(msg);}
    public static void info(String msg) {
        Bukkit.getLogger().info(msg);
    }
    public static void warn(String msg) {
        Bukkit.getLogger().warning(msg);
    }
    public static void err(String msg){
        Bukkit.getLogger().severe(msg);
    }
    public static void except(String Msg) throws RuntimeException {
        throw new RuntimeException(Msg);
    }
    public void exceptDisable(JavaPlugin plugin, String Msg){
        Bukkit.getPluginManager().disablePlugin(plugin);
        throw new RuntimeException(Msg);
    }
    public static void sendActionBar(Player player, String msg){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(msg).create());
    }
    protected boolean isAlpha(String name) {
        char[] chars = name.toLowerCase().toCharArray();
        for (char c : chars) {
            if(c < 97 || c > 122) {
                return false;
            }
        }

        return true;
    }

    protected String getLastOnlineTime(boolean online, long lastOnline) {
        if(online){
            return "ONLINE NOW";
        }
        long rem = System.currentTimeMillis() - lastOnline;
        long years = rem / 31557600000L;
        long weeks = (rem - (years * 31557600000L)) / 604800000L;
        long days = (rem-(years * 31557600000L)-(weeks * 604800000L)) / 86400000;
        long hours = ((rem - (days * 86400000) - (years * 31557600000L)-(weeks * 604800000L)) / 3600000);
        long minutes = (rem - (days * 86400000) - (hours * 3600000)-(years * 31557600000L)-(weeks * 604800000L)) / 60000;
        long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)-(years * 31557600000L)-(weeks * 604800000L)) / 1000;
        if(years == 0){
            if(weeks == 0){
                return days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
            }else{
                return weeks + (weeks == 1 ? " week" : " weeks") + ", " + days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
            }
        }else{
            return years + (years == 1 ? " year" : " years") + ", " + weeks + (weeks == 1 ? " week" : " weeks") + ", " + days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
        }
    }
//    protected class LevelUtil{
//        int _level;
//        double _points;
//        double _extrapoints;
//        public LevelUtil(double initial, double multiplier, double increment, double points){
//            _points = points;
//            double levelpass = initial;
//            int level = 1;
//            long timestamp = System.currentTimeMillis();
//            while(points >= levelpass){
//                level++;
//                points-=levelpass;
//                levelpass*=multiplier;
//                levelpass+=increment;
//                if(System.currentTimeMillis()-timestamp > TimeOut || levelpass < 1){
//                    _level = 1;
//                    _extrapoints = 0;
//                    return;
//                }
//            }
//            _level = level;
//            _extrapoints = points;
//        }
//        public int getLevel() {
//            return _level;
//        }
//        public double getExtraPoints() {
//            return _extrapoints;
//        }
//        public double getPoints() {
//            return _points;
//        }
//    }
public static SortedSet<Map.Entry<Player, Integer>> sort(final Map<Player, Integer> map) {
    final SortedSet<Map.Entry<Player, Integer>> treeMap = new TreeSet<>(Map.Entry.comparingByValue());
    treeMap.addAll(map.entrySet());
    return treeMap;
}
}