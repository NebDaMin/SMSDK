package smsdk;

//created 7/6/2017
//Grizzly
//modifed from ytAPI.java from kmcalhoun

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

public class YtAPI {

    //Connection types
    public static final String CT_ACTIVITIES = "activities";
    public static final String CT_CAPTIONS = "captions";
    public static final String CT_CHANNEL_BANNERS = "channelBanners";
    public static final String CT_CHANNELS = "channels";
    public static final String CT_CHANNEL_SECTIONS = "channelSections";
    public static final String CT_COMMENTS = "comments";
    public static final String CT_COMMENT_THREADS = "commentThreads";
    public static final String CT_GUIDE_CATEGORIES = "guideCategories";
    public static final String CT_I18NLANGUAGES = "i18nLanguages";
    public static final String CT_I19NREGIONS = "i18nRegions";
    public static final String CT_PLAYLIST_ITEMS = "playlistItems";
    public static final String CT_PLAYLISTS = "playlists";
    public static final String CT_SEARCH = "search";
    public static final String CT_SUBSCRIPTIONS = "subscriptions";
    public static final String CT_THUMBNAILS = "thumbnails";
    public static final String CT_VIDEO_CATEGORIES = "videoCategories";
    public static final String CT_VIDEOS = "videos";

    public static final String PARAM_KEY = "key";

    public String apiKey;

    public static final String YT_URL = "https://www.googleapis.com/youtube/v3/";

    public YtAPI(String apiKey){
        this.apiKey = apiKey;
    }
    
    public JSONObject getObject(String connection, Map<String, Object> params) {
        return request(connection, params, null);
    }
    
    public JSONObject request(String path, Map<String, Object> params, Map<String, Object> postArgs) {
        if (postArgs != null) {
            postArgs.put(PARAM_KEY, apiKey);
        } else {
            if (params == null) {
                params = new HashMap<String, Object>();
            }
            params.put(PARAM_KEY, apiKey);
        }
        
        String urlStr = YT_URL + path + '?'
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
    
    public ArrayList<JSONObject> convertJsonItemsToList(JSONObject json)
      { try
         { ArrayList<JSONObject> list = new ArrayList<JSONObject>();
            for(int i = 0; i < json.getJSONArray("items").length(); i++)
             { list.add(Utility.parseJson(json.getJSONArray("items").get(i).toString())); }
            return list;
           } catch(JSONException ex)
               { return null; }
       }
    public ArrayList<JSONObject> convertJsonReplyToList(JSONObject json)
      { try
         { ArrayList<JSONObject> list = new ArrayList<JSONObject>();
           System.out.println("hello?");
            for(int i = 0; i < json.getJSONArray("comments").length(); i++)
             {  System.out.println("Replies"); 
                list.add(  Utility.parseJson( json.getJSONObject("replies").getJSONArray("comments").get(i).toString() ) );
                
               }
          // json.getJSONArray("items").getJSONObject("replies").getJSONArray("comments").get(0).toString() ) ); }
            return list;
           } catch(JSONException ex)
               { System.out.println(ex);
                   
                   return null; }
       }    
    
    
  }
