import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a parser object. the class has methods to provide json
 * parsing and printing functionalities as required in the current stage.
 *
 * @author Ram Elgov
 */
public final class ParseAndPrint {
    // The response from the 'Get https://api.spotify.com/v1/browse/new-releases' request
    public String response;
    private final JsonObject responseJson;

    public ParseAndPrint(String response) {
        this.response = response;
        this.responseJson = JsonParser.parseString(response).getAsJsonObject();
    }

    /**
     * Prints new album and single releases as provided by sending a 'GET' request to the spotify API
     * endpoint: https://api.spotify.com/v1/browse/new-releases
     */
    public void newReleases() {
        List<JsonElement> items = new ArrayList<>();
        JsonArray artistsObjects;
        try {
            JsonObject albums = this.responseJson.get("albums").getAsJsonObject();
            for (JsonElement item: albums.getAsJsonArray("items")) {
                items.add(item);
            }

            for (JsonElement item: items){
                List<String> artists = new ArrayList<>();
                System.out.println(item.getAsJsonObject().get("name").getAsString());
                artistsObjects = item.getAsJsonObject().getAsJsonArray("artists");
                for (JsonElement artistObject: artistsObjects) {
                    artists.add(artistObject.getAsJsonObject().get("name").getAsString());
                }
                System.out.println(artists);
                System.out.println(item.getAsJsonObject().get("external_urls")
                        .getAsJsonObject().get("spotify").getAsString());
                System.out.println();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints a list of categories used to tag items in Spotify (on, for example, the Spotify player’s “Browse” tab),
     * as provided by sending a 'GET' request to the spotify API
     * endpoint: https://api.spotify.com/v1/browse/categories
     */
    public void categories() {
        JsonArray items = responseJson.getAsJsonObject("categories").getAsJsonArray("items");
        for (JsonElement item: items) {
            System.out.println(item.getAsJsonObject().get("name").getAsString());
        }
    }

    /**
     * Prints a list of Spotify featured playlists (shown, for example, on a Spotify player’s ‘Browse’ tab).
     * as provided by sending a 'GET' request to the spotify API
     * endpoint: https://api.spotify.com/v1/browse/featured-playlists
     */

    public void featured() {
        JsonArray items = responseJson.getAsJsonObject("playlists").getAsJsonArray("items");
        for (JsonElement item: items) {
            System.out.println(item.getAsJsonObject().get("name").getAsString());
            System.out.println(item.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify").getAsString());
            System.out.println();
        }
    }

    /**
     * Prints a list of Spotify playlists tagged with a particular category,
     * as provided by sending a 'GET' request to the spotify API
     * endpoint: https://api.spotify.com/v1/browse/categories/{category_id}/playlists
     *  {category_id} is a path parameter that contains the Spotify category ID for the desired category.
     */
    public void playlistsByCategory() {
        JsonArray items = responseJson.getAsJsonObject("playlists").getAsJsonArray("items");
        for (JsonElement item: items) {
            System.out.println(item.getAsJsonObject().get("name").getAsString());
            System.out.println(item.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify").getAsString());
            System.out.println();
        }

    }
}