package com.github.kostrovik.http.client.exceptions;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-12-14
 * github:  https://github.com/kostrovik/http-client
 */
public class HttpClientException extends RuntimeException {
    public HttpClientException() {
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(Throwable cause) {
        super(cause);
    }
}
