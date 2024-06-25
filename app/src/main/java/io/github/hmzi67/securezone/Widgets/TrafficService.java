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

    private static final String BASE_URL = "https://data.traffic.hereapi.com/v7/flow";
//    private static final String APP_ID = "NfWWCp4Jrue5EC2JMeqB";
//    private static final String APP_CODE = "dNnyhRWtElQsSDCVfctdi-5ToN5Jh461Az6r9kkwFgU";
    private static final String APIKEY = "j2qExmXqCdc7JYMB5XZQVsaVtVfyiqQsewz_IxkI8z8";

    private final RequestQueue requestQueue;
    private final Gson gson;

    public TrafficService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public void getTrafficInfo(double latitude, double longitude, final TrafficResponseListener listener) {
//        String url = BASE_URL + "?prox=" + latitude + "," + longitude + ",1000&app_id=" + APP_ID + "&app_code=" + APP_CODE;
        String url = BASE_URL + "?in=circle:" + latitude + "," + longitude + ";r=2000&locationReferencing=olr&apiKey=" + APIKEY;



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
        @SerializedName("results")
        public Result[] results;

        public static class Result {
            @SerializedName("location")
            public Location location;

            @SerializedName("currentFlow")
            public CurrentFlow currentFlow;

            public static class Location {
                @SerializedName("description")
                public String description;

                @SerializedName("length")
                public int length;

                @SerializedName("shape")
                public Shape shape;

                public static class Shape {
                    @SerializedName("links")
                    public Link[] links;

                    public static class Link {
                        @SerializedName("points")
                        public Point[] points;

                        @SerializedName("length")
                        public int length;

                        public static class Point {
                            @SerializedName("lat")
                            public double lat;

                            @SerializedName("lng")
                            public double lng;
                        }
                    }
                }
            }

            public static class CurrentFlow {
                @SerializedName("speed")
                public double speed;

                @SerializedName("speedUncapped")
                public double speedUncapped;

                @SerializedName("freeFlow")
                public double freeFlow;

                @SerializedName("jamFactor")
                public double jamFactor;

                @SerializedName("confidence")
                public double confidence;

                @SerializedName("traversability")
                public String traversability;

                @SerializedName("subSegments")
                public SubSegment[] subSegments;

                public static class SubSegment {
                    @SerializedName("length")
                    public int length;

                    @SerializedName("speed")
                    public double speed;

                    @SerializedName("speedUncapped")
                    public double speedUncapped;

                    @SerializedName("freeFlow")
                    public double freeFlow;

                    @SerializedName("jamFactor")
                    public double jamFactor;

                    @SerializedName("confidence")
                    public double confidence;

                    @SerializedName("traversability")
                    public String traversability;
                }
            }
        }
    }


    // Define a listener interface for handling API responses
    public interface TrafficResponseListener {
        void onResponse(TrafficData trafficData);
        void onError(String errorMessage);
    }
}
