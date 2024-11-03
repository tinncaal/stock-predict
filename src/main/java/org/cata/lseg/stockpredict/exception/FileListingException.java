package org.cata.lseg.stockpredict.exception;

public class FileListingException extends BaseException {

    public FileListingException(String message, Throwable e) {
        super(message, e);
    }

    public FileListingException(String message) {
        super(message);
    }
}
