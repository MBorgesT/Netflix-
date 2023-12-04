package exceptions;

public class FolderDoesNotExistException extends Exception {
    public FolderDoesNotExistException(String message) {
        super(message);
    }
}
