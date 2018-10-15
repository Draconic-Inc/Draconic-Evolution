package com.brandon3055.draconicevolution.lib;

/**
 * Created by brandon3055 on 15/10/18.
 */
public class WTFException extends RuntimeException {
    public WTFException() {
    }

    public WTFException(String message) {
        super(message);
    }

    public WTFException(String message, Throwable cause) {
        super(message, cause);
    }

    public WTFException(Throwable cause) {
        super(cause);
    }

    public WTFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
