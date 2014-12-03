package tab.com.whoiswhorx.exceptions;

/**
 * Created by juanyanezgc on 02/12/14.
 */
public class CustomException extends Exception {
    public enum ErrorType {
        NETWORK, JSOUP
    }

    private ErrorType errorType;

    public CustomException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;

    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
