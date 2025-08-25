package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;
    protected final String serverUrl;

    public BaseClient(RestTemplate rest, String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    // GET методы
    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, null, parameters);
    }

    // POST методы
    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, body, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return post(path, userId, body, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, body, parameters);
    }

    // PUT методы
    protected <T> ResponseEntity<Object> put(String path, Long userId, T body) {
        return put(path, userId, body, null);
    }

    protected <T> ResponseEntity<Object> put(String path, Long userId, T body, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, body, parameters);
    }

    // PATCH методы
    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return patch(path, userId, body, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, body, parameters);
    }

    // DELETE методы
    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null, parameters);
    }

    // Основной метод для отправки запросов
    protected <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method, String path, Long userId, T body, Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> serverResponse;
        try {
            if (parameters != null && !parameters.isEmpty()) {
                serverResponse = rest.exchange(serverUrl + path, method, requestEntity, Object.class, parameters);
            } else {
                serverResponse = rest.exchange(serverUrl + path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }

}