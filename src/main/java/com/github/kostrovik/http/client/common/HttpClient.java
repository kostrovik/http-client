package com.github.kostrovik.http.client.common;

import com.github.kostrovik.http.client.dictionaries.HttpProtocol;
import com.github.kostrovik.http.client.utils.ConnectionUtils;
import com.github.kostrovik.useful.interfaces.Listener;
import com.github.kostrovik.useful.utils.ObservableByteChannel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-14
 * github:  https://github.com/kostrovik/http-client
 */
public class HttpClient {
    private String serverAddress;
    private Charset charset;
    private boolean useHttps;
    private ConnectionUtils connectionUtils;
    private String answerDetailsAttribute;
    private URLConnection connection;

    public HttpClient(String serverAddress) {
        this(serverAddress, Charset.forName("UTF-8"));
    }

    public HttpClient(String serverAddress, Charset charset) {
        this.serverAddress = serverAddress;
        this.charset = charset;

        this.connectionUtils = new ConnectionUtils();
        this.useHttps = connectionUtils.parseProtocol(this.serverAddress).equals(HttpProtocol.HTTPS);
        this.answerDetailsAttribute = "";
    }

    public boolean isUseHttps() {
        return useHttps;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        Objects.requireNonNull(charset);
        this.charset = charset;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getAnswerDetailsAttribute() {
        return answerDetailsAttribute;
    }

    public void setAnswerDetailsAttribute(String answerDetailsAttribute) {
        Objects.requireNonNull(answerDetailsAttribute);
        this.answerDetailsAttribute = answerDetailsAttribute;
    }

    public void createConnection(String method, Map<String, String> headers, String apiUrl, Map<String, List<String>> urlParams) throws IOException {
        String requestParams = connectionUtils.prepareQueryParams(urlParams, charset);

        URL serverApiUrl = requestParams.isEmpty()
                ? new URL(serverAddress + apiUrl)
                : new URL(serverAddress + apiUrl + "?" + requestParams);

        connection = createConnection(method, headers, serverApiUrl);
    }

    public void setConnectionData(String data) throws IOException {
        if (Objects.nonNull(data) && !data.trim().isEmpty()) {
            try (OutputStream output = connection.getOutputStream()) {
                if (output != null) {
                    output.write(data.getBytes(charset));
                }
            }
        }
    }

    public HttpResponse getResponse() {
        return new HttpResponse((HttpURLConnection) connection, answerDetailsAttribute, charset);
    }

    public File downloadFile(URL fromUrl, Path filePath) throws IOException {
        preparePath(filePath);
        try (ReadableByteChannel readChannel = Channels.newChannel(fromUrl.openStream())) {
            try (FileChannel writeChannel = new FileOutputStream(filePath.toString()).getChannel()) {
                writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
                return new File(filePath.toUri());
            }
        }
    }

    public File downloadFile(URL fromUrl, Path filePath, Listener<Double> progressListener) throws IOException {
        preparePath(filePath);
        try (ObservableByteChannel readChannel = new ObservableByteChannel(
                contentLength(fromUrl),
                Channels.newChannel(fromUrl.openStream()),
                progressListener
        )) {
            try (FileChannel writeChannel = new FileOutputStream(filePath.toString()).getChannel()) {
                writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
                return new File(filePath.toUri());
            }
        }
    }

    private int contentLength(URL url) throws IOException {
        return url.openConnection().getContentLength();
    }

    private void preparePath(Path filePath) throws IOException {
        if (Files.notExists(filePath)) {
            Files.createDirectories(filePath.getParent());
        }
    }

    private URLConnection createConnection(String method, Map<String, String> headers, URL connectionUrl) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) connectionUrl.openConnection();

        if (!method.equals("GET")) {
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
        }

        httpConnection.setRequestMethod(method);

        headers.forEach(httpConnection::setRequestProperty);

        return httpConnection;
    }
}
