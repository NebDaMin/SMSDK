package smsdk.twitter;

import com.google.common.base.Charsets;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONException;
import org.json.JSONObject;
import smsdk.Utility;
import org.apache.http.client.methods.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;

public class TwitterAPI {

    //Connection types
    public static final String CT_STATUSES = "status/show";
    public static final String CT_RETWEETS = "statuses/retweets";

    public static final String PARAM_ACCESS_TOKEN = "access_token";

    private JSONObject token;
    private TwitterOauth auth;
    private HttpClient client;

    public static final String TWT_URL = "https://api.twitter.com/1.1/";

    public TwitterAPI(String appId, String appSecret) {
        this.client = HttpClientBuilder.create().build();
        this.token = requestToken(appId, appSecret);
    }

    public TwitterAPI(String appId, String appSecret, String token, String tokenSecret) {
        this.auth = new TwitterOauth(appId, appSecret, token, tokenSecret);
        this.client = HttpClientBuilder.create().build();
    }

    public JSONObject getObject(Map<String, Object> params) {
        return request("statuses/show.json", params, null);
    }

    public JSONObject requestToken(String appId, String appSecret) {
        String authString = appId + ":" + appSecret;
        String authStringEnc = DatatypeConverter.printBase64Binary(authString.getBytes());
        HttpPost request = new HttpPost("https://api.twitter.com/oauth2/token");
        request.setHeader("Authorization", "Basic " + authStringEnc);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "client_credentials"));

        JSONObject resp = new JSONObject();
        try {
            request.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(request);
            resp = Utility.parseJson(response.getEntity().getContent().toString());
        } catch (UnsupportedEncodingException ex) {
            System.out.println("UnsupportedEncodingException: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }

        return resp;
    }

    public JSONObject request(String path, Map<String, Object> params, Map<String, Object> postArgs) {

        HttpUriRequest request = null;
        String urlStr = TWT_URL + path;
        String method = (postArgs == null) ? "GET" : "POST";
        if (method.equals("GET")) {
            if (auth != null) {
                HttpGet get = new HttpGet(urlStr);
                auth.signRequest(get, null);
                request = get;
            }
        } else if (method.equals("POST")) {
            HttpPost post = new HttpPost(urlStr);
            post.setEntity(new StringEntity('?' + Utility.encodeURLParameters(postArgs), Charsets.UTF_8));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            if (auth != null) {
                auth.signRequest(post, '?' + Utility.encodeURLParameters(postArgs));
            }
            request = post;
        }

        if (request != null) {
            String postContent = null;
            if (method.equals("PUT")) {
                postContent = '?' + Utility.encodeURLParameters(postArgs);
            }
            auth.signRequest(request, postContent);
        }

        JSONObject resp = new JSONObject();
        try {
            HttpResponse response = client.execute(request);
            System.out.println(response.toString());
            resp = Utility.parseJson(response.getEntity().toString());
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        } catch (JSONException ex) {
            System.out.println("JSONException: " + ex.getMessage());
        }

//        try {
//            String response = Utility.openUrl(urlStr, method, postArgs);
//            resp = Utility.parseJson(response);
//        } catch (MalformedURLException ex) {
//            System.out.println("MalformedURLException: " + ex.getMessage());
//        } catch (IOException ex) {
//            System.out.println("IOException: " + ex.getMessage());
//        } catch (JSONException ex) {
//            System.out.println("JSONException: " + ex.getMessage());
//        }
        return resp;
    }

    public ArrayList<JSONObject> convertJsonItemsToList(JSONObject json) {
        try {
            ArrayList<JSONObject> list = new ArrayList<JSONObject>();
            for (int i = 0; i < json.getJSONArray("items").length(); i++) {
                list.add(Utility.parseJson(json.getJSONArray("items").get(i).toString()));
            }
            return list;
        } catch (JSONException ex) {
            return null;
        }
    }
}
