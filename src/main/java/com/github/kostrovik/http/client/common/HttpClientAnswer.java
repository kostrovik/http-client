package com.github.kostrovik.http.client.common;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-14
 * github:  https://github.com/kostrovik/http-client
 */
public class HttpClientAnswer {
    private int status;
    private String message;
    private Object details;
    private Map<String, List<String>> headers;
    private ByteArrayInputStream file;

    public HttpClientAnswer(int status, String message) {
        this.status = status;
        this.message = message;
        this.details = new Object();
        this.headers = new HashMap<>();
        this.file = new ByteArrayInputStream(new byte[0]);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public ByteArrayInputStream getFile() {
        return file;
    }

    public void setFile(ByteArrayInputStream file) {
        Objects.requireNonNull(file);
        this.file = file;
    }
}
