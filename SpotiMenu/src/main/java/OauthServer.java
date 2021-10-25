import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OauthServer {
    private static final String REDIRECT_URI = "http://localhost:8080";
    private static final String CLIENT_ID = "faac92922c0047dc9274b6acd1708e9f";
    private static final String CLIENT_SECRET = "6245ebe8aff64e67af0d77eba0efdf5d";
    private static final String GRANT_TYPE = "authorization_code";
    public static String SPOTIFY_ACCESS_SERVER_POINT;
    private static OauthServer oauthServerInstance;
    private HttpServer server;
    static boolean authorized = false;
    public static String accessToken;
    HttpContext context;

    private OauthServer() {}

    public static OauthServer getOauthServerInstance() throws IOException {
        if (oauthServerInstance == null) {
            oauthServerInstance = new OauthServer();
            // Start the Oauth server
            oauthServerInstance.startServer();
            // Create the http handler
            oauthServerInstance.createHttpHandler();
        }
        return oauthServerInstance;
    }

    private void startServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.start();
    }

    public void stopServer(){
        server.stop(1);
    }

    private void createHttpHandler() {
        context = server.createContext("/", exchange -> {
            // Getting the response query (contains authentication code or error)
            String query = exchange.getRequestURI().getQuery();
            final String clientMessage;
            if(query != null && query.contains("code")) {
                System.out.println("code received");
                clientMessage = "Got the code. Return back to your program.";
                // Request the access token with the authentication code provided.
                // The code starts in the query one index after the '=' sign.
                requestToken(query.substring(query.indexOf('=') + 1));
            } else {
                clientMessage = "Authorization code not found. Try again.";
            }
            exchange.sendResponseHeaders(200, clientMessage.length());
            exchange.getResponseBody().write(clientMessage.getBytes());
            exchange.getResponseBody().close();
        });
    }
    private void requestToken(String code) {
        System.out.println("making http request for access_token...");
        HttpClient client = HttpClient.newBuilder().build();
        // Setting up a POST request of the form: POST https://accounts.spotify.com/api/token
        // REQUEST BODY PARAMETER VALUE: grant_type = "authorization_code", code (the code returned from query),
        //redirect_uri="http://localhost:8080",
        //Authorization comprised of client_id and client_secret.
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(SPOTIFY_ACCESS_SERVER_POINT + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&grant_type=" + GRANT_TYPE
                                + "&code=" + code
                                + "&redirect_uri=" + REDIRECT_URI
                                +"&scope=user-modify-playback-state"))
                .build();
        // Send the request to https://accounts.spotify.com/api/token
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            authorized = true; // Success
            accessToken = JsonParser.parseString(response.body()).getAsJsonObject().get("access_token").getAsString(); // Parsing the access token
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getCode() {
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=user-modify-playback-state"
                ,SPOTIFY_ACCESS_SERVER_POINT,CLIENT_ID,REDIRECT_URI);
        System.out.println();
        System.out.println("waiting for code...");

    }
    public boolean isAuthorized() {
        return authorized;
    }
}