package org.cata.lseg.stockpredict.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ErrorInfo(String className, String exMessage, HttpStatus httpStatus, UUID uuid, String path, LocalDateTime timestamp) {
    public ErrorInfo(BaseException ex, HttpStatus httpStatus, String path) {
        this(ex.getClass().getName(), ex.getLocalizedMessage(), httpStatus, ex.getUuid(), path, LocalDateTime.now());
    }
}
