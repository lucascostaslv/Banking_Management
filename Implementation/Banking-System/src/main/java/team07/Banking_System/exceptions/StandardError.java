package team07.Banking_System.exceptions;

import java.io.Serial;
import java.io.Serializable;

public class StandardError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long timestamp;
    private Integer status;
    private String message;
    private String error;
    private String path;

    public StandardError(Long timestamp, Integer status, String message, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.error = error;
        this.path = path;
    }

    public StandardError() {}

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
