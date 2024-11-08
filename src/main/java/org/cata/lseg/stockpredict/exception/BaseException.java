package org.cata.lseg.stockpredict.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class BaseException extends RuntimeException  {
    private final UUID uuid = UUID.randomUUID();
    //private static final String MESSAGE_TEMPLATE = "uuid: {}, message: {}";
    //private Class<?> callerClass;
    //private String callerClassName;

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(String message, Throwable e) {
        super(message, e);
    }

    /*public BaseException(String message, Class<?> clazz) {
        super(message);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        this.callerClass = clazz;
        this.callerClassName = stackTrace[3].getClassName();
        //throw new BaseException(message, this, false);
    }*/
}
