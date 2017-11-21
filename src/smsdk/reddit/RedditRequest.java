package smsdk.reddit;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.net.*;

public class RedditRequest {

    private Map<String, String> queryParameters;
    private Map<String, String> bodyParameters;
    private static String endpoint;

    public RedditRequest(String endpoint) {
        queryParameters = new HashMap<String, String>();
        bodyParameters = new HashMap<String, String>();
        this.endpoint = endpoint;
    }

    public void addQueryParameter(String key, String value) {
        queryParameters.put(key, value);
    }

    public void addBodyParameter(String key, String value) {
        bodyParameters.put(key, value);
    }

    public String generateQueryParameters() {
        String paramsString = "";
        boolean start = true;
        for (String key : queryParameters.keySet()) {
            if (!start) {
                paramsString = paramsString.concat("&");
            } else {
                start = false;
            }
            String value = queryParameters.get(key);
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported Encoding Exception thrown in request: " + e.getMessage());
            }
            paramsString = paramsString.concat(key + "=" + value);
        }
        return paramsString;
    }

    public String generateBodyParameters() {
        String paramsString = "";
        boolean start = true;
        for (String key : bodyParameters.keySet()) {
            if (!start) {
                paramsString = paramsString.concat("&");
            } else {
                start = false;
            }
            String value = bodyParameters.get(key);
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported Encoding Exception thrown in request: " + e.getMessage());
            }
            paramsString = paramsString.concat(key + "=" + value);
        }
        return paramsString;
    }

    public String getEndpoint() {
        return endpoint.concat(this.generateQueryParameters());
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
