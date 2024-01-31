//package com.Arhke.ArhkeLib.Utils.SyncScheduler;
//
//import net.legamemc.booknews.admin.Sound;
//import net.legamemc.booknews.nms.NMSUtil;
//import org.apache.commons.lang.Validate;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.PlayerInventory;
//import org.bukkit.inventory.meta.BookMeta;
//
//import java.nio.ByteBuffer;
//
//public class BookGen {
//    private static ItemStack newBook(String title, String author, String... pages) {
//        ItemStack is = new ItemStack(Material.WRITTEN_BOOK, 1);
//        BookMeta meta = (BookMeta) is.getItemMeta();
//        meta.setAuthor(author);
//        meta.setTitle(title);
//        meta.setPages(pages);
//        is.setItemMeta(meta);
//        return is;
//    }
//
//    private void openBook(ItemStack book, Player p) {
//        int slot = p.getInventory().getHeldItemSlot();
//        org.bukkit.inventory.ItemStack old = p.getInventory().getItem(slot);
//        p.getInventory().setItem(slot, book);
//
//        ByteBuffer buf = Unpooled.buffer(256);
//        buf.setByte(0, 0);
//        buf.writerIndex(1);
//        ByteBuf buf = Unpooled.buffer(256);
//        buf.setByte(0, 0);
//        buf.writerIndex(1);
//        NMSUtil.getNMS().sendPacket(p, buf);
//        p.getInventory().setItem(slot, oldItem);
//
//        (new Sound(this.plugin)).playerSound(p);
//        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
//        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
//        p.getInventory().setItem(slot, old);
//    }
//    public void openBook(Player player, ItemStack book) {
//        player.closeInventory();
//        Validate.notNull(book, "The ItemStack is null! This is not an error with CitizensBooks," +
//                " so please don't report it. Make sure the plugins that uses CitizensBooks as dependency are correctly configured.");
//        Validate.isTrue(book.getType() == Material.WRITTEN_BOOK, "The ItemStack is not a written book! This is not an error with CitizensBooks," +
//                " so please don't report it. Make sure the plugins that uses CitizensBooks as dependency are correctly configured.");
//        int slot = player.getInventory().getHeldItemSlot();
//        ItemStack old = player.getInventory().getItem(slot);
//        PlayerInventory pi = player.getInventory();
//        pi.setItem(slot, book);
//        try {
//            this.rightClick(player);
//        } catch (Exception ex) {
//            this.plugin.getLogger().log(Level.WARNING, "Something went wrong!", ex);
//        }
//        if (old != null)
//            pi.setItem(slot, old.clone());
//        else
//            pi.setItem(slot, null);
//    }
//    protected void rightClick(Player player) {
//        this.distribution.sendRightClick(player);
//    }
//
//}
