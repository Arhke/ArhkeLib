package com.Arhke.WRCore;

import com.Arhke.WRCore.District.core.Feudal;
import com.Arhke.WRCore.District.kingdoms.Rank;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.CauldronFurniture;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.Smeltery;
import com.Arhke.WRCore.ItemSystem.FurnitureMapping.Tannery;
import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.ConfigManager;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.FileIO.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class ConfigLoader extends MainBase {
    Map<ConfigFile, ConfigManager> configMap = new HashMap<>();
    public ConfigLoader (Main instance){
        super(instance);
        for (ConfigFile configFile: ConfigFile.values()){
            File file = Paths.get(getPlugin().getDataFolder().toString(),configFile.getFilePaths()).toFile();
            FileManager fm = new FileManager(file);
            configFile.createConfigDefaults(fm);
            fm.save();
            ConfigManager cm = new ConfigManager(fm);
            configMap.put(configFile, cm);
        }
        setStaticConfigs();
//        getPlugin().saveResource("ItemSystem/CustomItem/Steel.yml", true);
//        getPlugin().saveResource("ItemSystem/CustomItem/Aspirin.yml", true);
//        getPlugin().saveResource("ItemSystem/CustomItem/WraithHide.yml", true);
//        getPlugin().saveResource("ItemSystem/Recipe/SteelRecipe.yml", true);
//        getPlugin().saveResource("ItemSystem/Materials.yml", true);

    }
    public static final String RankPermKey = "rankPerm";
    public void setStaticConfigs() {
        DataManager rankPerm = getConfigManager(ConfigFile.DISTRICTCONFIG).getDataManager(RankPermKey);
        Rank.LEADER.setPerm(rankPerm.getStringList("leader"));
        Rank.EXECUTIVE.setPerm(rankPerm.getStringList("executive"));
        Rank.MEMBER.setPerm(rankPerm.getStringList("member"));
        Rank.GUEST.setPerm(rankPerm.getStringList("guest"));

        DataManager materials = getConfigManager(ConfigFile.VALIDMATERIALS);
        Smeltery.ValidMaterial.addAll(materials.buildMaterialSet("smeltery", "valid"));
        Smeltery.ExplodeMaterial.addAll(materials.buildMaterialSet("smeltery", "explode"));
        CauldronFurniture.ValidMaterial.addAll(materials.buildMaterialSet("tannery", "valid"));
        CauldronFurniture.ExplodeMaterial.addAll(materials.buildMaterialSet("tannery", "explode"));

    }

    /**
     * Returns the ConfigManage of the specified ConfigFile Enum, returns null if not found
     * @param cf
     * @return
     */
    @Nullable
    public ConfigManager getConfigManager(ConfigFile cf){
        return configMap.get(cf);
    }

    public enum ConfigFile{
        TECHCMDLANG("Lang", "techcommand.yml"){
            @Override
            public void createConfigDefaults(FileManager fm) {

            }
        },
        TOWNCMDLANG("Lang", "towncommand.yml"){
            @Override
            public void createConfigDefaults(FileManager fm) {

            }
        },RTPCMDLANG("Lang", "rtpcommand.yml"){
            @Override
            public void createConfigDefaults(FileManager fm) {

            }
        },
        KINGDOMCMDLANG("Lang", "kingdomcommand.yml"){
            @Override
            public void createConfigDefaults(FileManager fm) {

            }
        },
        DLISTENERLANG("Lang", "districtlistener.yml"){
            @Override
            public void createConfigDefaults(FileManager fm) {
                YamlConfiguration c = fm.getConfig();
                DataManager dm = fm.getDataManager();
                //===============<Land>==============
                if(!c.isString("land.unconquered"))
                    c.set("land.unconquered", "Unconquered Land");
                if(!c.isString("land.enter"))
                    c.set("land.enter", "&7~{0}{1}&7~");
                if(!c.isString("land.deny.place"))
                    c.set("land.deny.place", "&cYou may not place blocks here");
                if(!c.isString("land.deny.break"))
                    c.set("land.deny.break", "&cYou may not break blocks here");
                if(!c.isString("land.deny.kill"))
                    c.set("land.deny.kill", "&cYou may not harm that here");
                dm.isOrDefault("&cYou may not interact with that here.", "land.deny.interact");
                dm.isOrDefault("&cYou may not do that here.", "land.deny.generic");
                dm.isOrDefault("&6&lThis chunk has {0} hp left before it is raid-able.", "land.deny.breakWall");
                dm.isOrDefault("&cYou may not place wall blocks during battle as an defender.", "land.deny.placeWall");
                dm.isOrDefault("&cYou may only place ladders during battle as an attacker.", "land.deny.placeLadder");
                if(!c.isString("land.place.seaWind"))
                    c.set("land.place.seaWind", "&7Oh no, the strong sea wind blew your block apart.");
                if(!c.isString("land.place.seaCurrent"))
                    c.set("land.place.seaCurrent", "&7Oh no, the strong sea current washed your block away.");
                if(!c.isString("land.break.seaWind"))
                    c.set("land.break.seaWind", "&7Oh no, the strong sea wind quickly rebuilds the broken block.");
                if(!c.isString("land.break.seaCurrent"))
                    c.set("land.break.seaCurrent", "&7Oh no, the strong sea current quickly repairs the broken block.");

                //====================<battle>==================================
                if(!c.isString("battle.notInWar"))
                    c.set("battle.notInWar", "&cYou must in war with that town in order to battle that town.");
                if(!c.isString("battle.notAccepted"))
                    c.set("battle.notAccepted", "&cA challenge must be accepted in order to initiate a battle.");
                if(!c.isString("battle.alreadyBattle"))
                    c.set("battle.alreadyBattle", "&cThere is already an ongoing battle raging about.");
                if(!c.isString("battle.tornadoAlert"))
                    c.set("battle.tornadoAlert", "&6&lTown of &7{0} &6&lis under a &7{2} &6&lsiege from &7{1}.");
                if(!c.isString("battle.prepTitle"))
                    c.set("battle.prepTitle", "&5Prepping for &cWarCry: &5Town of &7{1} &5vs &7{2}");
                if(!c.isString("battle.battleTitle"))
                    c.set("battle.battleTitle", "Town of &cWarCry: &7{0} &cvs &7{1}");
                dm.isOrDefault("&a{0}'s Island Core", "battle.coreName");
                dm.isOrDefault("&6&l{0} has successfully defended their town!", "battle.defenderWin");
                dm.isOrDefault("&7{0} was deposited into your capital town bank for winning the NightRaid", "battle.receiveMoney");
                dm.isOrDefault("&7{0}&6 has lost &7{0} &6in a NightRaid against &7{1}", "battle.defenderLostRaid");
                dm.isOrDefault("&7{0}&6 has lost their town &7{1} &6in a WarCry against &7{2}", "battle.defenderLostTown");
                dm.isOrDefault("&7{0}&6 has fallen in a raid against &7{1}.\n&6They prospered for &7{2} days.\n&7My deepest condolences to members of {0}.", "battle.kingdomFall");
                //=====================<Chat Formats>==================
                if(!c.isString("town.chat"))
                    c.set("town.chat", "&8&lT&r&8-&f{0}&8[&f{1}&8|&f{2}&8]&5>>&b{3}");
                if(!c.isString("kingdom.chat"))
                    c.set("kingdom.chat", "&8[&7{0}&f|&7{1}&8] {2}&f: &a{3}");
                fm.getDataManager().isOrDefault("&6&7{0} &6lire (&7{1}%&6) were taken out of your earnings as tribute to &7{2}", "tributeMsg");

            }
        },
        ILISTENERLANG("Lang","itemsystemListener.yml"){
            @Override
            public void createConfigDefaults(FileManager fm){
                DataManager dm = fm.getDataManager();
                dm.isOrDefault("&cYou may not place down molten metal.", "moltenMetalPlace");
                DataManager smeltery = dm.getDataManager("smeltery");
                smeltery.isOrDefault(5, "explodeIntensity");
                smeltery.isOrDefault("&aThis is a Forge! Right click the furnace with items to add to the forge.", "notSmelting");
                smeltery.isOrDefault("&7This Forge is Smelting...", "isSmelting");
                smeltery.isOrDefault("&7This Forge has been Smelting for &6{0} &7minutes.", "smeltTimer");
                smeltery.isOrDefault("&7I'm sorry that does not fit in the forge.", "doesNotFit");
                DataManager cauldron = dm.getDataManager("cauldron");
                cauldron.isOrDefault("&aThis is an empty Cauldron! Right click the Cauldron with items to add things to it.", "empty");
                cauldron.isOrDefault("&7This Cauldron has items mixed in it...", "notEmpty");
                cauldron.isOrDefault("&7I'm sorry that does not fit in the cauldron.", "doesNotFit");
                cauldron.isOrDefault(5, "explodeIntensity");
                DataManager coolant = dm.getDataManager("coolant");
                coolant.isOrDefault("&7Please fill it up with water to continue to cool metal.", "empty");
                coolant.isOrDefault("&7The metal turned into dust as it cooled.", "dust");
                coolant.isOrDefault("&7The Metal Crystallized, and broke your bucket.", "crystallize");
                coolant.isOrDefault("&7The Metal cools and hardens.", "harden");
                DataManager tannery = dm.getDataManager("tannery");
                tannery.isOrDefault("&aThis is a Tannery, right click with a treated hide to hang it up.", "notDrying");
                tannery.isOrDefault("&7A hide is currently drying..", "isDrying");
                tannery.isOrDefault("&7This hide has been drying for &6{0} &7minutes.", "dryTimer");
                tannery.isOrDefault("&7The hide turned into dust as you went to retrieve it.", "dust");





            }
        },
        DISTRICTCONFIG("District", "config.yml"){
            @Override
            public void createConfigDefaults(FileManager fm){
                YamlConfiguration c = fm.getConfig();
                DataManager dm = fm.getDataManager();
                dm.isOrDefault(3, "town.name.minLength");
                if(!c.isInt("town.name.maxLength"))
                    c.set("town.name.maxLength", 10);

                List<String> banned = new ArrayList<>();
                banned.add("fuck");
                banned.add("bitch");
                if(!c.isList("town.name.banned"))
                    c.set("town.name.banned", banned);
                if(!c.isBoolean("town.name.allowSymbols"))
                    c.set("town.name.allowSymbols", false);
                if(!c.isDouble("town.cost"))
                    c.set("town.cost", 300);
                if(!c.isInt("town.maxMembers"))
                    c.set("town.maxMembers", 15);
                //============<Town Land>===============
                if(!c.isBoolean("town.land.sendTitles"))
                    c.set("town.land.sendTitles", true);
                if(!c.isBoolean("town.land.protectBlockBreak"))
                    c.set("town.land.protectBlockBreak", true);
                dm.isOrDefault(true, "town.land.protectInteract");
                dm.isOrDefault(true, "town.land.protectMovement");
                if(!c.isBoolean("town.land.protectSpawners"))
                    c.set("town.land.protectSpawners", true);
                //================<Town Member>============
                if(!c.isBoolean("town.member.keepInv"))
                    c.set("town.member.keepInv", true);
                if(!c.isBoolean("town.member.keepExp"))
                    c.set("town.member.keepExp", true);
                //==================<Kingdom>================
                if(!c.isInt("kingdom.name.minLength"))
                    c.set("kingdom.name.minLength", 3);
                if(!c.isInt("kingdom.name.maxLength"))
                    c.set("kingdom.name.maxLength", 10);
                List<String> banned1 = new ArrayList<>();
                banned.add("fuck");
                banned.add("bitch");
                if(!c.isList("kingdom.name.banned"))
                    c.set("kingdom.name.banned", banned1);
                if(!c.isBoolean("kingdom.name.allowSymbols"))
                    c.set("kingdom.name.allowSymbols", false);
                if(!c.isDouble("kingdom.cost"))
                    c.set("kingdom.cost", 300);
                if(!c.isInt("kingdom.maxTowns"))
                    c.set("kingdom.maxTowns", 15);
                if(!c.isInt("kingdom.inactiveDeleteDays"))
                    c.set("kingdom.inactiveDeleteDays", 30);




                if(!c.isInt("kingdom.challenges.minBet"))
                    c.set("kingdom.challenges.minBet", 500);
                //========<Reputation>=========
                if(!c.isInt("reputation.max"))
                    c.set("reputation.max", 10000);
                if(!c.isInt("reputation.min"))
                    c.set("reputation.min", 0);
                if(!c.isInt("reputation.start")) {
                    c.set("reputation.start", 100);
                }
                if(!c.isInt("reputation.kickPlayerFromKingdom"))
                    c.set("reputation.kickPlayerFromKingdom", 1);
                if(!c.isInt("reputation.playerKill"))
                    c.set("reputation.playerKill", 10);
                if(!c.isInt("reputation.playerDeath"))
                    c.set("reputation.playerDeath", -10);

                //============<Rank Perm Section>=============
                ConfigurationSection rankPermConfig = fm.getDataManager().getDataManager(RankPermKey).getConfig();
                if(!rankPermConfig.isList(Rank.LEADER.name().toLowerCase())){
                    List<String> perm = new ArrayList<>();
                    perm.add("BUILD");
                    perm.add("KICK");
                    perm.add("INVITE");
                    perm.add("DEPOSIT");
                    perm.add("WITHDRAW");
                    perm.add("OPENTOWN");
                    perm.add("VIEWBANK");
                    perm.add("PROMOTE");
                    perm.add("DEMOTE");
                    perm.add("CHANGENAME");
                    perm.add("CLAIM");
                    perm.add("UNCLAIM");
                    perm.add("SETHOME");
                    perm.add("NEUTRAL");
                    perm.add("ALLY");
                    perm.add("ENEMY");
                    perm.add("INVITETOWNS");
                    perm.add("COFFERVIEW");
                    perm.add("COFFERDEPOSIT");
                    perm.add("COFFERWITHDRAW");
                    perm.add("KICKTOWN");
                    perm.add("TOGGLESHIELD");
                    perm.add("DEMOTEKINGDOM");
                    perm.add("PROMOTEKINGDOM");
                    perm.add("CHANGENAMEKINGDOM");
                    perm.add("BUILDKINGDOM");
                    perm.add("JOINKINGDOM");
                    perm.add("LEAVEKINGDOM");
                    perm.add("CREATEKINGDOM");
                    rankPermConfig.set(Rank.LEADER.name().toLowerCase(), perm);
                }
                if(!rankPermConfig.isList(Rank.EXECUTIVE.name().toLowerCase())){
                    List<String> perm = new ArrayList<>();
                    perm.add("BUILD");
                    perm.add("KICK");
                    perm.add("INVITE");
                    perm.add("DEPOSIT");
                    perm.add("WITHDRAW");
                    perm.add("OPENTOWN");
                    perm.add("PROMOTE");
                    perm.add("DEMOTE");
                    perm.add("JOINKINGDOM");
                    perm.add("LEAVEKINGDOM");
                    perm.add("CREATEKINGDOM");
                    perm.add("CLAIM");
                    perm.add("UNCLAIM");
                    perm.add("SETHOME");
                    perm.add("NEUTRAL");
                    perm.add("ALLY");
                    perm.add("ENEMY");
                    perm.add("INVITETOWNS");
                    perm.add("COFFERVIEW");
                    perm.add("COFFERDEPOSIT");
                    perm.add("COFFERWITHDRAW");
                    perm.add("KICKTOWN");
                    perm.add("TOGGLESHIELD");
                    perm.add("DEMOTEKINGDOM");
                    perm.add("PROMOTEKINGDOM");
                    perm.add("CHANGENAMEKINGDOM");
                    perm.add("BUILDKINGDOM");
                    rankPermConfig.set(Rank.EXECUTIVE.name().toLowerCase(), perm);
                }
                if(!rankPermConfig.isList(Rank.MEMBER.name().toLowerCase())){
                    List<String> perm = new ArrayList<>();
                    perm.add("BUILD");
                    perm.add("INVITE");
                    perm.add("COFFERVIEW");
                    perm.add("COFFERDEPOSIT");
                    rankPermConfig.set(Rank.MEMBER.name().toLowerCase(), perm);
                }
                if(!rankPermConfig.isList(Rank.GUEST.name().toLowerCase())){
                    List<String> perm = new ArrayList<>();
                    perm.add("BUILD");
                    rankPermConfig.set(Rank.GUEST.name().toLowerCase(), perm);
                }

                //============<Misc>==========
                if(!c.isInt("seabed"))
                    c.set("seabed", 30);
                if(!c.isInt("seaLevel"))
                    c.set("seaLevel",63);

                List<String> worlds = new ArrayList<>();
                worlds.add("NotAWorldYouCanClaim");
                if(!c.isList("noClaimWorlds"))
                    c.set("noClaimWorlds", worlds);
                if(!c.isList("bannedWorlds"))
                    c.set("bannedWorlds", worlds);
                //==========<Tax>===========
                DataManager taxConfig = dm.getDataManager("tax");
                taxConfig.isOrDefault(5, "landConst");
                taxConfig.isOrDefault(5, "townConst");
                taxConfig.isOrDefault(5, "memberConst");
                taxConfig.isOrDefault("&7{0} &6has been deducted from your capital bank for daily kingdom tax.", "kingdomTaxMsg");
                taxConfig.isOrDefault("&7Your town has been removed for failing to pay taxes two days in a row", "townFailMsg");
                taxConfig.isOrDefault("&7{0} &6has been deducted from your town bank for hourly town tax.", "townTaxMsg");
                taxConfig.isOrDefault("&7Your Kingdom has been removed for failing to pay taxes two days in a row", "townFailMsg");
                taxConfig.isOrDefault("&7{0} &6has been deducted from your capital bank for attacking challenge tax\n(Note that this will increase by 20% every day).", "attackerTaxMsg");
                taxConfig.isOrDefault("&7{0} &6has been deducted from your town bank for defend tribute tax.", "defenderTownTaxMsg");
                taxConfig.isOrDefault("&7{0} &6has been deducted from your capital bank for defending challenge tax\n(Note that this is given to the challenging nation).", "defenderTaxMsg");
                taxConfig.isOrDefault("&7{0} &6has been added to your capital bank from your tributes.", "tributePaidMsg");


//                c.set("landMustBeConnected", true);
//                //1.12.3
//                ArrayList<String> customCommands = new ArrayList<String>();
//                customCommands.add("realm");
//                customCommands.add("faction");
//                c.set("customCommands", customCommands);
//                //1.12.3
//
//                ArrayList<String> bannedWorlds = new ArrayList<String>();
//                bannedWorlds.add("nonFeudalWorld");
//                bannedWorlds.add("nonFeudalWorld_nether");
//                bannedWorlds.add("nonFeudalWorld_end");
//                c.set("bannedWorlds", bannedWorlds);
//                ArrayList<String> noClaimWorlds = new ArrayList<String>();
//                noClaimWorlds.add("nonClaimWorld");
//                noClaimWorlds.add("nonClaimWorld_nether");
//                noClaimWorlds.add("nonClaimWorld_end");
//                c.set("noClaimWorlds", noClaimWorlds);
//
//                c.set("kingdom.name.maxLength", 20);
//                c.set("kingdom.name.minLength", 3);
//                c.set("kingdom.name.allowSymbols", false);
//                ArrayList<String> bannedNames = new ArrayList<String>();
//                bannedNames.add("curseWordsHere");
//                bannedNames.add("racistWordsHere");
//                bannedNames.add("Forseth11");
//                bannedNames.add("Minecraft");
//                bannedNames.add("Kingdom");
//                bannedNames.add("Kingdoms");
//                c.set("kingdom.challenges.lengthInHours", 168);
//                c.set("kingdom.challenges.forceScoreboards", true);
//                c.set("kingdom.challenges.enableScoreboards", true);
//                c.set("kingdom.challenges.startTax", 50.0);
//                c.set("kingdom.challenges.taxAdd", 15.0);
//                c.set("kingdom.challenges.taxPerMinuteWhileFightersOnline", true);
//                c.set("kingdom.challenges.minutesToWinWhileFightersOnlineBeforeAccepting", 100);
//                c.set("kingdom.challenges.maxDefence", 3);
//                c.set("kingdom.challenges.winban", true);
//                c.set("kingdom.challenges.winland", true);
//                c.set("kingdom.challenges.onlinePercentNeededForAttackersToWinByDefault", 70.0);
//                c.set("kingdom.challenges.secondsToBeginAttack", 600);
//                c.set("kingdom.challenges.secondsToDefendPerChunk", 30);
//                c.set("kingdom.challenges.percentOfTreasuryWonByDefault", 20.0);
//                c.set("kingdom.challenges.percentOfTreasuryWon", 85.0);
//                c.set("kingdom.challenges.attackerCounterAttackBanInHours", 168);
//                c.set("kingdom.challenges.defenderCounterAttackBanInHours", 48);
//                c.set("kingdom.challenges.nobleWinXPMultiplier", 1.35);
//                c.set("kingdom.challenges.xpWin", 20000);
//                c.set("kingdom.challenges.xpWinDefault", 8000);
//                c.set("kingdom.challenges.percentAttackMembersNeededToFight", 40.0);
//                c.set("kingdom.challenges.percentDefenderMembersNeededToFight", 40.0);
//                c.set("kingdom.challenges.claimAgeRequiredHours", 120.0);
//                c.set("kingdom.challenges.fighterSeparation", 2);
//
//                c.set("kingdom.challenges.keepInventory", true);
//                c.set("kingdom.challenges.loseLand", true);
//
//                //1.8.1
//                c.set("kingdom.chat.symbols.enabled", true);
//                c.set("kingdom.chat.symbols.ally", "!");
//                c.set("kingdom.chat.symbols.kingdom", ":");
//                //
//
//                c.set("kingdom.keepInventoryOnLand", true);
//
//                c.set("kingdom.shield.1.price", 15000);
//                c.set("kingdom.shield.2.price", 30000);
//                c.set("kingdom.shield.3.price", 50000);
//                c.set("kingdom.shield.vacation.price", 5000);
//                c.set("kingdom.shield.1.coolDownDays", 30);
//                c.set("kingdom.shield.2.coolDownDays", 45);
//                c.set("kingdom.shield.3.coolDownDays", 60);
//                c.set("kingdom.shield.vacation.coolDownDays", 25);
//
//                c.set("kingdom.landProtection.blockPlace", true);
//                c.set("kingdom.landProtection.blockBreak", true);
//                c.set("kingdom.landProtection.chestAccess", true);
//                c.set("kingdom.landProtection.piston", true);
//                c.set("kingdom.landProtection.creeperExplosion", false);
//                c.set("kingdom.landProtection.otherExplosion", true);
//                c.set("kingdom.landProtection.lighter", true);
//                c.set("kingdom.landProtection.interact", true);
//                c.set("kingdom.landProtection.fireSpread", true);
//                c.set("kingdom.landProtection.enderman", false);
//                c.set("kingdom.landProtection.enderDragon", false);
//                c.set("kingdom.landProtection.villagers", true);
//                c.set("kingdom.landProtection.enderpearls", true);
//
//                c.set("kingdom.home.teleportDelayInSeconds", 3);
//
//                c.set("kingdom.shieldProtection.blockPlace", true);
//                c.set("kingdom.shieldProtection.blockBreak", true);
//                c.set("kingdom.shieldProtection.chestAccess", true);
//                c.set("kingdom.shieldProtection.piston", true);
//                c.set("kingdom.shieldProtection.creeperExplosion", true);
//                c.set("kingdom.shieldProtection.otherExplosion", true);
//                c.set("kingdom.shieldProtection.lighter", true);
//                c.set("kingdom.shieldProtection.interact", true);
//                c.set("kingdom.shieldProtection.fireSpread", true);
//                c.set("kingdom.shieldProtection.enderman", true);
//                c.set("kingdom.shieldProtection.enderDragon", true);
//                c.set("kingdom.shieldProtection.villagers", true);
//                c.set("kingdom.shieldProtection.enderpearls", true);
//
//                //c.set("kingdom.tax.land", 2.0); OLD
//                c.set("kingdom.land.sendtitles", true);
//                c.set("kingdom.land.tax", 2.0);
//
//                List<String> cmds = new ArrayList<String>();
//                cmds.add("/sethome");
//                cmds.add("/home");
//                cmds.add("/t home");
//                cmds.add("/tpa");
//                cmds.add("/tpahere");
//                c.set("kingdom.land.bannedcommands", cmds);
//                c.set("kingdom.inactiveDeleteDays", 60);
//                c.set("kingdom.cost", 0.0);
//                c.set("kingdom.startShieldHours", 120);
            }
        },
        VALIDMATERIALS("ItemSystem", "Materials.yml"){
            @Override
            public void createConfigDefaults(FileManager fm){
                DataManager dm = fm.getDataManager();
                List<String> smelteryValid = new ArrayList<>(Collections.singleton(Material.IRON_INGOT.name()));
                List<String> smelteryExplode = new ArrayList<>(Collections.singleton(Material.TNT.name()));
                List<String> tanneryValid = new ArrayList<>(Collections.singleton(Material.REDSTONE.name()));
                List<String> tanneryExplode = new ArrayList<>(Collections.singleton(Material.TNT.name()));
                DataManager smeltery = dm.getDataManager("smeltery");
                smeltery.isOrDefault(smelteryValid, "valid");
                smeltery.isOrDefault(smelteryExplode, "explode");

                DataManager tannery = dm.getDataManager("tannery");
                tannery.isOrDefault(tanneryValid, "valid");
                tannery.isOrDefault(tanneryExplode, "explode");
            }
        };
        private String[] filePaths;
        ConfigFile ( String... filePath) {
            
            this.filePaths = filePath;
        }
        public abstract void createConfigDefaults(FileManager fm);
        public String[] getFilePaths() {
            return this.filePaths;
        }
    }


}
