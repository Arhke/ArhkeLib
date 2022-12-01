package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.Lib.GUI.InventoryGui;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BanTimeGUI extends InventoryGui {
    static Inventory staticInventory = Bukkit.createInventory(null, 54, "&cBan Time GUI for {0}");
    int offset;

    /**
     * Creates now inventory gui.
     * @param instance main instance
     * @param player player opening it
     * @param offset the time offset
     * @param args  placeholder objects
     */
    public BanTimeGUI(Main instance, Player player, int offset, Object... args) {
        super(instance, staticInventory, args);
        this.offset = offset;
        setItems(player);
    }

    @Override
    public void setItems(Player p) {
        final TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
        if (u.getKingdom() == null){
            return;
        }
        List<String> greenLore = new ArrayList<>();
        greenLore.add(ChatColor.GREEN + "Available for raiding");
        List<String> redLore = new ArrayList<>();
        redLore.add(ChatColor.RED + "Banned from being raided");
        ItemStack green = new ItemStack(Material.WOOL, 1, (short)5);
        ItemMeta im = green.getItemMeta();
        im.setLore(greenLore);
        green.setItemMeta(im);
        ItemStack red = new ItemStack(Material.WOOL, 1, (short)14);
        im = red.getItemMeta();
        im.setLore(redLore);
        red.setItemMeta(im);

        int relativeTime = 0;
        for (int i = 10; i < 54; i++){
            if (i%9 < 1 || i%9>7){
                continue;
            }
            if (relativeTime >= 24){
                break;
            }
            relativeTime++;
            final int banTimeIndex = relativeTime-offset-1;
            Bukkit.broadcastMessage(banTimeIndex+"");
            final String timeDisplay = tcm("&6{0}&8{1} time slot", relativeTime%12 == 0?12:relativeTime%12, relativeTime > 12?"PM":"AM");
            ItemStack slotItem;
            if (u.getKingdom().getBanTime(banTimeIndex)){
                slotItem = new ItemStack(red);
            }else{
                slotItem = new ItemStack(green);
            }
            setItem(i, setDisplayName(slotItem, timeDisplay), (arg)->{

                InventoryClickEvent ice = (InventoryClickEvent)arg;
                ice.setCancelled(true);
                if(u.getKingdom() == null){
                    ice.getWhoClicked().closeInventory();
                    return;
                }
                TUser clickUser = getPlugin().getTUserManager().getOrNewTUser((Player)ice.getWhoClicked());
                Rank rank = clickUser.getKingdom().getRank(ice.getWhoClicked().getUniqueId());
                if (!rank.checkPerm(Rank.RankPerm.ADJUSTBANTIME)){
                    ice.getWhoClicked().closeInventory();
                    return;
                }
                if (!u.getKingdom().canBanTime(banTimeIndex)){
                    ice.getWhoClicked().closeInventory();
                    ice.getWhoClicked().sendMessage(tcm("&cYou need to leave three continuous hours available for raiding!"));
                    return;
                }
                ItemStack updateItem;
                if(u.getKingdom().setBanTime(banTimeIndex)){
                    updateItem = new ItemStack(red);
                }else{
                    updateItem = new ItemStack(green);
                }
                ice.setCurrentItem(setDisplayName(updateItem, timeDisplay));
            });
            fillRest(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        }

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
