//package edu.gcc.gravy;
//
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import okhttp3.*;
//
//public class SupabaseServiceTest {
//
//    private final String BASE_URL = "https://qenvprrucbmyklugcqal.supabase.co/rest/v1/";
//    secret key removed
//    private final String TEST_USER_ID = "1";
//
//    @Test
//    void testDatabaseConnection() throws Exception {
//        SupabaseService service = new SupabaseService(
//                new OkHttpClient(),
//                BASE_URL,
//                API_KEY
//        );
//
//        String response = service.getProfile(TEST_USER_ID, TEST_JWT);
//
//        assertNotNull(response);
//        assertFalse(response.isEmpty());
//        System.out.println(response);
//        assertTrue(response.contains(TEST_USER_ID));
//    }
//
//    @Test
//    void testSupabaseReachable() throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(BASE_URL)
//                .addHeader("apikey", API_KEY)
//                .build();
//
//        Response response = client.newCall(request).execute();
//
//        assertEquals(200, response.code());
//    }
//
//    @Test
//    void testGetProfileMocked() throws Exception {
//        OkHttpClient mockClient = mock(OkHttpClient.class);
//        Call mockCall = mock(Call.class);
//
//        when(mockClient.newCall(any())).thenReturn(mockCall);
//
//        Response mockResponse = new Response.Builder()
//                .request(new Request.Builder().url("http://test").build())
//                .protocol(Protocol.HTTP_1_1)
//                .code(200)
//                .message("OK")
//                .body(ResponseBody.create(
//                        "[{\"id\":\"123\"}]",
//                        MediaType.get("application/json")
//                ))
//                .build();
//
//        when(mockCall.execute()).thenReturn(mockResponse);
//
//        SupabaseService service = new SupabaseService(
//                mockClient,
//                "http://fake",
//                "fake-key"
//        );
//
//        String result = service.getProfile("123", "fake-jwt");
//
//        assertTrue(result.contains("123"));
//    }
//
//    @Test
//    void getProfile() {
//        SupabaseService service = new SupabaseService(
//                new OkHttpClient(),
//                BASE_URL,
//                API_KEY
//        );
//    }
//
//    @Test
//    void updateProfile() {
//        SupabaseService service = new SupabaseService(
//                new OkHttpClient(),
//                BASE_URL,
//                API_KEY
//        );
//    }
//}