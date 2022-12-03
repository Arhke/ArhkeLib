package com.Arhke.ArhkeLib.Lib.sql;

//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import com.Arhke.ArhkeLib.Lib.sql.async.SQLAsync;
//import com.Arhke.ArhkeLib.Lib.sql.async.SQLWait;


import java.sql.SQLException;

public interface FOFF {
	void setTable();

	void set(Object... paramVarArgs);

	Object get(String selectColumn, String columnReturn,
			Object selectValue) throws Exception;

	boolean contains(String index, Object value) throws SQLException;

	void update(String selectColumn, String changeColumn,
			Object whereIndex, Object changeValue);

//	void remove(String paramString, Object paramObject);
//
//	boolean isOpen();
//
//	void open();
//
//	void close();
//
//	boolean isConnected();
//
//	ResultSet executeQuery(String statement, boolean next);
//
//	boolean executeUpdate(String statement);
//
//	boolean columnExists(String string);
//
//	Table getTable();
//
//	boolean containsCaseInsensitiveString(String index, String value) throws SQLException;
//
//	/*
//	 * Async methods
//	 */
//	SQLWait setAsync(Object... values) throws SQLException;
//
//	SQLWait getAsync(String selectColumn, String columnReturn,
//			Object selectValue) throws Exception;
//
//	SQLWait containsAsync(String index, Object value) throws SQLException;
//
//	SQLWait updateAsync(String selectColumn, String changeColumn,
//			Object whereIndex, Object changeValue) throws SQLException;
//
//	SQLWait removeAsync(String index, Object value) throws SQLException;
//
//	SQLWait executeQueryAsync(String statement, boolean next);
//
//	SQLWait executeUpdateAsync(String statement);
//
//	/*
//	 * Embedded Async methods
//	 */
//	SQLWait setAsync(SQLAsync.SQLEventHandler handler, Object... values) throws SQLException;
//
//	SQLWait getAsync(String selectColumn, String columnReturn,
//			Object selectValue, SQLAsync.SQLEventHandler handler) throws Exception;
//
//	SQLWait containsAsync(String index, Object value, SQLAsync.SQLEventHandler handler) throws SQLException;
//
//	SQLWait updateAsync(String selectColumn, String changeColumn,
//			Object whereIndex, Object changeValue, SQLAsync.SQLEventHandler handler) throws SQLException;
//
//	SQLWait removeAsync(String index, Object value, SQLAsync.SQLEventHandler handler) throws SQLException;
//
//	SQLWait executeQueryAsync(String statement, boolean next, SQLAsync.SQLEventHandler handler);
//
//	SQLWait executeUpdateAsync(String statement, SQLAsync.SQLEventHandler handler);
}