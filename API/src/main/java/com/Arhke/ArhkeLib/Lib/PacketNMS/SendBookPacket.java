//package com.Arhke.ArhkeLib.Lib.PacketNMS;
//
//import me.clip.placeholderapi.PlaceholderAPI;
//import net.md_5.bungee.api.chat.*;
//import net.md_5.bungee.chat.ComponentSerializer;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.Sound;
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.BookMeta;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class SendBookPacket {
//    public void buildPage(Player player, BookMeta meta, List<String> configPages, HashMap<String, String> interactivePlaceholders) {
//        Iterator var5 = configPages.iterator();
//
//        while (var5.hasNext()) {
//            String page = (String) var5.next();
//            TextComponent tc = NMSUtil.pageToTextComponent(player, page, interactivePlaceholders);
//            String json = ComponentSerializer.toString(tc);
//            BaseComponent[] components = ComponentSerializer.parse(json);
//            meta.spigot().addPage(new BaseComponent[][]{components});
//        }
//
//    }
//
//    public void onOpen(Player p) {
//        int slot = p.getInventory().getHeldItemSlot();
//        ItemStack oldItem = p.getItemInHand();
//        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
//        BookMeta meta = (BookMeta) book.getItemMeta();
//        ConfigurationSection cs = this.plugin.getConfig().getConfigurationSection("book");
//        List<String> pagesList = new ArrayList();
//        Iterator var8 = cs.getKeys(false).iterator();
//
//        while (var8.hasNext()) {
//            String key = (String) var8.next();
//            pagesList.add(cs.getString(key));
//        }
//
//        meta.setAuthor("Server");
//        meta.setTitle("News");
//        NMSUtil.getNMS().buildPage(p, meta, pagesList, this.plugin.getInteractivesPlaceholders());
//        book.setItemMeta(meta);
//        p.getInventory().setItem(slot, book);
//        ByteBuf buf = Unpooled.buffer(256);
//        buf.setByte(0, 0);
//        buf.writerIndex(1);
//        NMSUtil.getNMS().sendPacket(p, buf);
//        p.getInventory().setItem(slot, oldItem);
//        (new Sound(this.plugin)).playerSound(p);
//    }
//
//    public static TextComponent pageToTextComponent(Player player, String page, HashMap<String, String> interactivePlaceholderMap) {
//        page = color(page);
//        List<String> textList = new ArrayList<>();
//        Pattern pattern = Pattern.compile("%booknews_.*?%");
//
//        for (Matcher matcher = pattern.matcher(page); matcher.find(); matcher = pattern.matcher(page)) {
//            int start = matcher.start();
//            int end = matcher.end();
//            if (start != 0) {
//                textList.add(page.substring(0, start));
//            }
//
//            String result = page.substring(start, end);
//            textList.add(result);
//            page = page.substring(end);
//        }
//
//        if (!page.isEmpty()) {
//            textList.add(page);
//        }
//
//        TextComponent textComponent = new TextComponent("");
//        FileConfiguration config = Main.getInstance().getConfig();
//        ConfigurationSection interactiveRootSection = config.getConfigurationSection("Interactive-Word");
//        boolean hasPlaceholderAPI = Main.getInstance().PlaceholderAPI;
//
//        for (String text : textList) {
//            String updatedText;
//            if (interactivePlaceholderMap.containsKey(text)) {
//                updatedText = interactivePlaceholderMap.get(text);
//                ConfigurationSection interactiveSection = interactiveRootSection.getConfigurationSection(updatedText);
//                String word = interactiveSection.getString("word");
//                BaseComponent interactiveWordComponent = new TextComponent(word);
//
//                for (String key : interactiveSection.getKeys(false)) {
//                    String var18 = key.toLowerCase();
//                    byte var19 = -1;
//                    switch (var18.hashCode()) {
//                        case -1771105512:
//                            if (var18.equals("underlined")) {
//                                var19 = 3;
//                            }
//                            break;
//                        case -1178781136:
//                            if (var18.equals("italic")) {
//                                var19 = 2;
//                            }
//                            break;
//                        case -771300846:
//                            if (var18.equals("clickevent")) {
//                                var19 = 5;
//                            }
//                            break;
//                        case 3029637:
//                            if (var18.equals("bold")) {
//                                var19 = 1;
//                            }
//                            break;
//                        case 94842723:
//                            if (var18.equals("color")) {
//                                var19 = 0;
//                            }
//                            break;
//                        case 148487876:
//                            if (var18.equals("obfuscated")) {
//                                var19 = 4;
//                            }
//                            break;
//                        case 275844830:
//                            if (var18.equals("hoverevent")) {
//                                var19 = 6;
//                            }
//                    }
//
//                    String hoverText;
//                    switch (var19) {
//                        case 0:
//                            String color = interactiveSection.getString(key).toUpperCase();
//                            net.md_5.bungee.api.ChatColor textColor = null;
//                            boolean foundColor = false;
//                            if (versionNumber >= 16) {
//                                Matcher hexMatcher = HEX_PATTERN.matcher(color);
//                                if (hexMatcher.find()) {
//                                    textColor = net.md_5.bungee.api.ChatColor.of(color);
//                                    if (textColor == null) {
//                                        textColor = net.md_5.bungee.api.ChatColor.BLACK;
//                                    } else {
//                                        foundColor = true;
//                                    }
//                                }
//                            }
//
//                            if (!foundColor) {
//                                try {
//                                    textColor = net.md_5.bungee.api.ChatColor.valueOf(interactiveSection.getString(key).toUpperCase());
//                                } catch (Exception var29) {
//                                    textColor = net.md_5.bungee.api.ChatColor.BLACK;
//                                }
//                            }
//
//                            interactiveWordComponent.setColor(textColor);
//                            break;
//                        case 1:
//                            interactiveWordComponent.setBold(interactiveSection.getBoolean(key));
//                            break;
//                        case 2:
//                            interactiveWordComponent.setItalic(interactiveSection.getBoolean(key));
//                            break;
//                        case 3:
//                            interactiveWordComponent.setUnderlined(interactiveSection.getBoolean(key));
//                            break;
//                        case 4:
//                            interactiveWordComponent.setObfuscated(interactiveSection.getBoolean(key));
//                            break;
//                        case 5:
//                            if (interactiveSection.getBoolean(key + ".enable")) {
//                                String actionName = interactiveSection.getString(key + ".action");
//                                String value = interactiveSection.getString(key + ".value");
//                                if (actionName != null && value != null) {
//                                    value = value.replace("%player%", player.getName());
//                                    hoverText = actionName.toLowerCase();
//                                    byte var39 = -1;
//                                    switch (hoverText.hashCode()) {
//                                        case -1654598210:
//                                            if (hoverText.equals("change_page")) {
//                                                var39 = 2;
//                                            }
//                                            break;
//                                        case -504306182:
//                                            if (hoverText.equals("open_url")) {
//                                                var39 = 1;
//                                            }
//                                            break;
//                                        case -404256420:
//                                            if (hoverText.equals("copy_to_clipboard")) {
//                                                var39 = 3;
//                                            }
//                                            break;
//                                        case 1845855639:
//                                            if (hoverText.equals("run_command")) {
//                                                var39 = 0;
//                                            }
//                                    }
//
//                                    switch (var39) {
//                                        case 0:
//                                        case 1:
//                                        case 2:
//                                        case 3:
//                                            try {
//                                                interactiveWordComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(actionName.toUpperCase()), value));
//                                            } catch (IllegalArgumentException var28) {
//                                                Logger.error(actionName + " doesn't exist in the version you are using");
//                                            }
//                                            break;
//                                        default:
//                                            Logger.error(actionName + " is not a valid action! Available actions: [OPEN_URL/RUN_COMMAND/CHANGE_PAGE/COPY_TO_CLIPBOARD]");
//                                    }
//                                }
//                            }
//                            break;
//                        case 6:
//                            hoverText = interactiveSection.getString(key + ".text");
//                            if (hasPlaceholderAPI) {
//                                hoverText = PlaceholderAPI.setPlaceholders(player, hoverText);
//                            }
//
//                            String coloredHoverText = color(hoverText);
//                            if (interactiveSection.getBoolean(key + ".enable")) {
//                                interactiveWordComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(coloredHoverText)).create()));
//                            }
//                    }
//                }
//
//                textComponent.addExtra(interactiveWordComponent);
//            } else {
//                updatedText = text;
//                if (hasPlaceholderAPI) {
//                    updatedText = PlaceholderAPI.setPlaceholders(player, text);
//                }
//
//                BaseComponent[] baseComponentArray = TextComponent.fromLegacyText(updatedText);
//                BaseComponent[] var14 = baseComponentArray;
//                int var15 = baseComponentArray.length;
//
//                for (int var16 = 0; var16 < var15; ++var16) {
//                    BaseComponent component = var14[var16];
//                    textComponent.addExtra(component);
//                }
//            }
//        }
//
//        return textComponent;
//    }
//
//    public static String color(String text) {
//        if (versionNumber >= 16) {
//            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
//
//            for (Matcher matcher = pattern.matcher(text); matcher.find(); matcher = pattern.matcher(text)) {
//                String color = text.substring(matcher.start(), matcher.end());
//                text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
//            }
//        }
//
//        return ChatColor.translateAlternateColorCodes('&', text);
//    }
//}
