package nl.wouterdebruijn.EasyH2O.entities;

public class InputException extends Exception {
    public String message;

    public InputException(String message) {
        this.message = message;
    }
}