package com.finance.auth;

import com.finance.database.YandexAuthDAO;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import java.awt.Desktop;
import java.net.URI;

public class YandexSignIn {

    private static final String CLIENT_ID = "................";
    private static final String CLIENT_SECRET = ".....................";
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private String authenticatedEmail;

    public String getAuthenticatedEmail() {
        return authenticatedEmail;
    }
    public void loginWithYandex() throws Exception {
        OAuth20Service service = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(REDIRECT_URI)
                .build(YandexApi.instance());

        String authUrl = service.getAuthorizationUrl() + "&prompt=select_account";
        Desktop.getDesktop().browse(new URI(authUrl));
        System.out.println("Opening browser for login...");

        String code = waitForAuthorizationCode();
        System.out.println("Received code: " + code);

        OAuth2AccessToken token = service.getAccessToken(code);
        System.out.println("Access Token: " + token.getAccessToken());

        OAuthRequest request = new OAuthRequest(Verb.GET, "https://login.yandex.ru/info?format=json");
        service.signRequest(token, request);
        Response response = service.execute(request);

        JSONObject obj = new JSONObject(response.getBody());
        String id = obj.optString("id");
        String login = obj.optString("login");
        String email = obj.optString("default_email");
        authenticatedEmail = email;

        YandexUser user = new YandexUser(id, login, email);
        System.out.println("User: " + user.getLogin() + " | Email: " + user.getEmail());

        YandexAuthDAO.saveYandexUser(user);
    }

    private String waitForAuthorizationCode() throws Exception {
        AtomicReference<String> codeHolder = new AtomicReference<>();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                String code = query.split("code=")[1].split("&")[0];
                codeHolder.set(code);

                String response = "Authorization successful! You can close this window.";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                server.stop(1);
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        });

        server.start();
        System.out.println("Waiting for authorization code at " + REDIRECT_URI + " ...");

        while (codeHolder.get() == null) {
            Thread.sleep(500);
        }

        return codeHolder.get();
    }

    // Yandex API definition
    private static class YandexApi extends DefaultApi20 {
        protected YandexApi() {}
        private static class InstanceHolder {
            private static final YandexApi INSTANCE = new YandexApi();
        }
        public static YandexApi instance() {
            return InstanceHolder.INSTANCE;
        }
        @Override
        public String getAccessTokenEndpoint() {
            return "https://oauth.yandex.com/token";
        }
        @Override
        protected String getAuthorizationBaseUrl() {
            return "https://oauth.yandex.com/authorize";
        }
    }
}