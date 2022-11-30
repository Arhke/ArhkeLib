package com.Arhke.WRCore.District.Commands.TownCommand.other;

import com.Arhke.WRCore.District.Commands.Arg;
import com.Arhke.WRCore.District.Commands.CommandHelp;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * 
 * @author Michael Forseth
 *
 */
public class HelpCommand extends SubCommandsBase {
	public HelpCommand(Main instance, String commandName, DataManager dm) {
		super(instance, commandName, dm);
	}

	public static final String NoPermKey = "noPerm", MustIntegerKey = "mustInteger", HelpKey = "help";


	@Override
	public void setDefaults(){
		ConfigurationSection cs = this.dm.getConfig();
		if(cs.isString(NoPermKey))
			cs.set(NoPermKey, "&cYou do not have permission.");
		if(cs.isString(MustIntegerKey))
			cs.set(MustIntegerKey, "&cHelp page must be an integer.");
		if(cs.isString(HelpKey))
			cs.set(HelpKey, "&cDo /t help for help with commands.");
	}
	@Override
	public CommandType getType() {
		return CommandType.PLAYER;
	}

	@Override
	public boolean run(String[] args, Player p) {
		if(isArgument(args[0])){
			if(!p.hasPermission("feudal.commands.user.general.help")){
				p.sendMessage(NoPermKey);
				return true;
			}
			if(args.length == 2){
				try{
//					CommandHelp.help(Integer.parseInt(args[1]), p);
				}catch(Exception e){
					p.sendMessage(this.dm.getTCM(MustIntegerKey));
				}
			}else{
//				CommandHelp.help(1, p);
			}
			return true;
		}
		return false;
	}



	@Override
	public HelpMessage[] getHelpMessage() {
		return new HelpMessage[]{new HelpMessage("feudal.commands.user.general.help", 
				"commandHelp.18")};
	}

}
