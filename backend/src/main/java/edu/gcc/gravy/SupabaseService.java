package edu.gcc.gravy;

import okhttp3.*;

public class SupabaseService {

    private static final String BASE_URL = "https://<project>.supabase.co/rest/v1";
    private static final String API_KEY = "YOUR_ANON_KEY";

    private final OkHttpClient client = new OkHttpClient();

    public String getProfile(String userId, String jwt) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "/profiles?id=eq." + userId)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String updateProfile(String userId, String jwt, String json) throws Exception {
        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/profiles?id=eq." + userId)
                .patch(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
