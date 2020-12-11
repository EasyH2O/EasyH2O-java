package nl.wouterdebruijn.EasyH2O.entities;

import org.mindrot.jbcrypt.BCrypt;

/**
 * User class
 * @author Emma
 */
public class User
{
  private String username;
  private String hashedPassword;

  public User(String username, String password)
  {
	this.username = username;
	this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
  }

  /**
   * @author Emma
   * @param password wordt door user ingevoerd
   * @return true if password matches hashedPassword else return false
   */
  public boolean validatePassword(String password)
  {
	return BCrypt.checkpw (password, hashedPassword);
  }

    /**
     * Create a brand new User (encrypt password)
     * @param username Username
     * @param plainText Plain text password.
     * @return A user instance.
     */
    public static User newUser(String username, String plainText) {
        return new User(username, BCrypt.hashpw(plainText, BCrypt.gensalt()));
    }

}
