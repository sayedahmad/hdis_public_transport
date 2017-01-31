package de.tu_berlin.dima.niteout.routing;

/**
 * The class {@code RoutingAPIException} is a form of {@code Exception} that
 * indicates conditions that an application using the RoutingAPI might want to
 * catch.
 * <p>
 * The {@code RoutingAPIException} and its subclasses also provides information
 * why the process went wrong and is the only data class used by the
 * {@code RoutingAPI} to express failing conditions.
 *
 * @author Thomas Wirth
 */
public class RoutingAPIException extends Exception {

    private final ErrorCode code;

    /**
     * Constructs a new exception with {@code null} as its detail message and
     * an error code to describe the failing condition. The cause is not
     * initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     *
     * @param code the error code describing the occurring condition
     */
    public RoutingAPIException(ErrorCode code) {
        super();
        this.code = code;
    }

    /**
     * Constructs a new exception with the error code and the specified
     * detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause}.
     *
     * @param code    the error code describing the occurring condition
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RoutingAPIException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructs a new exception with the error code, specified cause and
     * a detail message.
     *
     * @param code  the error code describing the occurring condition
     * @param cause the cause exception
     */
    public RoutingAPIException(ErrorCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * Constructs a new exception of the routing api with the error code, the
     * specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this exception's detail message.
     *
     * @param code    the error code describing the occurring condition
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public RoutingAPIException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Returns the error code of this RoutingAPIException which describes the
     * condition that occurred and finally lead to the error.
     *
     * @return  the error code describing the failure condition, which may be

     */
    public ErrorCode getCode() {
        return code;
    }


    public enum ErrorCode {

        MISSING_KEYS("API keys were not set in the system properties of the running jvm instance"),
        INVALID_KEYS("Some API key is invalid");

        public final String message;

        ErrorCode(String message) {
            this.message = message;
        }

    }
}
