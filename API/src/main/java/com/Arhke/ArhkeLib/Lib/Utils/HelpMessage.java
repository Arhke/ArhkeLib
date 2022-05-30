package com.Arhke.ArhkeLib.Lib.Utils;

import com.Arhke.ArhkeLib.Lib.Base.Base;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;

/**
 * Help Message with perm and page system
 */
public class HelpMessage extends Base {
    final String title;
    final List<HelpPerm> helpPermList = new ArrayList<>();
    int linePerPage = 8;

    public HelpMessage() {
        this("&6====<Page &7{0} &6out of &7{1}&6>====");
    }

    public HelpMessage(String title) {
        this.title = title;
    }

    public void setLinePerPage(int line) {
        this.linePerPage = line;
    }

    /**
     * @param requester <b>Player/Console</b> requesting the help msg <br> <b>null</b> if none specified
     * @param page page number for help
     * @return Help Message String
     */
    public String getMessage(Permissible requester, int page) {
        int maxPage = helpPermList.size() / linePerPage;
        StringBuilder sb = new StringBuilder();
        sb.append(tcm(title, page, maxPage));
        for (int i = page*linePerPage; i < page*linePerPage+8; i++){
            try {
                HelpPerm hp =  helpPermList.get(i);
                if (requester == null || requester.hasPermission(hp.getPerm())) {
                    sb.append(hp.getMsg());
                }
            }catch(ArrayIndexOutOfBoundsException e){
                break;
            }

        }

        return sb.toString();
    }

    public void addMessage(String helpMsg, String perm) {
        helpPermList.add(new HelpPerm(helpMsg, perm));
    }

    private static class HelpPerm {
        private String helpMsg;
        private String perm;
        public HelpPerm(String helpMsg, String perm) {
            this.helpMsg = helpMsg;
            this.perm = perm;
        }
        public String getMsg(){
            return this.helpMsg;
        }
        public String getPerm(){
            return this.perm;
        }
    }
}
