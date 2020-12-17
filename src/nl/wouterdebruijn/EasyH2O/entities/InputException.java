package nl.wouterdebruijn.EasyH2O.entities;

/**
 * Custom error for user related input errors.
 *
 * @Author Wouter de Bruijn git@rl.hedium.nl
 */
public class InputException extends Exception {
    public String message;

    public InputException(String message) {
        this.message = message;
    }
}