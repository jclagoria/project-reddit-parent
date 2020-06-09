package ar.com.reddit.exceptions;

public class SpringRedditException extends RuntimeException {

    public SpringRedditException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public SpringRedditException(String exMessae) {
        super(exMessae);
    }
}
