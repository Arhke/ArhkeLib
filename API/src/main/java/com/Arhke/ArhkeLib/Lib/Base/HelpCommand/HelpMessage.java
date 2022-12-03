package com.Arhke.ArhkeLib.Lib.Base.HelpCommand;

import com.Arhke.ArhkeLib.Lib.Base.Base;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;


public class HelpMessage {
    String title;
    final List<HelpPerm> helpPermList = new ArrayList<>();
    int linePerPage = 8;

    public HelpMessage(String title) {
        this.title = title;
    }

    public void setLinePerPage(int line) {
        this.linePerPage = line;
    }

    /**
     * @param requester <b>Player/Console</b> requesting the help msg <br> <b>null</b> if want all
     * @param page page number for help
     * @return Help Message String
     */
    public String getMessage(Permissible requester, int page) {
        int maxPage = helpPermList.size() / linePerPage;
        StringBuilder sb = new StringBuilder();
        sb.append(Base.tcm(title, page, maxPage)).append('\n');
        for (int i = page*linePerPage; i < page*linePerPage+8 && i < helpPermList.size(); i++){
            HelpPerm hp =  helpPermList.get(i);
            if (requester == null || hp.hasPermission(requester))
                sb.append(hp.getMsg()).append('\n');
        }
        return sb.toString();
    }

    public void addMessage(String helpMsg, String... perm) {
        helpPermList.add(new HelpPerm(helpMsg, perm));
    }

}