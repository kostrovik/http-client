package com.github.kostrovik.http.client.common;

import com.github.kostrovik.http.client.utils.Downloader;
import com.github.kostrovik.useful.interfaces.Listener;

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

    public HttpRequest(HttpClient client) {
        this.client = client;
        this.apiUrl = "";
        this.queryParams = new HashMap<>();
        this.data = "";
        this.headers = new HashMap<>();
        this.method = "GET";
    }

    public HttpRequest GET(String apiUrl) {
        Objects.requireNonNull(apiUrl);
        this.method = "GET";
        this.apiUrl = apiUrl;
        return this;
    }

    public HttpRequest POST(String apiUrl) {
        Objects.requireNonNull(apiUrl);
        this.method = "POST";
        this.apiUrl = apiUrl;
        return this;
    }

    public void download(URL from, Path filePath) {
        downloadWithProgressListener(from, filePath, null, null);
    }

    public void download(URL from, Path filePath, Listener<File> listener) {
        downloadWithProgressListener(from, filePath, listener, null);
    }

    public void downloadWithProgressListener(URL from, Path filePath, Listener<File> listener, Listener<Double> progressListener) {
        Downloader loader = new Downloader(client, from, filePath);
        loader.setListener(listener);
        loader.setProgressListener(progressListener);
        new Thread(loader).start();
    }

    public HttpRequest setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public HttpRequest setData(String data) {
        this.data = data;
        return this;
    }

    public HttpRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequest build() throws IOException {
        client.createConnection(method, headers, apiUrl, queryParams);
        client.setConnectionData(data);
        return this;
    }

    public HttpResponse getResponse() {
        return client.getResponse();
    }
}
