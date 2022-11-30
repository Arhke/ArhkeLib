package com.Arhke.WRCore.District.Commands.TownCommand.admin;

import com.Arhke.WRCore.District.Commands.Arg;
import com.Arhke.WRCore.District.TUsers.TUser;
import com.Arhke.WRCore.District.kingdoms.Kingdom;
import com.Arhke.WRCore.District.kingdoms.KingdomLog;
import com.Arhke.WRCore.District.kingdoms.Town;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * @author Michael Forseth
 *
 */
public class KingdomLogCommand extends SubCommandsBase {
	public KingdomLogCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}
	public static final String NoPermKey = "noPerm";



	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();
		if(cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("feudal.commands.admin.kingdomlog")){
				p.sendMessage(this.dm.getTCM(NoPermKey));
				return true;
			}
			
			if(args.length == 2 || args.length == 3){
				OfflinePlayer offp = Bukkit.getOfflinePlayer(args[1]);
				TUser u = getPlugin().getTUserManager().tempGetTUser(offp.getUniqueId());
				if(u != null){
					ArrayList<KingdomLog> logs = new ArrayList<KingdomLog>();
					ArrayList<KingdomLog> logsD = u.getPastKingdoms();
					long lastTime = Long.MAX_VALUE;

					if(logsD != null && logsD.size() > 0){

						while(logs.size() < logsD.size()){
							KingdomLog l = null;
							for(KingdomLog log : logsD){
								if(log.getTime() <= lastTime && !logs.contains(log)){
									l = log;
								}
							}
							if(l != null){
								lastTime = l.getTime();
								logs.add(l);
							}else{
								break;
							}
						}
						int page = 1;
						if(args.length == 3){
							try{
								page = Integer.parseInt(args[2]);
							}catch(Exception e){
								p.sendMessage(this.dm.getTCM("kingdomlog.noInt"));
								return true;
							}
						}
						if(page < 1){
							page = 1;
						}
						int maxPage = (logs.size() / 7) + 1;
						if(page > maxPage){
							page = maxPage;
						}


						p.sendMessage(this.dm.getTCM("kingdomlog.title").replace("%name%", u.getName()) + " " + page + "/" + maxPage);// &6&lKingdom logs (%name%)
						for(int i = ((page - 1)*7); i < ((page) * 7); i++){
							if(i < logs.size()){
								KingdomLog log = logs.get(i);
								String kingdom = log.getKingdomUUID();
								Kingdom k = getPlugin().getKingdomsManager().getKingdom(UUID.fromString(kingdom));
								if(k != null){
									kingdom = k.getName();
								}else{
									kingdom = "UNKNOWN / DELETED (" + kingdom + ")";
								}

								String time = log.getTime() + "";
								long rem = System.currentTimeMillis() - log.getTime();
								long years = rem / 31557600000L;
								long weeks = (rem - (years * 31557600000L)) / 604800000L;
								long days = (rem-(years * 31557600000L)-(weeks * 604800000L)) / 86400000;
								long hours = ((rem - (days * 86400000) - (years * 31557600000L)-(weeks * 604800000L)) / 3600000);
								long minutes = (rem - (days * 86400000) - (hours * 3600000)-(years * 31557600000L)-(weeks * 604800000L)) / 60000;
								long seconds = (rem - (days * 86400000) - (hours * 3600000) - (minutes * 60000)-(years * 31557600000L)-(weeks * 604800000L)) / 1000;
								if(years == 0){
									if(weeks == 0){
										time = days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
									}else{
										time =  weeks + (weeks == 1 ? " week" : " weeks") + ", " + days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
									}
								}else{
									time = years + (years == 1 ? " year" : " years") + ", " + weeks + (weeks == 1 ? " week" : " weeks") + ", " + days + (days == 1 ? " day" : " days") + " " + hours + ":" + minutes + ":" + seconds + " ago";
								}

								p.sendMessage(this.dm.getTCM("kingdomlog.log").replace("%time%", time).replace("%kingdom%", kingdom).replace("%join%", log.isJoin() ? "JOINED" : "LEFT"));// &d%join% %kingdom% %time%
							}
						}
					}else{
						p.sendMessage(this.dm.getTCM("kingdomlog.noLog"));// This player has no kingdom log
					}
				}else{
					p.sendMessage(this.dm.getTCM("commands.1219"));
				}
			}else{
				p.sendMessage(this.dm.getTCM("commands.2930"));
			}
			return true;
		}
		return false;
	}

	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.admin.kingdomlog", 
				"kingdomlog.cmd")};
	}

}
