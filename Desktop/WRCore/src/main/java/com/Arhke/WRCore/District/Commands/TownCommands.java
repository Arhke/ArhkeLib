package com.Arhke.WRCore.District.Commands;

import com.Arhke.WRCore.District.Commands.TownCommand.*;
import com.Arhke.WRCore.District.util.Utils;
import com.Arhke.WRCore.Lib.Base.CommandsBase;
import com.Arhke.WRCore.Lib.Base.SubCommandsBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author William Lin
 *
 */
public class TownCommands extends CommandsBase {

	public TownCommands(Main instance, String command, DataManager dm) {
		super(instance, command, dm,
				new AllyCommand(instance, "ally", dm),
				new BanTimeCommand(instance, "bantime", dm),
				new ChunkCommand(instance, "chunk", dm),
				new ClaimCommand(instance, "claim", dm),
				new DemoteCommand(instance, "demote", dm),
//				new DescriptionCommand(instance, "desc", dm),
				new HomeCommand(instance, "home", dm),
				new KickMemCommand(instance, "kick", dm),
				new KingdomChatCommand(instance, "chat", dm),
				new LeaderCommand(instance, "leader", dm),
				new NeutralCommand(instance, "neutral", dm),
				new PlayerInviteCommand(instance, "invite", dm),
				new PlayerJoinCommand(instance, "join", dm),
				new PromoteCommand(instance, "promote", dm),
				new SetOpenCommand(instance, "setopen", dm),
				new SpawnCommand(instance, "spawn", dm),
				new TownBankCommand(instance, "bank",dm),
				new TownCreateCommand(instance, "create", dm),
				new DepositCommand(instance, "deposit", dm),
				new TownDisbandCommand(instance, "disband", dm),
				new TownInfoCommand(instance, "info", dm),
				new TownLeaderCommand(instance, "leader", dm),
				new TownLeaveCommand(instance, "leave",dm),
				new TownListCommand(instance, "list",dm),
				new UnclaimCommand(instance, "unclaim", dm),
				new WithdrawCommand(instance , "withdraw", dm)
		);

	}


	@Override
	public boolean onCommand(CommandSender sender,
							 org.bukkit.command.Command cmd, String label, String[] args) {
		process(sender, args);
		return true;
	}
	public static final String UnknownCommandKey = "unknownCommand", InvalidConsoleCommandKey = "invalidConsoleCommand";
	@Override
	public void setDefaults() {
		ConfigurationSection cs = this.dm.getConfig();
		if(!cs.isString(UnknownCommandKey))
			cs.set(UnknownCommandKey, "&cUnknown command. Try &7/t help &cfor help.");
		if(!cs.isString(InvalidConsoleCommandKey))
			cs.set(InvalidConsoleCommandKey, "Invalid command. Console commands: /t changeReputation <player> <change>, /f addXP/addLevel <player> <profession/strength/toughness/speed/stamina/luck/all/attributes> <int> [true/false], /f reload");
		for(SubCommandsBase command:getSubCommands()){
			command.setDefaults();
		}

	}
	public void process(CommandSender sender, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(args.length == 0){
//				CommandHelp.help(1, p);
				p.sendMessage(" /f for general info\n" +
						"/f ally <name>\n" +
						"/f bantime <your current time 24 hrs>\n" +
						"/f chunk - displays chunk info\n" +
						"/f chunk <amount> - spends money to add <amount> of hp onto the chunk\n" +
						"/f chunk all - spends all of your money to add as much hp as possible on to the chunk\n" +
						"/f claim - claims the chunk you are standing on right now. Fails if you are not standing in a chunk adjacent to a claim\n" +
						"/f create <name> - to create your own faction\n" +
						"/f demote <player> - demote a player\n" +
						"/f deposit - deposit money to the town that you are standing on. Fails if your faction doesn’t own the town, or if you aren’t standing on the heart plot \n" +
						"/f home - teleport to the spawn of the first town you have created\n" +
						"/f home <name> - teleport to the specified town that you own.\n" +
						"/f kick <player> - kicks a member from your town\n" +
						"/f chat - switches to faction chat\n" +
						"/f leader <player> - gives leadership of the faction to the specified player. Fails if you are not leader of the faction\n" +
						"/f neutral <name> - remove an allied faction\n" +
						"/f invite <player> - invites a player to join your faction\n" +
						"/f join <name> - Joins the specified faction if you were invited or if they are open.\n" +
						"/f disband - disbands the current faction you own\n" +
						"/f info - displays info of the faction you are currently standing on right now\n" +
						"/f leave - leaves your current faction. Fails if you are the leader.\n" +
						"/f list - lists all of the factions\n" +
						"/f unclaim - unclaimed the chunk of land you are standing on\n" +
						"/f withdraw - withdraws money from the bank of the town you are standing on. Fails if your factions doesn’t own the town, or if you are not standing on the heart chunk.\n");
				return;
			}else {
				for (SubCommandsBase command : getSubCommands()) {
					if (command.getType().equals(SubCommandsBase.CommandType.PLAYER) && command.run(args, p)) {
						return;
					}
				}
				p.sendMessage(this.dm.getTCM(UnknownCommandKey));
			}
		}
		else{
			if(args.length == 0){
				Bukkit.getConsoleSender().sendMessage(this.dm.getTCM(InvalidConsoleCommandKey));
			}else{
				for(SubCommandsBase command : getSubCommands()) {
					if (command.getType().equals(SubCommandsBase.CommandType.SERVER) && command.run(args, null)) {
						return;
					}
				}
				Bukkit.getConsoleSender().sendMessage(this.dm.getTCM("commands.3264"));
			}
		}
	}

	public boolean preprocess(String message, Player player) {
		if(Utils.isFeudalCommand(message)) {
			String[] args = message.split(" ");
			String[] args2 = new String[args.length-1];
			for(int i = 1; i < args.length; i++){
				args2[i-1] = args[i];
			}
			process(player, args2);
			return true;
		}
		return false;
	}



}
