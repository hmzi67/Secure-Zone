package io.github.hmzi67.securezone.Widgets;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import com.android.volley.Request;
import com.google.gson.annotations.SerializedName;

public class TrafficService {

    private static final String BASE_URL = "https://api.opencagedata.com/geocode/v1/json";
//    private static final String APP_ID = "NfWWCp4Jrue5EC2JMeqB";
//    private static final String APP_CODE = "dNnyhRWtElQsSDCVfctdi-5ToN5Jh461Az6r9kkwFgU";
    private static final String APIKEY = "226ee3b17c684ed095df16f25e2c7e19";

    private final RequestQueue requestQueue;
    private final Gson gson;

    public TrafficService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public void getTrafficInfo(double latitude, double longitude, final TrafficResponseListener listener) {
//        String url = BASE_URL + "?prox=" + latitude + "," + longitude + ",1000&app_id=" + APP_ID + "&app_code=" + APP_CODE;
//        String url = BASE_URL + "?in=circle:" + latitude + "," + longitude + ";r=2000&locationReferencing=olr&apiKey=" + APIKEY;
        String url = BASE_URL + "?q=" + latitude + "," + longitude + "&key=" + APIKEY;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response using Gson
                        TrafficData trafficData = gson.fromJson(response.toString(), TrafficData.class);

                        // Pass the traffic data to the listener
                        listener.onResponse(trafficData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        listener.onError(error.getMessage());
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(request);
    }

    // Define a POJO class for parsing JSON response
    public static class TrafficData {

    }


    // Define a listener interface for handling API responses
    public interface TrafficResponseListener {
        void onResponse(TrafficData trafficData);
        void onError(String errorMessage);
    }
}
