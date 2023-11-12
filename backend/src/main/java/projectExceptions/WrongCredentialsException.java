package projectExceptions;

public class WrongCredentialsException extends Exception {

    public WrongCredentialsException(String errorMessage) {
        super(errorMessage);
    }

}
