//package com.Arhke.ArhkeLib.sql;
//
//import java.io.File;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import org.bukkit.Bukkit;
//
//
//public class MySQL  extends SQLite implements SQLManager {
//
//	public MySQL() {
//		open();
//	}
//
//	public synchronized void open() {
//        con = java.sql.DriverManager.getConnection("jdbc:sqlite:"
//                + this.fileDir + File.separator + this.fileName);
//        this.st = con.createStatement();
//
//        this.st.setQueryTimeout(30);
//        Connection conn = null;
//        Properties connectionProps = new Properties();
//        connectionProps.put("user", this.userName);
//        connectionProps.put("password", this.password);
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			Bukkit.getLogger().severe(
//					"[Feudal] Failed to init MySQL driver:");
//			StackUtil.dumpStack(e);
//			return;
//		}
//
//        String host = "na03-sql.pebblehost.com";
//		String port = "3306";
//		String database = "customer_301635_test";
//		String user = "customer_301635_test";
//		String pass = "L$EeBIV3Gn2Pv$TOfeE!";
//		try {
//			this.setConnection(java.sql.DriverManager.getConnection("jdbc:mysql://"
//					+ host + ':' + port + '/' + database + '?' + "user=" + user
//					+ "&password=" + pass));
//
//			this.setStatement(this.getConnection().createStatement());
//
//			this.getStatement().setQueryTimeout(30);
//		} catch (SQLException e) {
//			Bukkit.getLogger().severe(
//					"[Feudal] Failed to init MySQL connection:");
//			StackUtil.dumpStack(e);
//		}
//	}
//
//}