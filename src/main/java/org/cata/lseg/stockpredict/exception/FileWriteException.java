package org.cata.lseg.stockpredict.exception;

public class FileWriteException extends BaseException {

    public FileWriteException(String message, Throwable e) {
        super(message, e);
    }

    public FileWriteException(String message) {
        super(message);
    }
}
