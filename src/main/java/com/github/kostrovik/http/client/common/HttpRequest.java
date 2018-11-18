package com.github.kostrovik.http.client.common;

import com.github.kostrovik.http.client.interfaces.Listener;
import com.github.kostrovik.http.client.utils.Downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        this.apiUrl = "";
        this.queryParams = new HashMap<>();
        this.data = "";
        this.headers = new HashMap<>();
        this.method = "GET";
    }

    public void GET(String apiUrl) {
        Objects.requireNonNull(apiUrl);
        this.method = "GET";
        this.apiUrl = apiUrl;
    }

    public void POST(String apiUrl) {
        Objects.requireNonNull(apiUrl);
        this.method = "POST";
        this.apiUrl = apiUrl;
    }

    public void download(URL from, Path filePath) {
        Downloader loader = new Downloader(client, from, filePath);
        new Thread(loader).start();
    }

    public void download(URL from, Path filePath, Listener<File> listener) {
        Downloader loader = new Downloader(client, from, filePath);
        loader.setListener(listener);
        new Thread(loader).start();
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
