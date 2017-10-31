package smsdk.twitter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import smsdk.Utility;

public class TwitterAPI {

    //Connection types
    public static final String CT_STATUSES = "status/show";
    public static final String CT_RETWEETS = "statuses/retweets";

    public static final String PARAM_ACCESS_TOKEN = "access_token";

    public String accessToken;

    public static final String TWT_URL = "https://api.twitter.com/1.1/";
    
    public TwitterAPI(String accessToken) {
        this.accessToken = accessToken;
    }

    public TwitterAPI(String appId, String appSecret) {
       this.accessToken = requestAppAccessToken(appId, appSecret); 
    }
    
    public JSONObject getObject(Map<String, Object> params) {
        return request("statuses/show.json", params, null);
    }

    public String requestAppAccessToken(String appId, String appSecret) {
        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("consumer_key", appId);
//        params.put("consumer_secret", appSecret);
        params.put("grant_type", "client_credentials");
        String urlStr = "https://api.twitter.com/oauth2/token?";
        JSONObject resp = new JSONObject();
        try {
            String response = Utility.openUrl(urlStr, "POST", params);
            resp = Utility.parseJson(response);
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        } catch (JSONException ex) {
            System.out.println("JSONException: " + ex.getMessage());
        }

        return resp.getString("access_token");
    }


    public JSONObject request(String path, Map<String, Object> params, Map<String, Object> postArgs) {
        if (accessToken != null) {
            if (postArgs != null) {
                postArgs.put(PARAM_ACCESS_TOKEN, accessToken);
            } else {
                if (params == null) {
                    params = new HashMap<String, Object>();
                }
                params.put(PARAM_ACCESS_TOKEN, accessToken);
            }
        }

        String urlStr = TWT_URL + path + '?'
                + Utility.encodeURLParameters(params);
        String method = (postArgs == null) ? "GET" : "POST";

        JSONObject resp = new JSONObject();
        try {
            String response = Utility.openUrl(urlStr, method, postArgs);
            resp = Utility.parseJson(response);
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        } catch (JSONException ex) {
            System.out.println("JSONException: " + ex.getMessage());
        }

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
