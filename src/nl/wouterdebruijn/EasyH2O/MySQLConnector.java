package nl.wouterdebruijn.EasyH2O;

import java.sql.*;

public class MySQLConnector {
    public Connection con;

    /**
     * Connect to MySQL Database
     *
     * @param hostname hostname of SQL Server (localhost / 127.0.0.1 / 192.168.1.1 / example.org
     * @param user     Username for SQL login
     * @param database Database for SQL Login
     * @param password Password of SQL User
     * @throws SQLException Login fails, throws error.
     */
    public void connect(String hostname, String user, String database, String password) throws SQLException {
        /*
          getConnection requires a connection string, in newer version the username and password aren't added to thing string, but provided as extra arguments.
          The String is generated by using the String.format function. Hostname and dbName are inserted.
         */
        this.con = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", hostname, database), user, password);
    }

    /**
     * Close the connection to MySQL Database
     *
     * @throws SQLException Throws error if disconnect fails
     */
    public void disconnect() throws SQLException {
        if (this.con != null) this.con.close();
    }

    /**
     * Create, execute and return query
     *
     * @return ResultSet
     * @throws SQLException Throws error if function fails
     */
    public ResultSet query(String query) throws SQLException {
        Statement stmt = this.con.createStatement(); //create java statement
        return stmt.executeQuery(query);
    }

    /**
     * Insert microbit data into db
     *
     * @param microbitData Data received from microbit
     * @throws SQLException Throws error if function fails
     * @Author Luca
     */
    public void sendMicroBitData(int regentonId, String microbitData) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO `datapoint`(`regenton`, `data`) VALUES (?, ?)");
        preparedStatement.setInt(1, regentonId);
        preparedStatement.setString(2, microbitData);
        preparedStatement.executeUpdate();
    }
}
