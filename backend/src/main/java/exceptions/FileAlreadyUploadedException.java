package exceptions;

public class FileAlreadyUploadedException extends Exception {

    public FileAlreadyUploadedException(String errorMessage) {
        super(errorMessage);
    }

}
