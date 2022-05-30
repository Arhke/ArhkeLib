package com.Arhke.ArhkeLib.Lib.Utils;

import java.util.Scanner;

public class PluginYamlBuilder {
    public static void build(Class<?> clazz){

        StringBuilder sb = new StringBuilder();
        Scanner scan = new Scanner(System.in);
        System.out.print("Plugin Name: ");
        sb.append("name: ").append(scan.nextLine()).append('\n');
        sb.append("author: Arhke").append('\n');
        System.out.print("Plugin Version: ");
        sb.append("version: ").append(scan.nextLine()).append('\n');
        sb.append("main: ").append(clazz.getName()).append('\n');
        System.out.print("Description: ");
        sb.append("description: ").append(scan.nextLine()).append('\n');
        System.out.print("api-version: ");
        sb.append("api-version: ").append(scan.nextLine()).append('\n');
        sb.append("softdepend: [");

        boolean appendComma = false;
        while (true) {
            System.out.print("Soft Dependencies: ");
            String s = scan.nextLine();
            if (s.length() == 0) break;
            else {
                if(appendComma){
                    sb.append(", ");
                }
                sb.append(s);
                appendComma = true;
            }


        }

        sb.append(']').append('\n');
        sb.append("commands:\npermissions:");
        System.out.println("==========================================\n"+sb.toString());


    }
    public static void buildCommand(){
        StringBuilder sb = new StringBuilder();
        Scanner scan = new Scanner(System.in);
        sb.append("commands:\n");
        System.out.print("Command Name: ");
        String command = scan.nextLine();
        sb.append("  ").append(command).append(":\n");
        sb.append("    usage: /").append(command).append(" []\n");
        System.out.print("Command Description: ");
        sb.append("    description: ").append(scan.nextLine()).append('\n');
        sb.append("    aliases: [");

        boolean appendComma = false;
        while (true) {
            System.out.print("Command Aliases: ");
            String s = scan.nextLine();
            if (s.length() == 0) break;
            else {
                if(appendComma){
                    sb.append(", ");
                }
                sb.append(s);
                appendComma = true;
            }


        }
        sb.append(']');
        System.out.println("==========================================\n"+sb.toString());
    }
}
/*        softdepend: [Vault, WorldEdit, PlaceholderAPI, MoltresCore]
        commands:
        feudal:commands:
  not-too-expensive:
    description: Not too expensive command
    usage: /not-too-expensive [reload/exempt]
    aliases: [nte]
        description: Primary feudal command
        aliases: [f, k, kingdom]
        permissions:
        feudal.*:
        description: Gives access to all feudal features.
default: op
        children:
        feudal.admin: true
        feudal.admin:
        description: Gives access to all admin features.*/
