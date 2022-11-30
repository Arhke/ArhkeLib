package com.Arhke.WRCore.District.Commands.TownCommand;

import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Michael Forseth
 *
 */
public class TownInfoCommand extends SubCommandsBase {
    public TownInfoCommand(Main instance, String commandName, DataManager dm) {
        super(instance, commandName, dm);
    }
    public static final String HelpKey = "help", NoPermKey = "noPerm", NoTownFoundKey = "noTownFound", NotInTownKey = "notInKingdom",
            OtherSuccessTitleKey = "otherSuccessTitle", OtherSuccessContentKey = "otherSuccessContent", SelfSuccessTitleKey = "selfSuccessTitle",
            SelfSuccessContentKey = "selfSuccessContent",

    AlliesEntryKey = "alliesEntry", AlliesDividerKey = "alliesDivider", EnemiesEntryKey = "enemiesEntry", EnemiesDividerKey = "enemiesDivider",
            MembersEntryKey = "membersEntry", MembersDividerKey = "membersDivider", AgeYearKey = "ageYear", AgeYearsKey = "ageYears", AgeWeekKey = "ageWeek",
            AgeWeeksKey = "ageWeeksKey", AgeDayKey = "ageDay", AgeDaysKey = "ageDays", AgeHourKey = "ageHour", AgeHoursKey = "ageHours", AgeMinuteKey = "ageMinute",
            AgeMinutesKey = "ageMinutes", AgeSecondKey = "ageSecond", AgeSecondsKey = "ageSeconds",BanTimeBannedKey = "banTimeBanned", BanTimeAvailableKey = "banTimeAvailable",
            BanTimeCurrentTimeKey = "banTimeCurrentTime", BanTimeDividerKey = "banTimeDivider",TownEntryKey = "townEntry", TownDividerKey = "townDivider", TownCapitalKey = "townCapital",
    InWildKey = "inWild";


    @Override
    public void setDefaults() {
        ConfigurationSection cs = this.dm.getConfig();
        if (!cs.isString(HelpKey))
            cs.set(HelpKey, "&6&l> {0}/t info <town> &7- Town info");
        if (!cs.isString(NoPermKey))
            cs.set(NoPermKey, "&cYou do not have permission.");
        if (!cs.isString(NoTownFoundKey))
            cs.set(NoTownFoundKey, "&cNo Town was found with the name &7{0}.");
        if (!cs.isString(NotInTownKey))
            cs.set(NotInTownKey, "&cUnable to display info. You are not in a town.");
        if (!cs.isString(OtherSuccessTitleKey))
            cs.set(OtherSuccessTitleKey, "&4&l==<&6&lTown {0} Info &7({1} Members)&4&l>==");
        if (!cs.isString(OtherSuccessContentKey))
            cs.set(OtherSuccessContentKey,
                    "&6Members: {0}\n" +
                            "&6Age: &7{1}\n" +
                            "&6Open Joining: &7{2}\n" +
                            "&6Land: &7{3}\n" +
                            "&6Allies: &7{4}\n" +
                            "&6BanTime: &7{5}");

        if (!cs.isString(SelfSuccessTitleKey))
            cs.set(SelfSuccessTitleKey, "&4&l==<&6&lTown {0} Info &7({1} Members)&4&l>==");
        if (!cs.isString(SelfSuccessContentKey))
            cs.set(SelfSuccessContentKey,
                    "&6Members: {0}\n" +
                            "&6Age: &7{1}\n" +
                            "&6Open Joining: &7{2}\n" +
                            "&6Heart x:&7{3} &6z:&7{4}\n"+
                            "&6Land: &7{5}\n" +
                            "&6Allies: &7{6}\n" +
                            "&6Treasury: &7${7}\n" +
                            "&6BanTime: &7{8}");
        if (!cs.isString(AlliesEntryKey))
            cs.set(AlliesEntryKey, "&a{0}");
        if (!cs.isString(AlliesDividerKey))
            cs.set(AlliesDividerKey, "&7, ");
        if (!cs.isString(EnemiesEntryKey))
            cs.set(EnemiesEntryKey, "&c{0}");
        if (!cs.isString(EnemiesDividerKey))
            cs.set(EnemiesDividerKey, "&7, ");
        if (!cs.isString(MembersEntryKey))
            cs.set(MembersEntryKey, "&6{0} ");
        if (!cs.isString(MembersDividerKey))
            cs.set(MembersDividerKey, "&7, ");
        if (!cs.isString(AgeYearKey))
            cs.set(AgeYearKey, "&7{0} &6Year ");
        if (!cs.isString(AgeYearsKey))
            cs.set(AgeYearsKey, "&7{0} &6Years ");
        if (!cs.isString(AgeWeekKey))
            cs.set(AgeWeekKey, "&7{0} &6Week ");
        if (!cs.isString(AgeWeeksKey))
            cs.set(AgeWeeksKey, "&7{0} &6Weeks ");
        if (!cs.isString(AgeDayKey))
            cs.set(AgeDayKey, "&7{0} &6Day ");
        if (!cs.isString(AgeDaysKey))
            cs.set(AgeDaysKey, "&7{0} &6Days ");
        if (!cs.isString(AgeHourKey))
            cs.set(AgeHourKey, "&7{0} &6Hour ");
        if (!cs.isString(AgeHoursKey))
            cs.set(AgeHoursKey, "&7{0} &6Hours ");
        if (!cs.isString(AgeMinuteKey))
            cs.set(AgeMinuteKey, "&7{0} &6Minute ");
        if (!cs.isString(AgeMinutesKey))
            cs.set(AgeMinutesKey, "&7{0} &6Minutes ");
        if (!cs.isString(AgeSecondKey))
            cs.set(AgeSecondKey, "&7{0} &6Second ");
        if (!cs.isString(AgeSecondsKey))
            cs.set(AgeSecondsKey, "&7{0} &6Seconds ");
        if (!cs.isString(BanTimeAvailableKey))
            cs.set(BanTimeAvailableKey, "&a{0}");
        if (!cs.isString(BanTimeBannedKey))
            cs.set(BanTimeBannedKey, "&c{0}");
        if (!cs.isString(BanTimeCurrentTimeKey))
            cs.set(BanTimeCurrentTimeKey, "&6*");
        if (!cs.isString(BanTimeDividerKey))
            cs.set(BanTimeDividerKey, " ");
        if (!cs.isString(InWildKey))
            cs.set(InWildKey, "&cCannot get town info in wilderness. ");


    }
    @Override
    public CommandType getType() {
        return CommandType.PLAYER;
    }
    @Override
    public boolean run(String[] args, Player p) {
        if(isArgument(args[0])){
            if(!p.hasPermission("districts.commands.info")){
                p.sendMessage(this.dm.getTCM(NoPermKey));
                return true;
            }
            TUser u = getPlugin().getTUserManager().getOrNewTUser(p);
            Town town;
            if(args.length == 2){
                town = getPlugin().getTownsManager().getTown(args[1]);
                if(town == null){
                    TUser argUser = getPlugin().getTUserManager().tempGetTUser(args[1]);
                    if (argUser != null){
                        Kingdom k = argUser.getKingdom();
                        if (k!= null){
                            town = k.getTowns().get(0);
                        }
                    }
                }
                if (town == null) {
                    p.sendMessage(this.dm.getTCM(NoTownFoundKey, args[1]));
                    return true;
                }
            }else{
                town = getPlugin().getWorldDataManager().getOrNewChunkData(p.getLocation()).getTown();
                if (town == null){
                    p.sendMessage(this.dm.getTCM(InWildKey));
                    return true;
                }
            }
            if (town.getKingdom().equals(u.getKingdom())){
                Location heart = town.getHeartLocation();
                p.sendMessage(this.dm.getTCM(SelfSuccessTitleKey, town.getName(), town.getKingdom().getMembers().size()));
                p.sendMessage(this.dm.getTCM(SelfSuccessContentKey,
                        getMembersDisplay(town.getKingdom()),
                        getAgeDisplay(town),
                        town.getKingdom().isKingdomOpen(),
                        heart.getChunk().getX(), heart.getChunk().getZ(),
                        town.getLandAmount(),
                        getAlliesDisplay(town.getKingdom()),
                        town.getBank(),
                        getBanTimeDisplay(town.getKingdom())));
            }else{
                p.sendMessage(this.dm.getTCM(OtherSuccessTitleKey, town.getName(), town.getKingdom().getMembers().size()));
                p.sendMessage(this.dm.getTCM(OtherSuccessContentKey,
                        getMembersDisplay(town.getKingdom()),
                        getAgeDisplay(town),
                        town.getKingdom().isKingdomOpen(),
                        town.getLandAmount(),
                        getAlliesDisplay(town.getKingdom()),
                        getBanTimeDisplay(town.getKingdom())));
            }
            return true;
        }
        return false;
    }
    @Override
    public HelpMessage[] getHelpMessage() {
        return new HelpMessage[]{new HelpMessage("feudal.commands.user.kingdoms.info",
                "commandHelp.68")};
    }
    public String getMembersDisplay(Kingdom k){
        StringBuilder ret = new StringBuilder();
        for (UUID uuid:k.getMembers().keySet()){
            OfflinePlayer offp = Bukkit.getOfflinePlayer(uuid);
            if (ret.toString().length() != 0){
                ret.append(this.dm.getTCM(MembersDividerKey));
            }
            ret.append(this.dm.getTCM(MembersEntryKey, offp.getName()));

        }
        return ret.toString();
    }
    public String getAlliesDisplay(Kingdom k){
        StringBuilder ret = new StringBuilder();
        for (UUID uuid:k.getAlliesList()){
            Kingdom ally = getPlugin().getKingdomsManager().getKingdom(uuid);
            if (ally == null){
                continue;
            }
            for(Town t: ally.getTowns()){
                if (ret.toString().length() != 0){
                    ret.append(this.dm.getTCM(AlliesDividerKey));
                }
                ret.append(this.dm.getTCM(AlliesEntryKey, t.getName()));
            }


        }
        return ret.toString();
    }
    public String getAgeDisplay(Town t){
        StringBuilder sb = new StringBuilder();
        long rem = t.getAge();
        long years = rem / 31557600000L;
        long weeks = (rem - (years * 31557600000L)) / 604800000L;
        long days = (rem-(years * 31557600000L)-(weeks * 604800000L)) / 86400000;
        long hours = ((rem - (days * 86400000) - (years * 31557600000L)-(weeks * 604800000L)) / 3600000);
        long minutes = (rem - (days * 86400000) - (hours * 3600000)-(years * 31557600000L)-(weeks * 604800000L)) / 60000;
        long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)-(years * 31557600000L)-(weeks * 604800000L)) / 1000;
        if(years != 0){
            sb.append(years == 1?this.dm.getTCM(AgeYearKey, years):this.dm.getTCM(AgeYearsKey, years));
        }
        if(weeks != 0){
            sb.append(weeks == 1?this.dm.getTCM(AgeWeekKey, weeks):this.dm.getTCM(AgeWeeksKey, weeks));
        }
        if(hours != 0){
            sb.append(hours == 1?this.dm.getTCM(AgeHourKey, hours):this.dm.getTCM(AgeHoursKey, hours));
        }
        if(minutes != 0){
            sb.append(minutes == 1?this.dm.getTCM(AgeMinuteKey, minutes):this.dm.getTCM(AgeMinutesKey, minutes));
        }
        if(seconds != 0){
            sb.append(seconds == 1?this.dm.getTCM(AgeSecondKey, seconds):this.dm.getTCM(AgeSecondsKey, seconds));
        }

        return sb.toString();
    }
    public String getLastOnlineDisplay(Town t){
        StringBuilder sb = new StringBuilder();
        long rem = t.getLastOnline();
        long years = rem / 31557600000L;
        long weeks = (rem - (years * 31557600000L)) / 604800000L;
        long days = (rem-(years * 31557600000L)-(weeks * 604800000L)) / 86400000;
        long hours = ((rem - (days * 86400000) - (years * 31557600000L)-(weeks * 604800000L)) / 3600000);
        long minutes = (rem - (days * 86400000) - (hours * 3600000)-(years * 31557600000L)-(weeks * 604800000L)) / 60000;
        long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)-(years * 31557600000L)-(weeks * 604800000L)) / 1000;
        if(years != 0){
            sb.append(years == 1?this.dm.getTCM(AgeYearKey, years):this.dm.getTCM(AgeYearsKey, years));
        }
        if(weeks != 0){
            sb.append(weeks == 1?this.dm.getTCM(AgeWeekKey, weeks):this.dm.getTCM(AgeWeeksKey, weeks));
        }
        if(hours != 0){
            sb.append(hours == 1?this.dm.getTCM(AgeHourKey, hours):this.dm.getTCM(AgeHoursKey, hours));
        }
        if(minutes != 0){
            sb.append(minutes == 1?this.dm.getTCM(AgeMinuteKey, minutes):this.dm.getTCM(AgeMinutesKey, minutes));
        }
        if(seconds != 0){
            sb.append(seconds == 1?this.dm.getTCM(AgeSecondKey, seconds):this.dm.getTCM(AgeSecondsKey, seconds));
        }

        return sb.toString();
    }
    public String getBanTimeDisplay(Kingdom k){
        StringBuilder ret = new StringBuilder();
        boolean[] banTime = k.getBanTime();
        Date date = new Date();
        @SuppressWarnings("deprecated")
        int hours = date.getHours();

        for(int i = 0; i < banTime.length; i++){
            if (ret.toString().length() != 0){
                ret.append(this.dm.getTCM(BanTimeDividerKey));
            }
            if (hours == i+1){
                ret.append(this.dm.getTCM(BanTimeCurrentTimeKey));
            }
            if (banTime[i]){
                ret.append(this.dm.getTCM(BanTimeBannedKey, i+1));
            }else {
                ret.append(this.dm.getTCM(BanTimeAvailableKey, i+1));
            }
        }
        return ret.toString();
    }


}
