package smsdk.reddit;

import javax.xml.bind.DatatypeConverter;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public class RedditOAuth {

    private static final String REDDIT_AUTHORIZE = "https://www.reddit.com/api/v1/authorize?";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private final String userAgent;
    private OAuthClient oAuthClient;
    private RedditAPI redditApi;
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final String PARAM_STATE = "state";
    private static final String PARAM_REDIRECT_URI = "redirect_uri";
    private static final String PARAM_DURATION = "duration";
    private static final String PARAM_SCOPE = "scope";
    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CODE = "code";

    public RedditOAuth(RedditAPI redditApi) {
        this.userAgent = "Comment Analytics";
        this.redditApi = redditApi;
        this.oAuthClient = new OAuthClient(new URLConnectionClient());
    }

    private void addUserAgent(OAuthClientRequest request) {
        request.addHeader(HEADER_USER_AGENT, userAgent);
    }

    private void addBasicAuthentication(OAuthClientRequest request, String clientId, String clientSecret) {
        String authString = clientId + ":" + clientSecret;
        String authStringEnc = DatatypeConverter.printBase64Binary(authString.getBytes());
        request.addHeader(HEADER_AUTHORIZATION, "Basic " + authStringEnc);
    }

    public synchronized RedditToken tokenAppOnly() {

        try {

            // Set general values of the request
            OAuthClientRequest.TokenRequestBuilder builder = OAuthClientRequest
                    .tokenProvider(OAuthProviderType.REDDIT)
                    .setParameter(PARAM_GRANT_TYPE, GRANT_TYPE_CLIENT_CREDENTIALS)
                    .setClientId(redditApi.getClientId())
                    .setClientSecret(redditApi.getClientSecret());

            // Build the request
            OAuthClientRequest request = builder.buildBodyMessage();

            // Add the user agent
            addUserAgent(request);

            // Add basic authentication
            addBasicAuthentication(request, redditApi.getClientId(), redditApi.getClientSecret());

            // Return a wrapper controlled by jReddit
            return new RedditToken(oAuthClient.accessToken(request));

        } catch (OAuthSystemException oase) {
            System.out.println(oase.getMessage());
        } catch (OAuthProblemException oape) {
            System.out.println(oape.getMessage());
        }
        return null;
    }
}
