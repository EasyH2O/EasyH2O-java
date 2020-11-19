package nl.wouterdebruijn.EasyH2O;

import java.sql.*;

public class MySQLConnector
	{
		Connection con;
		
		public void connect() throws SQLException
			{
				this.con = DriverManager.getConnection ("jdbc:mysql://localhost/easyh20");
			}
			
		public void disconnect() throws SQLException
			{
				this.con.close ();
			}
			
		public ResultSet query() throws SQLException
			{
				Statement stmt = this.con.createStatement();
				String query = "select * from users;";
				ResultSet rs = stmt.executeQuery(query);
				return rs;
				
			}
	}
