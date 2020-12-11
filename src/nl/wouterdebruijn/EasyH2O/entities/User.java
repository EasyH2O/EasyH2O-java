package nl.wouterdebruijn.EasyH2O.entities;

import org.mindrot.jbcrypt.BCrypt;

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
     * Create a brand new User (encrypt password)
     * @param id User Id
     * @param email User email
     * @param plainText Plaintext password
     * @param name User full name
     * @return User instance.
     */
    public static User newUser(int id, String email, String plainText, String name) {
        return new User(id, email, BCrypt.hashpw(plainText, BCrypt.gensalt()), name);
    }

}
