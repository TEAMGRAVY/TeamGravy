package edu.gcc.gravy;
import okhttp3.*;
import com.google.gson.*;
import java.nio.file.*;

public class RateMyProfessorClient {

    private static final String RMP_URL = "https://www.ratemyprofessors.com/graphql";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final String SCHOOL_ID = "U2Nob29sLTM4NA==";

    private static final String QUERY =
            "query TeacherSearchResultsPageQuery($query: TeacherSearchQuery!, $schoolID: ID, $includeSchoolFilter: Boolean!) {" +
                    "  search: newSearch { teachers(query: $query, first: 1000, after: \"\") {" +
                    "    edges { cursor node {" +
                    "      id legacyId avgRating numRatings wouldTakeAgainPercent avgDifficulty" +
                    "      department firstName lastName" +
                    "      school { name id }" +
                    "    } }" +
                    "    pageInfo { hasNextPage endCursor }" +
                    "    resultCount" +
                    "  }}" +
                    "  school: node(id: $schoolID) @include(if: $includeSchoolFilter) {" +
                    "    __typename ... on School { name } id" +
                    "  }" +
                    "}";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void exportAllProfessorsToJson(String outputPath) throws Exception {
        // Search with empty string to get all professors at the school
        JsonObject queryVars = new JsonObject();
        queryVars.addProperty("text", "");
        queryVars.addProperty("schoolID", SCHOOL_ID);
        queryVars.addProperty("fallback", true);
        queryVars.add("departmentID", JsonNull.INSTANCE);

        JsonObject variables = new JsonObject();
        variables.add("query", queryVars);
        variables.addProperty("schoolID", SCHOOL_ID);
        variables.addProperty("includeSchoolFilter", true);

        JsonObject body = new JsonObject();
        body.addProperty("query", QUERY);
        body.add("variables", variables);

        RequestBody requestBody = RequestBody.create(gson.toJson(body), JSON_TYPE);

        Request request = new Request.Builder()
                .url(RMP_URL)
                .post(requestBody)
                .addHeader("Authorization", "Basic dGVzdDp0ZXN0")
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:129.0) Gecko/20100101 Firefox/129.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rawJson = response.body().string();

            // Parse and re-serialize with pretty printing
            JsonObject parsed = JsonParser.parseString(rawJson).getAsJsonObject();
            JsonArray professors = parsed
                    .getAsJsonObject("data")
                    .getAsJsonObject("search")
                    .getAsJsonObject("teachers")
                    .getAsJsonArray("edges");

            // Write pretty-printed JSON to file
            Files.writeString(Path.of(outputPath), gson.toJson(professors));
            System.out.println("Exported " + professors.size() + " professors to " + outputPath);
        }
    }
}