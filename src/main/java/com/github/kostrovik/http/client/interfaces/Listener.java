package com.github.kostrovik.http.client.interfaces;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-18
 * github:  https://github.com/kostrovik/http-client
 */
public interface Listener<T> {
    void handle(T result);

    void error(Throwable error);
}
