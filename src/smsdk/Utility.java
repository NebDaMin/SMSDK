package smsdk;




import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    @SuppressWarnings("deprecation")
    public static String encodeURLParameters(Map<String, Object> params) {
        if (params == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (!isFirst) {
                sb.append('&');
            } else {
                isFirst = false;
            }
            sb.append(URLEncoder.encode(key));
            sb.append('=');
            sb.append(URLEncoder.encode(params.get(key).toString()));
        }
        return sb.toString();
    }

    public static String openUrl(String urlStr, String method,
            Map<String, Object> postArgs) throws IOException,
            MalformedURLException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", System.getProperties()
                .getProperty("http.agent")
                + " SocialMediaSDK");
        if (!method.equals("GET")) {
            if (!postArgs.containsKey("method")) {
                postArgs.put("method", method);
            }

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String encodedPostArgs = encodeURLParameters(postArgs);
            DataOutputStream dos = new DataOutputStream(connection
                    .getOutputStream());
            dos.writeBytes(encodedPostArgs);
            dos.flush();
            dos.close();
        }

        String response = "";
        try {
            response = read(connection.getInputStream());
        } catch (Exception e) {
            response = read(connection.getErrorStream());
        }
        return response;
    }

    private static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public static JSONObject parseJson(String response) throws JSONException {
        // Edge case: when sending a POST request to /[post_id]/likes
        // the return value is 'true' or 'false'. Unfortunately
        // these values cause the JSONObject constructor to throw
        // an exception.
        if (response.equals("false")) {
            System.out.println("request failed");
        }
        if (response.equals("true")) {
            response = "{value : true}";
        }
        JSONObject json = new JSONObject(response);

        // errors set by the server are not consistent
        // they depend on the method and endpoint
        if (json.has("error")) {
            JSONObject error = json.getJSONObject("error");
            System.out.println("Error Message: " + error.getString("message") + ". Error Type: " + error
                    .getString("type"));
        }
        if (json.has("error_code") && json.has("error_msg")) {
            System.out.println("Error Message: " + json.getString("error_msg")+ ". Error Code: " + Integer
                    .parseInt(json.getString("error_code")));
        }
        if (json.has("error_code")) {
            System.out.println("Error Message: request failed"+ ". Error Code: " + Integer.parseInt(json
                    .getString("error_code")));
        }
        if (json.has("error_msg")) {
            System.out.println(json.getString("error_msg"));
        }
        if (json.has("error_reason")) {
            System.out.println(json.getString("error_reason"));
        }
        return json;
    }

}
