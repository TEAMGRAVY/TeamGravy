package edu.gcc.gravy;

import okhttp3.*;

public class SupabaseService {

    private final OkHttpClient client;
    private final String baseUrl;
    private final String apiKey;

    public SupabaseService(OkHttpClient client, String baseUrl, String apiKey) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public String getProfile(String userId, String jwt) throws Exception {
        Request request = new Request.Builder()
                .url(baseUrl + "/profiles?id=eq." + userId)
                .addHeader("apikey", apiKey)
                //.addHeader("Authorization", "Bearer " + jwt)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String updateProfile(String userId, String jwt, String json) throws Exception {
        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(baseUrl + "/profiles?id=eq." + userId)
                .patch(body)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
