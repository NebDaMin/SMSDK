package smsdk;




import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

public class FbAPI {

    // Connection types
    public static final String CT_FRIENDS = "friends";
    public static final String CT_NEWS_FEED = "home";
    public static final String CT_PROFILE_FEED = "feed";
    public static final String CT_LIKES = "likes";
    public static final String CT_MOVIES = "movies";
    public static final String CT_MUSIC = "music";
    public static final String CT_BOOKS = "books";
    public static final String CT_NOTES = "notes";
    public static final String CT_PHOTO_TAGS = "photos";
    public static final String CT_PHOTO_ALBUMS = "albums";
    public static final String CT_VIDEO_TAGS = "videos";
    public static final String CT_VIDEO_UPLOADS = "videos/uploaded";
    public static final String CT_EVENTS = "events";
    public static final String CT_GROUPS = "groups";
    public static final String CT_CHECKINS = "checkins";
    public static final String CT_COMMENTS = "comments";

    // Common URL parameters
    public static final String PARAM_FIELDS = "fields";
    public static final String PARAM_IDS = "ids";
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_METHOD = "method";

    public static final String ID_ME = "me";

    public static final String FB_URL = "https://graph.facebook.com/";

    private String accessToken;

    public FbAPI(String accessToken) {
        this.accessToken = accessToken;
    }

    public FbAPI(String appId, String appSecret) {
       this.accessToken = requestAppAccessToken(appId, appSecret); 
    }
    
    public String getAccessToken(){
        return accessToken;
    }

    public JSONObject getObject(String id) {
        return request(id, null, null);
    }

    public JSONObject getObject(String id, Map<String, Object> params) {
        return request(id, params, null);
    }

    public JSONObject getObjects(List<String> ids, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String id : ids) {
            if (!isFirst) {
                sb.append(',');
            } else {
                isFirst = false;
            }
            sb.append(id);
        }
        params.put(PARAM_IDS, sb.toString());
        return request("", params, null);
    }

    public JSONObject getConnections(String id, String connectionName, Map<String, Object> params) {
        return request(id + '/' + connectionName, params, null);
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
        String urlStr = FB_URL + path + '?'
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

    public String requestAppAccessToken(String appId, String appSecret) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("client_id", appId);
        params.put("client_secret", appSecret);
        params.put("grant_type", "client_credentials");
        String urlStr = FB_URL + "oauth/access_token?" + Utility.encodeURLParameters(params);
        JSONObject resp = new JSONObject();
        try {
            String response = Utility.openUrl(urlStr, "GET", null);
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
