package com.Arhke.ArhkeLib.Lib.sql;

/**
 * A SQL table class. Improvements made by Richard Bimmer.
 * 
 * @author Richard Bimmer
 * @version 5/24/17
 */
public class Table {
	private final String name;
	private final String usage;

	public Table(String name, String usage) {
		this.name = name;
		this.usage = usage;
	}

	public String getName() {
		return this.name;
	}

	public String getUsage() {
		return " (" + this.usage + ")";
	}

	public String getValues() {
		StringBuilder v = new StringBuilder();
		String[] usageArray = this.usage.split(",");
		int i = 0;
		for (String column : usageArray) {
			i++;
			
			// exclude primary key information from columns
			if (!column.toUpperCase().startsWith("PRIMARY KEY")) {
				String[] c = column.split(" ");
				v.append(c[0] == null ? "" : c[0] + (i <= usageArray.length - 1 ? "," : ""));
			}	
		}

		return "(" + v + ")";
	}
	
	public String getValuesNonPrimary() {
		String v = "";
		String[] usageArray = this.usage.split(",");
		int i = 0;
		for (String column : usageArray) {
			i++;
			if(!column.toUpperCase().contains("PRIMARY KEY")) {
				
				// exclude primary key information from columns
				if (!column.toUpperCase().startsWith("PRIMARY KEY")) {
					String[] c = column.split(" ");
					v = v + (c[0] == null ? "" : new StringBuilder().append(c[0]).append(i <= usageArray.length - 1 ? "," : "").toString());
				}	
			}
		}

		return "(" + v + ")";
	}
}
