package ba.kripas.jplag;

public class InvalidOptionsException extends Exception {
    public InvalidOptionsException() {
    }

    public InvalidOptionsException(String message) {
        super(message);
    }
}
