package nl.wouterdebruijn.EasyH2O.entities;

import org.mindrot.jbcrypt.BCrypt;

/**
 * User class
 *
 * @author Emma
 */
public class User {
    public String id;
    public String naam;
    public String email;
    private final String hashedPassword;

    public User(String email, String hashedPassword) {
        this.email = email;
        this.hashedPassword = hashedPassword;
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
     *
     * @param email     Email of user.
     * @param plainText Plain text password.
     * @return A user instance.
     */
    public static User newUser(String email, String plainText) {
        return new User(email, BCrypt.hashpw(plainText, BCrypt.gensalt()));
    }

}
