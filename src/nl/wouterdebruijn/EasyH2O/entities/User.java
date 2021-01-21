package nl.wouterdebruijn.EasyH2O.entities;

import nl.wouterdebruijn.EasyH2O.Main;
import nl.wouterdebruijn.EasyH2O.Regenton;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User class
 *
 * @author Emma
 */
public class User {
    public final int id;
    public final String name;
    public final String email;
    public final Boolean isAdmin;
    private String hashedPassword;

    public User(int id, String email, String password, String name, Boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * @param password wordt door user ingevoerd
     * @return true if password matches hashedPassword else return false
     * @author Emma
     */
    public boolean validatePassword(String password) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Save the user to the database.
     *
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException Throws SQL error when connection had problems.
     */
    public int save() throws SQLException {
        PreparedStatement preparedStatement = Main.mySQLConnector.con.prepareStatement("INSERT INTO `user`(`naam`, `passwordHash`, `email`) VALUES (?, ?, ?)");
        preparedStatement.setString(1, this.name);
        preparedStatement.setString(2, this.hashedPassword);
        preparedStatement.setString(3, this.email);
        return preparedStatement.executeUpdate();
    }

    /**
     * Gets regentonnen from database associated with this user.
     *
     * @return List of Regenton objects.
     * @throws SQLException on mySQL error.
     * @Author Riham - 19075057@student.hhs.nl
     */
    public List<Regenton> getRegentonnen() throws SQLException {
        PreparedStatement preparedStatement = Main.mySQLConnector.con.prepareStatement("SELECT * from regenton WHERE owner = ?");
        preparedStatement.setInt(1, this.id);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Regenton> resultList = new ArrayList<Regenton>();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String comPort = resultSet.getString("comPort");

            Regenton tempRegenton = new Regenton(id, comPort, this);
            resultList.add(tempRegenton);
        }

        return resultList;
    }

    /**
     * Deletes user
     *
     * @Author =--=
     */
    public void delete() {
        try {
            PreparedStatement preparedStatement = Main.mySQLConnector.con.prepareStatement("DELETE FROM `user` WHERE `id` = ?");
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Create a brand new User (encrypt password)
     *
     * @param id             User Id
     * @param email          User email
     * @param hashedPassword Password hash of user.
     * @param name           User full name
     * @return User instance.
     */
    public static User fromHash(int id, String email, String hashedPassword, String name, Boolean isAdmin) {
        User user = new User(id, email, "tmp", name, isAdmin);
        user.hashedPassword = hashedPassword;
        return user;
    }

}
