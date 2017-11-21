package smsdk.reddit;

import java.util.HashSet;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;

public class RedditToken {

    public static final String PARAM_TOKEN_TYPE = "token_type";
    private String accessToken;
    private String refreshToken;

    private String tokenType;
    private long expiration;
    private long expirationSpan;
    HashSet<String> scopes;

    public RedditToken(OAuthJSONAccessTokenResponse token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
        this.expiration = currentTimeSeconds() + token.getExpiresIn();
        this.expirationSpan = token.getExpiresIn();
        scopes = new HashSet<String>();
        String[] split = token.getScope().split(",");
        for (String s : split) {
            this.scopes.add(s);
        }
        this.tokenType = token.getParam(PARAM_TOKEN_TYPE);
    }

    public void refresh(OAuthJSONAccessTokenResponse token) {
        this.accessToken = token.getAccessToken();
        this.expiration = currentTimeSeconds() + token.getExpiresIn();
        this.expirationSpan = token.getExpiresIn();
        String[] split = token.getScope().split(",");
        for (String s : split) {
            this.scopes.add(s);
        }
        this.tokenType = token.getParam(PARAM_TOKEN_TYPE);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isExpired() {
        return expiration < currentTimeSeconds();
    }

    public long getExpiration() {
        return expiration;
    }

    public String getTokenType() {
        return tokenType;
    }

    public boolean isRefreshable() {
        return this.getRefreshToken() != null;
    }

    public boolean willExpireIn(long seconds) {
        return currentTimeSeconds() + seconds > expiration;
    }

    public long getExpirationSpan() {
        return expirationSpan;
    }

    private static long currentTimeSeconds() {
        return System.currentTimeMillis() / (long) 1000;
    }
}
