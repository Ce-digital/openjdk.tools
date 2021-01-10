package openjdk.tools.json.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import openjdk.tools.json.JsonList;
import openjdk.tools.json.exceptions.JsonException;
import openjdk.tools.json.util.converters.JsonSqlConverter;

public class JsonJdbcClient {
	
	
	/**
	 * Run an SQLConverter query and get the result as a JsonList.
	 * 
	 * @param connection
	 * @param sql
	 * @return Returns a JsonList of JsonMaps as rows having the column name as JSON key and the cell value as JSON value
	 * @throws JsonException
	 * @throws SQLException
	 */
	public static JsonList query(Connection connection, String sql) throws JsonException, SQLException {
		return JsonSqlConverter.convert(connection.createStatement().executeQuery(sql));
	}
	
	/**
	 * Run an SQLConverter query and get the result as a JsonList.
	 * 
	 * @param jdbc_driver
	 * @param url
	 * @param user
	 * @param password
	 * @param sql
	 * @return Returns a JsonList of JsonMaps as rows having the column name as JSON key and the cell value as JSON value
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static JsonList query(String jdbc_driver, String url, String user, String password, String sql) throws ClassNotFoundException, SQLException {
		return query(getJDBCConnection(jdbc_driver, url, user, password), sql);
	}
	
	/**
	 * Run an SQLConverter query and get the result as a JsonList.
	 * 
	 * @param jdbc_driver
	 * @param url
	 * @param user
	 * @param password
	 * @param sql
	 * @return Returns a JsonList of JsonMaps as rows having the column name as JSON key and the cell value as JSON value
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static JsonList query(Class<?> jdbc_driver, String url, String user, String password, String sql) throws ClassNotFoundException, SQLException {
		return query(getJDBCConnection(jdbc_driver.getName(), url, user, password), sql);
	}
	
	/**
	 * Run an SQLConverter statement and get success state as boolean.
	 * 
	 * @param connection
	 * @param sql
	 * @return Returns 'true' if execution was successful or 'false' if unsuccessful
	 * @throws SQLException
	 */
	public static boolean execute(Connection connection, String sql) throws SQLException {
		return connection.createStatement().execute(sql);
	}
	
	/**
	 * Run an SQLConverter statement and get success state as boolean.
	 * 
	 * @param jdbc_driver
	 * @param url
	 * @param user
	 * @param password
	 * @param sql
	 * @return Returns 'true' if execution was successful or 'false' if unsuccessful
	 * @throws SQLException
	 */
	public static boolean execute(String jdbc_driver, String url, String user, String password, String sql) throws ClassNotFoundException, SQLException {
		return execute(getJDBCConnection(jdbc_driver, url, user, password), sql);
	}
	
	/**
	 * Run an SQLConverter statement and get success state as boolean.
	 * 
	 * @param jdbc_driver
	 * @param url
	 * @param user
	 * @param password
	 * @param sql
	 * @return Returns 'true' if execution was successful or 'false' if unsuccessful
	 * @throws SQLException
	 */
	public static boolean execute(Class<?> jdbc_driver, String url, String user, String password, String sql) throws ClassNotFoundException, SQLException {
		return execute(getJDBCConnection(jdbc_driver.getName(), url, user, password), sql);
	}
	
	/**
	 * Create a JDBC connection from a given JDBC driver class-name, a connection-string URL, a user-name and a password. 
	 * 
	 * @param jdbc_driver
	 * @param url
	 * @param user
	 * @param password
	 * @return Returns the JDBC connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getJDBCConnection(String jdbc_driver, String url, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName(jdbc_driver);
		return DriverManager.getConnection(url, user, password);
	}

}
