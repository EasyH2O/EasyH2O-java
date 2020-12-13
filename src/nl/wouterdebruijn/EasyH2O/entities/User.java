package nl.wouterdebruijn.EasyH2O.entities;

import nl.wouterdebruijn.EasyH2O.Main;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User class
 *
 * @author Emma
 */
public class User {
    public final int id;
    public final String name;
    public final String email;
    private final String hashedPassword;

    public User(int id, String email, String hashedPassword, String name) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.name = name;
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
     * Create a brand new User (encrypt password)
     * @param id User Id
     * @param email User email
     * @param plainText Plaintext password
     * @param name User full name
     * @return User instance.
     */
    public static User newUser(int id, String email, String plainText, String name) {
        return new User(id, email, BCrypt.hashpw(plainText, BCrypt.gensalt(16)), name);
    }

}
