package com.github.kostrovik.http.client.utils;

import com.github.kostrovik.http.client.dictionaries.HttpProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-14
 * github:  https://github.com/kostrovik/http-client
 */
public class ConnectionUtilsTest {
    private ConnectionUtils utils;

    @BeforeEach
    void init() {
        utils = new ConnectionUtils();
    }

    @Test
    void parseHttpProtocolTest() {
        String testAddress = "http://test-http.url";
        HttpProtocol result = utils.parseProtocol(testAddress);

        assertEquals(HttpProtocol.HTTP, result);
    }

    @Test
    void parseHttpsProtocolTest() {
        String testAddress = "https://test-https.url";
        HttpProtocol result = utils.parseProtocol(testAddress);

        assertEquals(HttpProtocol.HTTPS, result);
    }

    @Test
    void parseErrorProtocolTest() {
        String testAddress = "httpp://test-error.url";
        HttpProtocol result = utils.parseProtocol(testAddress);

        assertEquals(HttpProtocol.HTTP, result);
    }
}