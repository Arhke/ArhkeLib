package com.Arhke.WRCore.District.Commands;

import java.util.ArrayList;
import java.util.List;

import com.Arhke.WRCore.Lib.Base.MainBase;
import com.Arhke.WRCore.Lib.FileIO.DataManager;
import com.Arhke.WRCore.Lib.Utils.HelpMessage;
import com.Arhke.WRCore.Main;
import org.bukkit.entity.Player;

import com.Arhke.WRCore.District.core.Feudal;

/**
 * 
 * @author Michael Forseth
 *
 */
public class CommandHelp extends MainBase {
	private ArrayList<HelpMessage> helpMessages = new ArrayList<>();

	public CommandHelp(Main instance){
		super(instance);
	}

	public ArrayList<HelpMessage> getHelpMessages(){
		return helpMessages;
	}
	
	public void help(int page, Player p) {
		try{
			List<String> sendableMessages = getSendable(p);
			
			int maxPage = sendableMessages.size() / 6;
			if((((double)sendableMessages.size()) / 6.0) > maxPage){
				maxPage++;
			}
			if(page < 1){
				page = 1;
			}
			if(page > maxPage){
				page = maxPage;
			}
			p.sendMessage("&9&l===< &6&lFeudal {0} / {1} &9&l>===".replace("{1}", maxPage + "").replace("{0}", page + ""));
			
			for(int i = (page-1)*6; i < (page*6) && i < sendableMessages.size(); i++){
				if(i < sendableMessages.size())
					p.sendMessage(sendableMessages.get(i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private List<String> getSendable(Player p) {
		List<String> str = new ArrayList<String>();
		for(HelpMessage msg : helpMessages){
			if(msg.hasPermission(p)){
				str.add(msg.getMessage());
			}
		}
		return str;
	}
}
