package org.cata.lseg.stockpredict.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalAdvice {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public record ErrorInfo(String className, String exMessage, HttpStatus httpStatus, UUID uuid, String path, String timestamp) {
        public ErrorInfo(BaseException ex, HttpStatus httpStatus, String path) {
            this(ex.getClass().getName(), ex.getLocalizedMessage(), httpStatus, ex.getUuid(), path, LocalDateTime.now().format(DATETIME_FORMATTER));
        }
    }

    @ExceptionHandler({NoFilesException.class,
    NoMarketFoldersException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorInfo> handleNoFilesException(final NoFilesException e, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorInfo errorInfo = new ErrorInfo(e, httpStatus, getPath(request));
        log.warn(errorInfo.toString());

        return ResponseEntity.status(httpStatus).body(errorInfo);
    }

    @ExceptionHandler(FileCounterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorInfo> handleNoFilesException(final FileCounterException e, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorInfo errorInfo = new ErrorInfo(e, httpStatus, getPath(request));
        log.warn(errorInfo.toString());

        return ResponseEntity.status(httpStatus).body(errorInfo);
    }

    @ExceptionHandler({FileParsingException.class,
            FileListingException.class,
            FileWriteException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorInfo> handleFileParsingAndListingException(final BaseException e, WebRequest request) {
        ErrorInfo errorInfo = new ErrorInfo(e, HttpStatus.INTERNAL_SERVER_ERROR, getPath(request));
        log.error(errorInfo.toString());

        return ResponseEntity.internalServerError().body(errorInfo);
    }

    @ExceptionHandler(NotEnoughDataException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<ErrorInfo> handleFileWriteException(final NotEnoughDataException e, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;
        ErrorInfo errorInfo = new ErrorInfo(e, httpStatus, getPath(request));
        log.error(errorInfo.toString());

        return ResponseEntity.status(httpStatus).body(errorInfo);
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
