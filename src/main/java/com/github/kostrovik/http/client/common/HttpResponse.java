package com.github.kostrovik.http.client.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kostrovik.http.client.exceptions.HttpClientException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * project: http-client
 * author:  kostrovik
 * date:    2018-11-14
 * github:  https://github.com/kostrovik/http-client
 */
public class HttpResponse {
    private int status;
    private String message;
    private Object details;
    private Map<String, List<String>> headers;
    private ByteArrayInputStream file;
    private Charset charset;
    private ObjectMapper mapper;
    private String answerDetailsAttribute;

    public HttpResponse(HttpURLConnection connection) {
        this(connection, "");
    }

    public HttpResponse(HttpURLConnection connection, String detailsAttribute) {
        this(connection, detailsAttribute, Charset.forName("UTF-8"));
    }

    public HttpResponse(HttpURLConnection connection, String detailsAttribute, Charset charset) {
        this.status = HttpURLConnection.HTTP_NO_CONTENT;
        this.message = "";
        this.details = new Object();
        this.headers = new HashMap<>();
        this.file = new ByteArrayInputStream(new byte[0]);

        this.charset = charset;
        this.mapper = new ObjectMapper();
        this.answerDetailsAttribute = detailsAttribute;

        buildAnswer(connection);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getDetails() {
        return details;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public ByteArrayInputStream getFile() {
        return file;
    }

    private void buildAnswer(HttpURLConnection connection) {
        try {
            setMetaData(connection);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                parseAnswer(connection.getErrorStream(), true, false);
            } else {
                parseAnswer(connection.getInputStream(), false, !connection.getContentType().contains("application/json"));
            }
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    private void setMetaData(HttpURLConnection connection) throws IOException {
        headers = connection.getHeaderFields();
        status = connection.getResponseCode();
        message = connection.getResponseMessage();
    }

    private void parseAnswer(InputStream input, boolean isError, boolean isFile) throws IOException {
        StringBuilder answer = new StringBuilder();
        if (Objects.nonNull(input)) {
            if (isFile) {
                file = new ByteArrayInputStream(input.readAllBytes());
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        answer.append(line);
                    }
                }
            }
        }

        setDetails(answer.toString(), isError);
    }

    private void setDetails(String answer, boolean isError) {
        if (Objects.nonNull(answer) && !answer.trim().isEmpty()) {
            try {
                Map data = mapper.readValue(answer, Map.class);
                details = (answerDetailsAttribute.isEmpty() || isError) ? data : data.getOrDefault(answerDetailsAttribute, new Object());
                details = Objects.isNull(details) ? new Object() : details;
            } catch (IOException error) {
                throw new HttpClientException(error);
            }
        }
    }
}