package smsdk.reddit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.*;
import javax.xml.bind.DatatypeConverter;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import org.apache.oltu.oauth2.client.*;
import org.apache.oltu.oauth2.client.request.*;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.*;
import org.apache.oltu.oauth2.common.*;
import org.apache.oltu.oauth2.common.exception.*;

import org.json.JSONException;
import org.json.JSONObject;
import smsdk.Utility;

public class RedditAPI {

    public static final String OAUTH_API_DOMAIN = "https://oauth.reddit.com";

    public static final String API_AUTH = "https://www.reddit.com/api/v1/access_token";

    private final String userAgent;

    private final HttpClient httpClient;

    private String clientId;

    private String clientSecret;

    public RedditAPI(String clientId, String clientSecret) {
        this.userAgent = "Comment Analytics";
        this.httpClient = HttpClientBuilder.create().build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String post(RedditToken rToken, RedditRequest redditRequest) {

        try {
            // Create post request
            HttpPost request = new HttpPost(OAUTH_API_DOMAIN + redditRequest.getEndpoint());

            // Add parameters to body
            request.setEntity(new StringEntity(redditRequest.generateBodyParameters()));

            // Add authorization
            addAuthorization(request, rToken);

            // Add user agent
            addUserAgent(request);

            // Add content type
            request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            return executeHttpRequest(request);

        } catch (UnsupportedEncodingException uee) {
            System.out.println("Unsupported Encoding Exception thrown in POST request when encoding body: " + uee.getMessage());
        }

        return null;

    }

    public String get(RedditToken rToken, RedditRequest redditRequest) {

        // Create get request
        HttpGet request = new HttpGet(OAUTH_API_DOMAIN + redditRequest.getEndpoint());

        // Add authorization
        addAuthorization(request, rToken);

        // Add user agent
        addUserAgent(request);

        return executeHttpRequest(request);

    }

    private String executeHttpRequest(HttpUriRequest request) {
        try {

            // Attempt to do execute request
            HttpResponse response = httpClient.execute(request);

            // Return response if successful
            if (response != null) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }

        } catch (UnsupportedEncodingException uee) {
            System.out.println("Unsupported Encoding Exception thrown in request: " + uee.getMessage());
        } catch (ClientProtocolException cpe) {
            System.out.println("Client Protocol Exception thrown in request: " + cpe.getMessage());
        } catch (IOException ioe) {
            System.out.println("I/O Exception thrown in request: " + ioe.getMessage());
        }

        return null;

    }

    private void addAuthorization(HttpRequest request, RedditToken rToken) {
        request.addHeader("Authorization", rToken.getTokenType() + rToken.getAccessToken());
    }

    private void addUserAgent(HttpRequest request) {
        request.addHeader("User-Agent", userAgent);
    }
    
    public String getClientId(){
        return clientId;
    }
    
    public String getClientSecret(){
        return clientSecret;
    }
      
    public ArrayList<JSONObject> convertJsonDataToList(JSONObject json) {
        try {
            ArrayList<JSONObject> list = new ArrayList<JSONObject>();
            for(int i = 0; i < json.getJSONArray("data").length(); i++){
                list.add(Utility.parseJson(json.getJSONArray("data").get(i).toString()));
            }
            return list;
        } catch (JSONException ex) {
            return null;
        }
    }
}
