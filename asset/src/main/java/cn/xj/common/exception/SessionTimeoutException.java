package cn.xj.common.exception;

public class SessionTimeoutException extends Exception {

    public SessionTimeoutException() {
    }

    public SessionTimeoutException(String message) {
        super(message);
    }

}
