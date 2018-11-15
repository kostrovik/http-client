package com.github.kostrovik.http.client.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kostrovik.http.client.dictionaries.HttpProtocol;
import com.github.kostrovik.http.client.utils.ConnectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
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
    private ObjectMapper mapper;

    public HttpClient(String serverAddress) {
        this(serverAddress, Charset.forName("UTF-8"));
    }

    public HttpClient(String serverAddress, Charset charset) {
        this.serverAddress = serverAddress;
        this.charset = charset;

        connectionUtils = new ConnectionUtils();
        this.useHttps = connectionUtils.parseProtocol(this.serverAddress).equals(HttpProtocol.HTTPS);
        mapper = new ObjectMapper();
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

    public HttpClientAnswer sendRequest(String method, String apiUrl, Map<String, String> headers, String data, Map<String, List<String>> urlParams) throws IOException {
        StringBuilder response = new StringBuilder();
        String requestParams = connectionUtils.prepareQueryParams(urlParams, charset);

        URL serverApiUrl = new URL(serverAddress + apiUrl + "?" + requestParams);

        URLConnection connection = createConnection(method, headers, serverApiUrl);

        if (!data.trim().isEmpty()) {
            try (OutputStream output = connection.getOutputStream()) {
                if (output != null) {
                    output.write(data.getBytes(charset));
                }
            }
        }

        return parseResponse(response, (HttpURLConnection) connection);
    }

    private HttpClientAnswer parseResponse(StringBuilder answer, HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            try (InputStreamReader input = new InputStreamReader(connection.getErrorStream(), charset)) {
                int character;
                while (((character = input.read()) != -1)) {
                    answer.append((char) character);
                }

                return buildAnswer(connection.getResponseCode(), connection.getResponseMessage(), null);
            }
        } else {
            String contentType = connection.getContentType();
            if (contentType.contains("application/json")) {
                try (InputStreamReader input = new InputStreamReader(connection.getInputStream(), charset)) {
                    int character;
                    while (((character = input.read()) != -1)) {
                        answer.append((char) character);
                    }

                    HttpClientAnswer ans = buildAnswer(connection.getResponseCode(), connection.getResponseMessage(), answer.toString());
                    ans.setHeaders(connection.getHeaderFields());
                    return ans;
                }
            } else {
                try (InputStream inputStream = connection.getInputStream()) {
                    HttpClientAnswer ans = buildAnswer(connection.getResponseCode(), connection.getResponseMessage(), null);
                    ans.setHeaders(connection.getHeaderFields());
                    ans.setFile((ByteArrayInputStream) inputStream);
                    return ans;
                }
            }
        }
    }

    private HttpClientAnswer buildAnswer(int responseCode, String responseMessate, String answer) {
        Object details = new Object();
        if (Objects.nonNull(answer) && !answer.trim().isEmpty()) {
            try {
                Map data = mapper.readValue(answer, Map.class);
                details = data.getOrDefault("details", new Object());
            } catch (IOException error) {
            }
        }

        HttpClientAnswer ans = new HttpClientAnswer(responseCode, responseMessate);
        ans.setDetails(details);
        return ans;
    }

    private URLConnection createConnection(String method, Map<String, String> headers, URL connectionUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();

        if (!method.equals("GET")) {
            connection.setDoOutput(true);
            connection.setDoInput(true);
        }

        connection.setRequestMethod(method);

        headers.forEach(connection::setRequestProperty);

        return connection;
    }
}
