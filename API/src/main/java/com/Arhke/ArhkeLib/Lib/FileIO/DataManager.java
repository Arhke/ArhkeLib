package com.Arhke.ArhkeLib.Lib.FileIO;

import com.Arhke.ArhkeLib.Lib.Base.Base;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
@SuppressWarnings("unused")
public class DataManager extends Base {
    ConfigurationSection config;

    public DataManager(ConfigurationSection cs){
        config = cs;
    }

    public ConfigurationSection getConfig(){
        return config;
    }
    public void isOrDefault(int def, String... path){
        String pathString = pathOf(path);
        if (config.isDouble(pathString)){
            config.set(pathString, (int)config.getDouble(pathString));
            return;
        }
        if(config.isLong(pathString)){
            config.set(pathString, (int)config.getLong(pathString));
            return;
        }
        if(!config.isInt(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(double def, String... path){
        String pathString = pathOf(path);
        if(config.isInt(pathString)) {
            config.set(pathString, (double)config.getInt(pathString));
        }
        if (config.isLong(pathString)){
            config.set(pathString, (double)config.getLong(pathString));
            return;
        }
        if(!config.isDouble(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(long def, String... path){
        String pathString = pathOf(path);
        if(config.isInt(pathString)) {
            config.set(pathString, (long)config.getInt(pathString));
        }
        if (config.isDouble(pathString)){
            config.set(pathString, (long)config.getDouble(pathString));
            return;
        }
        if(!config.isLong(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(String def, String... path){
        String pathString = pathOf(path);
        if(!config.isString(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(List<String> def, String... path){
        String pathString = pathOf(path);
        if(!config.isList(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(boolean def, String... path){
        String pathString = pathOf(path);
        if(!config.isBoolean(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(Vector def, String... path){
        String pathString = pathOf(path);
        if(!config.isVector(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(Color def, String... path){
        String pathString = pathOf(path);
        if(!config.isColor(pathString))
            config.set(pathString, def);
    }
    public void isOrDefault(ItemStack def, String... path){
        String pathString = pathOf(path);
        if(!config.isItemStack(pathString))
            config.set(pathString, def);
    }
    /**
     * Gets the boolean specified at the path.
     * Defaults false if path not found.
     */
    public boolean getBoolean(String... path) {
        String pathString = pathOf(path);
        if (config.isBoolean(pathString))
            return config.getBoolean(pathString);

        return false;
    }
    /**
     * Gets the int at the path.
     * Defaults to 0 if not found
     */
    public int getInt(String... path){
        String pathString = pathOf(path);
        if(config.isInt(pathString))
            return config.getInt(pathString);
        else
            return 0;
    }
    /**
     * Gets the int at the path.
     * Defaults to defInt if not found
     */
    public int getInt(int defInt, String... path){
        String pathString = pathOf(path);
        if(config.isInt(pathString))
            return config.getInt(pathString);
        else
            return defInt;
    }
    public long getLong(String... path){
        return getLong(0, path);
    }

    public long getLong(long defLong, String... path) {
        String pathString = pathOf(path);
        if (config.isLong(pathString))
            return config.getLong(pathString);
        if (config.isInt(pathString))
            return config.getInt(pathString);
        if(config.isDouble(pathString))
            return (long)config.getDouble(pathString);
        else
            return defLong;
    }
    public double getDouble(String... path){
        return getDouble(0, path);
    }
    public double getDouble(double defDouble,String... path){
        String pathString = pathOf(path);
        if(config.isDouble(pathString))
            return config.getDouble(pathString);
        if(config.isInt(pathString)){
            return config.getInt(pathString);
        }
        return defDouble;
    }
    public String getString (String... path){
        String pathString = pathOf(path);
        if(config.isString(pathString))
            return config.getString(pathString);
        return "\u00a7cINFORMATION MISSING FOR \"" + pathString + "\"";

    }
    public String getDefString(String defString, String... path){
        String pathString = pathOf(path);
        if(config.isString(pathString))
            return config.getString(pathString);
        return defString;
    }
    /**
     * Gets the string at the specified "path" and translates the color code and {i} placeholders
     */
    public String getTCM(String path, Object... objList){
        return tcm(getString(path), objList);
    }
    public List<String> getStringList(String... path){
        String pathString = pathOf(path);
        if(config.isList(pathString)){
            return config.getStringList(pathString);
        }else{
            return new ArrayList<>();
        }
    }
    public List<String> getStringList(List<String> defList, String... path){
        String pathString = pathOf(path);
        if(config.isList(pathString)){
            return config.getStringList(pathString);
        }else
            return defList;

    }
    public <T extends YamlSerializable> T getObj(Supplier<T> supp, String... path)  {
        T obj = supp.get();
        obj.load(getDataManager(path));
        return obj;
    }
    public <T extends YamlSerializable> void loadCollection(Supplier<T> supp, Collection<T> collection)  {
        for(String key: getConfig().getKeys(false)) {
            collection.add(getObj(supp,key));
        }
    }
    public <T> void saveCollection(Collection<T> collection)  {
        int i = 0;
        for(T entry: collection) {
            set(entry, i+"");
            i++;
        }
    }
    public void forEach(Consumer<String> entry){
        for(String key: getConfig().getKeys(false)) {
            entry.accept(key);
        }
    }
    public void set(Object value, String... path) {
        String pathString = pathOf(path);
        if(value instanceof YamlSerializable){
            ((YamlSerializable) value).write(getDataManager(path));
            return;
        }
        config.set(pathString, value);
    }
    /**
     * returns Null if UUID doesnt exist
     */
    @SuppressWarnings("ConstantConditions")
    public UUID getUUID(String... path){
        String pathString = pathOf(path);
        if(config.isString(pathString)) {
            try {
                return UUID.fromString(config.getString(pathString));
            } catch (IllegalArgumentException exception) {
                warn("Unable to load " + config.getString(pathString) + " into a UUID");
                return null;
            }
        } else {
            return null;
        }
    }
    /**
     * note this method doesnt fill in a default location
     */
    @SuppressWarnings("ConstantConditions")
    public Location getLocation(String... path) {
        ConfigurationSection cs = getConfigurationSection(path);
        if (!cs.isString(WorldKey)) {
            set(null, path);
            return null;
        }
        World world;
        if ((world = Bukkit.getWorld(cs.getString(WorldKey))) == null) {
            set(null, path);
            return null;
        }
        if (!(cs.isDouble(XKey) && cs.isDouble(YKey) && cs.isDouble(ZKey))){
            set(null, path);
            return null;
        }
        return new Location(world, cs.getDouble(XKey), cs.getDouble(YKey), cs.getDouble(ZKey));
    }
    @SuppressWarnings("ConstantConditions")
    public void setLocation(Location location, String... path){
        ConfigurationSection cs = getConfigurationSection(path);
        cs.set(WorldKey, location.getWorld().getName());
        cs.set(XKey, location.getX());
        cs.set(YKey, location.getY());
        cs.set(ZKey, location.getZ());
    }
    public Color getColor(String... path){
        ConfigurationSection cs = getConfigurationSection(path);
        if (!cs.isInt(RedKey)){
            cs.set(RedKey, 0d);
        }
        if (!cs.isInt(GreenKey)){
            cs.set(GreenKey, 0d);
        }
        if (!cs.isInt(BlueKey)){
            cs.set(BlueKey, 0d);
        }
        return Color.fromBGR(cs.getInt(BlueKey), cs.getInt(GreenKey), cs.getInt((RedKey)));
    }
    public void setColor(Color color, String... path){
        ConfigurationSection cs = getConfigurationSection(path);
        cs.set(BlueKey, color.getBlue());
        cs.set(GreenKey, color.getGreen());
        cs.set(RedKey, color.getRed());
    }
    public Material getMaterial(Material defMat, String... path){
        String pathString = pathOf(path);
        if (!config.isString(pathString)){
            return defMat;
        }
        try{
            return Material.valueOf(config.getString(pathString));
        }catch(IllegalArgumentException e){
            warn("Illegal Configuration of \"" + pathString + "\" defaulting to " + defMat);
            return defMat;
        }
    }
    public List<Material> getMaterialList(String... path){
        List<String> materials = getStringList(path);
        List<Material> ret = new ArrayList<>();
        materials.forEach(s -> {
            try{
                ret.add(Material.valueOf(s));
            }catch(IllegalArgumentException ignored){

            }
        });
        return ret;
    }


    public List<ItemStack> getItemStackList(){
        List<ItemStack> ret = new ArrayList<>();
        for (String key: getConfig().getKeys(false)){
            ret.add(getConfig().getItemStack(key));
        }
        return ret;
    }
    public void setConfigItemStack(ItemStack itemStack, String... path){

    }

    private ConfigurationSection getConfigurationSection(String... path) {
        String pathString = pathOf(path);
        return config.isConfigurationSection(pathString)?config.getConfigurationSection(pathString):
                config.createSection(pathString);
    }

    public void wipeAllKeys(){
        for (String key: config.getKeys(false)){
            config.set(key, null);
        }
    }
    public void delete(FileManager cm) {
        cm.getDataManager().set(null, config.getCurrentPath());
    }
    public DataManager getDataManager(String... path){
        if(path.length == 0)
            return this;
        return new DataManager(getConfigurationSection(path));
    }
    //Location Keys
    public static final String WorldKey = "worldLoc", XKey = "xLoc", YKey = "yLoc", ZKey = "zLoc";
    //Color Keys
    public static final String RedKey = "Red", GreenKey = "Green", BlueKey = "Blue";


}



