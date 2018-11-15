package com.github.kostrovik.http.client.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-15
 * github:  https://github.com/kostrovik/http-client
 */
public class HttpRequest {
    private HttpClient client;
    private String apiUrl;
    private Map<String, List<String>> queryParams;
    private String data;
    private Map<String, String> headers;
    private String method;
    private HttpClientAnswer result;

    public HttpRequest(HttpClient client) {
        this.client = client;
    }

    public void Get(String apiUrl) {
        this.method = "GET";
        this.apiUrl = apiUrl;
    }

    public void Post(String apiUrl) {
        this.method = "POST";
        this.apiUrl = apiUrl;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void build() throws IOException {
        this.result = client.sendRequest(method, apiUrl, headers, data, queryParams);
    }

    public HttpClientAnswer getResult() {
        return result;
    }
}
