//created 9/6/2017
//Grizzly
//modifed from ytCLIENT.java by kmcalhoun

package main.sminterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.*;
import smsdk.*;

public class YTClient {

    private static final String API_KEY = "AIzaSyBsAXJxBUV4VvMFSe8GxXnmxgkwiTU7Yhs";
    static YtAPI ytClient;
    private ArrayList<JSONObject> PostArrayList;

    public YTClient() {
        ytClient = new YtAPI(API_KEY);
        PostArrayList = new ArrayList();
    }

    public void fetchComments(String type, String id) {
        System.out.println("I'm here");
        System.out.println(type);
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (type.equals("user")) {
            params.put("forUsername", id);
            params.put("part", "id");
            JSONObject channel = ytClient.getObject("channels", params);
            JSONArray items = channel.getJSONArray("items");
            id = items.getJSONObject(0).getString("id");
            type = "channel";
            params.clear();
        }
        params.put(type + "Id", id);
        System.out.println("type: "+ type + "Id: " + id);
        /*??*/
        params.put("textFormat", "plainText");
        params.put("part", "snippet,replies");
        JSONObject comments = ytClient.getObject("commentThreads", params);
        ArrayList<JSONObject> commentsList = ytClient.convertJsonItemsToList(comments);
        for (JSONObject comment : commentsList) {
            /*PostArrayList.add(comment);
            System.out.println("Comment id: " + comment.getString("id"));
            System.out.println("Message: " + comment.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("textDisplay"));
            System.out.println("Created on: " + comment.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("publishedAt"));
            System.out.println(); */
            PostArrayList.add(comment); 
            //System.out.println("Comment id: " + comment.getString("id"));
            System.out.println("Parent Comment: " + comment.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("textOriginal")+ "\r\n");
             try
                { 
                   // System.out.println("Top message: " + comment.getJSONObject("replies").getJSONArray("comments") );
                    //ArrayList<JSONObject> reply; //.getJSONObject("replies").getJSONArray("comments");
                       // reply = ytClient.convertJsonReplyToList( comment.getJSONObject("replies") );
                
                   ArrayList<JSONObject> reply = new ArrayList<JSONObject>();
                    for(int i = 0; i < comment.getJSONObject("replies").getJSONArray("comments").length(); i++)
                        { reply.add(Utility.parseJson(comment.getJSONObject("replies").getJSONArray("comments").get(i).toString())); }
             
                    System.out.println("Children exist");
                  
                     for( JSONObject replies : reply)
                     { //System.out.println("reading text");
                       System.out.print("   | ");
                       System.out.print("\r\n    -> ");
                       System.out.println(replies.getJSONObject("snippet").getString("textOriginal"));
                       }
                    PostArrayList.add(reply);
                    //System.out.println("Created on: " + comment.getString("publishedAt"));
                  System.out.println();
                 } catch(JSONException jsex)
                    { /*System.out.println(jsex);
                      System.out.println("no replies"); */}
                   catch(Exception ex)
                    { System.out.println(ex); 
                     System.out.println(".,.,"); }
            
        }
    }
    
    public void clearArray() {
        PostArrayList.clear();
    }
}
