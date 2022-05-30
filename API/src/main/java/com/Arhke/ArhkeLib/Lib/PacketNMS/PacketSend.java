//package com.Arhke.ArhkeLib.Lib.PacketNMS;
//
//import net.md_5.bungee.api.chat.BaseComponent;
//import net.md_5.bungee.api.chat.TextComponent;
//import net.md_5.bungee.chat.ComponentSerializer;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.meta.BookMeta;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//public class PacketSend {
//    public void sendPacket(Player player) {
////        PacketPlayOutOpenBook packet2 = new PacketPlayOutOpenBook(EnumHand.a);
////
////        CraftPlayer craftPlayer = (CraftPlayer)player;
////        craftPlayer.getHandle().b.a(packet2, (GenericFutureListener)null);
//    }
//
//    public void buildPage(Player player, BookMeta meta, List<String> configPages, HashMap<String, String> interactivePlaceholders) {
//        Iterator var5 = configPages.iterator();
//
//        while(var5.hasNext()) {
//            String page = (String)var5.next();
////            TextComponent tc = NMSUtil.pageToTextComponent(player, page, interactivePlaceholders);
////            String json = ComponentSerializer.toString(tc);
//            BaseComponent[] components = ComponentSerializer.parse(json);
//            meta.spigot().addPage(new BaseComponent[][]{components});
//        }
//
//    }
//}
