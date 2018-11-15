package com.github.kostrovik.http.client.utils;

import com.github.kostrovik.http.client.dictionaries.HttpProtocol;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-14
 * github:  https://github.com/kostrovik/http-client
 */
public class ConnectionUtils {
    private Pattern serverAddressPattern = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=_!:,.;]*[-a-zA-Z0-9+&@#/%=_]");

    public HttpProtocol parseProtocol(String serverAddress) {
        Matcher matcher = serverAddressPattern.matcher(serverAddress);
        if (matcher.matches() && matcher.group(1).trim().isEmpty()) {
            return HttpProtocol.HTTP;
        }

        try {
            return HttpProtocol.valueOf(matcher.group(1).toUpperCase());
        } catch (IllegalStateException e) {
            return HttpProtocol.HTTP;
        }
    }

    public String prepareQueryParams(Map<String, List<String>> urlParams, Charset charset) {
        return urlParams.keySet().stream().map(key -> {
            if (urlParams.get(key).size() == 1) {
                return String.format("%s=%s", key, encodeValue(urlParams.get(key).get(0), charset));
            } else {
                return urlParams.get(key).stream().map(val -> String.format("%s=%s", key, encodeValue(val, charset))).collect(Collectors.joining("&"));
            }
        }).collect(Collectors.joining("&"));
    }

    public String encodeValue(String value, Charset charset) {
        return URLEncoder.encode(value, charset);
    }
}