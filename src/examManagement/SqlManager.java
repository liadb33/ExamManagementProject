package examManagement;

import java.sql.*;
import org.postgresql.util.*;

public class SqlManager {

	private final static String DB_URL = "jdbc:postgresql://localhost:5432/schooldb";
	public final static int DUPLICATE_FAULT = -1;
	public final static int SQL_FAULT = -2;
	public final static int UNKNOWN_FAULT = -3;
	private static SqlManager instance = null;
	private Connection conn;

	private SqlManager() {
		if (!connectDB())
			System.out.println("Connection failed");
	}

	public static synchronized SqlManager getInstance() {
		if (instance == null)
			instance = new SqlManager();

		return instance;
	}

	public boolean connectDB() {
		try {
			Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(DB_URL, "postgres", "1234");
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public int updateQuery(String query) {

		int result = -1;
		try {
			Statement stmt = this.conn.createStatement();
			result = stmt.executeUpdate(query);
		} catch (PSQLException p) {
			return DUPLICATE_FAULT;
		} catch (SQLException e) {
			return SQL_FAULT;
		} catch (Exception e) {
			return UNKNOWN_FAULT;
		}

		return result;
	}

	public ResultSet selectQuery(String query) {
		ResultSet rs = null;
		try {
			Statement stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query);
		} catch (Exception e) {
			rs = null;
		}

		return rs;
	}

	public Connection getConn() {
		return conn;
	}
	
	

}
