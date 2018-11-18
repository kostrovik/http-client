/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-14
 * github:  https://github.com/kostrovik/http-client
 */
module com.github.kostrovik.http.client {
    requires java.logging;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports com.github.kostrovik.http.client.common;
    exports com.github.kostrovik.http.client.interfaces;
}