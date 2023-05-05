package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ApiResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class LogIn {
        @JsonProperty("access_token")
        public String accessToken;
        @JsonProperty("secret")
        public String secret;

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getSecret() {
            return secret;
        }
    }
}
