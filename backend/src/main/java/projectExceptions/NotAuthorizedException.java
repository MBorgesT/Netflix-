package projectExceptions;

public class NotAuthorizedException extends Exception {

    public NotAuthorizedException(String errorMessage) {
        super(errorMessage);
    }

}
