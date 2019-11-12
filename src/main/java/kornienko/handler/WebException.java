package kornienko.handler;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

public class WebException {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public WebException(Exception ex) {
        this.message = ex.getMessage();
    }

    public WebException(final HttpStatus status, final String message, final List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public WebException(final HttpStatus status, final String message, final String error) {
        this.status = status;
        this.message = message;
        this.errors = Collections.singletonList(error);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }

    public void setError(final String error) {
        this.errors = Collections.singletonList(error);
    }

}