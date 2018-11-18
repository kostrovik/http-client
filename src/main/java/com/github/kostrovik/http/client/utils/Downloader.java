package com.github.kostrovik.http.client.utils;

import com.github.kostrovik.http.client.common.HttpClient;
import com.github.kostrovik.http.client.interfaces.Listener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-18
 * github:  https://github.com/kostrovik/http-client
 */
public class Downloader implements Runnable {
    private HttpClient client;
    private URL from;
    private Path to;
    private Listener<File> listener;

    public Downloader(HttpClient client, URL from, Path to) {
        Objects.requireNonNull(client);
        this.client = client;

        Objects.requireNonNull(from);
        this.from = from;

        Objects.requireNonNull(to);
        this.to = to;
    }

    public void setListener(Listener<File> listener) {
        if (Objects.nonNull(listener)) {
            this.listener = listener;
        }
    }

    @Override
    public void run() {
        try {
            File result = client.downloadFile(from, to);
            if (Objects.nonNull(listener)) {
                listener.handle(result);
            }
        } catch (IOException e) {
            listener.error(e);
        }
    }
}
