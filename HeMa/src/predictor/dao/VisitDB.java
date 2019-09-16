package predictor.dao;

import java.sql.*;

public class VisitDB {
	private Connection conn = null;
	private Statement stmt = null;
	public PreparedStatement pstmt = null;

	public static final String driverName = "com.mysql.jdbc.Driver";
	public static final String urls = "jdbc:mysql://10.108.17.44:3306/code2vec?useSSL=true&verifyServerCertificate=false";

	public VisitDB() {
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(urls, "root", "123456");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ResultSet executeQuery(String sql) throws Exception {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			throw e;
		}
		return rs;
	}


	public boolean execute(String sql){
		pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			return pstmt.execute();
		} catch (SQLException ex) {
			System.out.println("sql:" + sql);
			ex.printStackTrace();
		}
		return false;
	}

	public void close_all() throws Exception {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				throw new Exception("close stmt Exception");
			}
		}

		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				throw new Exception("close pstmt Exception");
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new Exception("close stmt Exception");
			}
		}
	}
}