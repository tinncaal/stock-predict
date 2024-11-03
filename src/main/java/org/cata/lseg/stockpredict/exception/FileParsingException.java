package org.cata.lseg.stockpredict.exception;

public class FileParsingException extends BaseException {

    public FileParsingException(String message, Throwable e) {
        super(message, e);
    }

    public FileParsingException(String message) {
        super(message);
    }
}
