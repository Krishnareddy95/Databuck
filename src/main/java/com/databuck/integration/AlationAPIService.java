package com.databuck.integration;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class AlationAPIService {

    public boolean validateAlationAccessToken(String alationBaseUrl, String accessToken, int userId){
        boolean isAccessTokenValid = false;
        try {
            //If alationBaseUrl does not contains https then append it
            if(!alationBaseUrl.contains("http"))
                alationBaseUrl = "https://"+alationBaseUrl;

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"api_access_token\":\""+accessToken+"\",\"user_id\":"+userId+"}");

            String publishUrl = ""+alationBaseUrl+"/integration/v1/validateAPIAccessToken/";
            Request request = new Request.Builder()
                    .url(publishUrl)
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .build();

            System.out.println("\n====>request url: " + publishUrl);

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            System.out.println("\n====> status code: " + response.code());

            if(responseBody!=null && !responseBody.trim().isEmpty()) {
                JSONObject responseObj = new JSONObject(responseBody);
                if(responseObj.has("api_access_token")) {

                    try {
                        String resAPIAccessToken = responseObj.getString("api_access_token");
                        String tokenStatus = responseObj.getString("token_status");
                        if (resAPIAccessToken.trim().equalsIgnoreCase(accessToken) && tokenStatus.trim().equalsIgnoreCase("ACTIVE"))
                            isAccessTokenValid = true;
                    }catch (Exception e){
                        System.out.println(e.getLocalizedMessage());
                        System.out.println("\n====> response body: " + responseBody);
                    }
                }else
                    System.out.println("\n====>Validation failed for API Access token ["+accessToken+"] ");
            }else
                System.out.println("\n====>API Access token ["+accessToken+"] validation for Alation failed.");
        }catch (Exception e){
            e.printStackTrace();
        }
        return isAccessTokenValid;
    }

    public String generateAlationAccessToken(String alationBaseUrl, String refreshToken, int userId){
        String accessAPIToken="";
        try {
            //If alationBaseUrl does not contains https then append it
            if(!alationBaseUrl.contains("http"))
                alationBaseUrl = "https://"+alationBaseUrl;

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"refresh_token\":\""+refreshToken+"\",\"user_id\":"+userId+"}");

            String publishUrl= ""+alationBaseUrl+"/integration/v1/createAPIAccessToken/";
            Request request = new Request.Builder()
                    .url(publishUrl)
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .build();

            System.out.println("\n====>request url: " + publishUrl);

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            int statusCode= response.code();
            System.out.println("\n====> status code: " + statusCode);

            if(statusCode !=200 && statusCode!= 201){
                System.out.println("\n====> request body: "+ "{\"refresh_token\":\""+refreshToken+"\",\"user_id\":"+userId+"}");
                System.out.println("\n====> response body: " + responseBody);
            }

            if(responseBody!=null && !responseBody.trim().isEmpty()) {
                JSONObject responseObj = new JSONObject(responseBody);

                if(responseObj!=null && responseObj.has("token_status")) {
                    try {
                        String tokenStatus = responseObj.getString("token_status");
                        if (tokenStatus.trim().equalsIgnoreCase("ACTIVE"))
                            accessAPIToken = responseObj.getString("api_access_token");
                    }catch (Exception e){
                        System.out.println(e.getLocalizedMessage());
                    }
                }else
                    System.out.println("\n====>Could not generate new API Access token for Alation");
            }else
                System.out.println("\n====>Could not generate new API Access token for Alation");
        }catch (Exception e){
            e.printStackTrace();
        }
        return accessAPIToken;
    }
}
