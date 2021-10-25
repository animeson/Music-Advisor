import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Locale;

public class EndPoint {
    private static final HttpClient client = HttpClient.newBuilder().build();
    public static String SPOTIFY_API_SERVER_POINT;
    public EndPoint() {
    }

    public void newReleases() throws IOException, InterruptedException {
        // List of items - correspondent with the "items" key in the json response.
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OauthServer.accessToken)
                .uri(URI.create(SPOTIFY_API_SERVER_POINT + "/v1/browse/new-releases"))
                .build();

        HttpResponse<String> response = client.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        ParseAndPrint output = new ParseAndPrint(response.body());
        output.newReleases();
    }

    public void featured() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OauthServer.accessToken)
                .uri(URI.create(SPOTIFY_API_SERVER_POINT + "/v1/browse/featured-playlists"))
                .build();

        HttpResponse<String> response = client.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        ParseAndPrint output = new ParseAndPrint(response.body());
        output.featured();
    }

    public String categories(boolean print) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OauthServer.accessToken)
                .uri(URI.create(SPOTIFY_API_SERVER_POINT + "/v1/browse/categories"))
                .build();

        HttpResponse<String> response = client.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        if (print) {
            ParseAndPrint output = new ParseAndPrint(response.body());
            output.categories();
        }

        return response.body();
    }

    public void playlistsByCategoryName(String categoryName) throws IOException, InterruptedException {
        JsonElement allCategories = JsonParser.parseString(categories(false)).getAsJsonObject();
        String categoryId = findCategoryIdByName(categoryName,allCategories);
        if ("Unknown category name.".equals(categoryId)) {
            System.out.println(categoryId);
        } else {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + OauthServer.accessToken)
                    .uri(URI.create(SPOTIFY_API_SERVER_POINT + "/v1/browse/categories/" + categoryId + "/playlists"))
                    .build();

            HttpResponse<String> response = client.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (response.body().contains("error")) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject error = json.getAsJsonObject("error");
                System.out.println(error.get("message").getAsString());
            } else {
                ParseAndPrint output = new ParseAndPrint(response.body());
                output.playlistsByCategory();
            }

        }
    }

    private String findCategoryIdByName(String categoryName, JsonElement allCategories) {
        for (JsonElement item: allCategories.getAsJsonObject().get("categories").getAsJsonObject().getAsJsonArray("items")) {
            if(categoryName.toLowerCase(Locale.ROOT).equals(item.getAsJsonObject().get("name").getAsString().toLowerCase(Locale.ROOT))) {
                return item.getAsJsonObject().get("id").getAsString();
            }
        }
        return "Unknown category name.";
    }
}