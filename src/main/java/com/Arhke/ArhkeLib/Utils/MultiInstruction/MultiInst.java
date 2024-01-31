package com.Arhke.ArhkeLib.Utils.MultiInstruction;

import com.Arhke.ArhkeLib.Base.Base;
import org.bukkit.OfflinePlayer;

public interface MultiInst {
    void apply(OfflinePlayer p);

    /**
     * @param s <p>String to be parsed.</p>
     * @return <b>null</b> - <p>If the string cannot be fit into a specific MultiInst type</p> <br>
     *     <b>MultiInst</b> - <p>The MultiInstruction object containing what to do (run apply() to execute the instruction)</p>
     *
     */
    static MultiInst parse(String s) {
        int colonIndex = s.indexOf(":");
        try {
            String lowerParse = s.substring(0, colonIndex);
            String content = s.substring(colonIndex + 1);
            if (lowerParse.equalsIgnoreCase("msg") || lowerParse.equalsIgnoreCase("message") || lowerParse.equalsIgnoreCase("tell")
                    || lowerParse.equalsIgnoreCase("m") || lowerParse.equalsIgnoreCase("t")) {
                return new PlayerMsg(content);
            } else if (lowerParse.equalsIgnoreCase("console") || lowerParse.equalsIgnoreCase("command") || lowerParse.equalsIgnoreCase("cmd") || lowerParse.equalsIgnoreCase("c")) {
                return new ConsoleCmd(content);
            } else if (lowerParse.equalsIgnoreCase("player") || lowerParse.equalsIgnoreCase("p")) {
                return new PlayerCmd(content);
            } else if (lowerParse.equalsIgnoreCase("o") || lowerParse.equalsIgnoreCase("op")) {
                return new OPCmd(content);
            }else if (lowerParse.equalsIgnoreCase("perm") || lowerParse.equalsIgnoreCase("permission")) {
                return new PlayerPerm(content);
            } else {
                Base.err("[MultiInst.java] Couldn't parse " + s + "\ninto an instruction.");
                return null;
            }
        }catch(StringIndexOutOfBoundsException e){
            Base.err("[MultiInst.java] Couldn't parse " + s + "\ninto an instruction.");
            return null;
        }
    }
}
