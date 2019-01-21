package com.afkl.cases.df.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class AuthenticationInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Value("${simple-travel-api.base-url}${simple-travel-api.auth.token-url}")
    private String authUrl;

    @Value("${simple-travel-api.auth.grant-type}")
    private String grantType;

    @Value("${simple-travel-api.auth.client-id}")
    private String clientId;

    @Value("${simple-travel-api.auth.secret}")
    private String secret;

    private OAuth2AccessToken token = null;

    private RestTemplate restTemplate;

    public AuthenticationInterceptor() {
        //We don't want to use the restTemplate with the auth interceptor, it does not make sense.
        this.restTemplate = new RestTemplate();
    }

    private OAuth2AccessToken getToken() {
        logger.info("Generating new access token...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        headers.setBasicAuth(clientId, secret, Charset.defaultCharset());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grantType);
        map.add("username", clientId);
        map.add("password", secret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<OAuth2AccessToken> response = restTemplate.postForEntity(authUrl, request, OAuth2AccessToken.class);

        if (response.getBody() != null) {
            OAuth2AccessToken token = response.getBody();
            token.setExpiresIn(System.currentTimeMillis() + token.getExpiresIn() * 1000);
            return token;
        }

        logger.warn("Unable to genrate token.");

        return null;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (token == null || token.isExpired()) {
            token = getToken();
        }

        HttpHeaders headers = request.getHeaders();

        headers.setBearerAuth(token.getAccessToken());

        return execution.execute(request, body);
    }

}
