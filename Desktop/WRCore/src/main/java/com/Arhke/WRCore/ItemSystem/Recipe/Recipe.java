package com.Arhke.WRCore.ItemSystem.Recipe;


import com.Arhke.WRCore.ItemSystem.CraftItem.CustomItem;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.FurnitureType;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import com.Arhke.WRCore.Main;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Recipe extends MainBase {
    private String id;
    private FurnitureType furnitureType;
    private double chance;
    private long time;
    private long timeRange;
    public Set<ItemStack>   input = new HashSet<>();
    public List<ItemStack> output = new ArrayList<>();
    private List<Double> outputChance = new ArrayList<>();
    private int _totalChance = 0;
    public static final String FurnitureKey = "FurnitureType", ChanceKey = "Chance", TimeKey = "Time",
    TimeRangeKey = "TimeRange", InputKey = "Input", OutputKey = "Output", ItemKey = "Item", AmountKey = "Amount";
    public Recipe(Main instance, FileManager fm) throws Exception{
        super(instance);
        DataManager dm = fm.getDataManager();
        id = fm.getFileNameNoExt().toLowerCase();
        try {
            furnitureType = FurnitureType.valueOf(dm.getString(FurnitureKey).toUpperCase());
        }catch(IllegalArgumentException e){
            furnitureType = FurnitureType.SMELTERY;
            dm.set(furnitureType.name(), FurnitureKey);
        }
        chance = dm.getDouble(ChanceKey);
        time = (long)(dm.getDouble(TimeKey)*1000);
        timeRange = (long)(dm.getDouble(TimeRangeKey)*1000);
        DataManager inputdm = dm.getDataManager(InputKey);
        for (String key: inputdm.getConfig().getKeys(false)) {
            if(inputdm.getConfig().isString(key+"."+ItemKey)){
                CustomItem ci = getPlugin().getCustomItemManager().getItem(inputdm.getString(key, ItemKey).toLowerCase());
                if (ci == null) continue;
                ItemStack is = ci.getNewItem();
                is.setAmount(inputdm.getInt(1, key, AmountKey));
                input.add(is);
            }else{
                ItemStack is = inputdm.getConfig().getItemStack(key);
                if (is == null) continue;
                input.add(is);
            }
        }
        DataManager outputdm = dm.getDataManager(OutputKey);
        for (String key: outputdm.getConfig().getKeys(false)) {
            DataManager outputEntryDM = outputdm.getDataManager(key);
            if(outputEntryDM.getConfig().isString(ItemKey)){
                CustomItem ci = getPlugin().getCustomItemManager().getItem(outputEntryDM.getString(ItemKey));
                if (ci == null) continue;
                ItemStack is = ci.getNewItem();
                is.setAmount(outputEntryDM.getInt(1, AmountKey));
                output.add(is);
            }else{
                ItemStack is = outputEntryDM.getConfig().getItemStack(ItemKey);
                if (is == null) continue;
                output.add(is);
            }
            outputChance.add(outputdm.getDouble(1d, key, ChanceKey));

        }
        if (output.size() == 0){
            except("Output Entries are empty");
            return;
        }
        for (Double doub: outputChance){
            _totalChance += doub;
        }

    }


    /**
     * 0 = no Match
     * 1 = match, no amount
     * 2 = material amount match
     */
    public int matchesMaterial(Set<ItemStack> input) {
        if (input.size() != this.input.size()){
            return 0;
        }
        boolean amount = true;
        first:
        for (ItemStack item : this.input) {
            for (ItemStack is : input) {
                if (is.isSimilar(item)) {
                    if (is.getAmount() != item.getAmount()) {
                        amount = false;
                    }
                    continue first;
                }
            }
            return 0;
        }
        if (amount)
            return 2;
        else
            return 1;
    }

    public ItemStack getOutputItem() {
        double value = randNum(_totalChance);
        int i;
        for (i = 0; i < outputChance.size(); i++) {
            if (outputChance.get(i) > value) {
                break;
            } else {
                value -= outputChance.get(i);
            }
        }
        return new ItemStack(output.get(i));

    }

    public boolean isFormed() {
        return randNum(100) < chance;
    }

    public String getId() {
        return id;
    }

    /**
     * Check if the time at which the item was taken out matches the time for the recipe
     *
     * @param StartTime
     * @return
     */
    public  boolean matchesTime(long StartTime) {
        long check = System.currentTimeMillis() - StartTime;
        return check > time - timeRange && check < time + timeRange;
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof Recipe && ((Recipe) o).getId().equals(this.getId());
    }
}
