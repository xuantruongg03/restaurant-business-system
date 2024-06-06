package restaurant_business_system.exceptions;

public class OTPException extends RuntimeException {
    public OTPException() {
        super();
    }

    public OTPException(String message) {
        super(message);
    }

    public OTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public OTPException(Throwable cause) {
        super(cause);
    }
}