import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String spotifyAccessServerPoint = null;
        String spotifyApiServerPoint = null;
        if (args != null && args.length > 1 && "-access".equals(args[0]) && "-resource".equals(args[2])) {
            spotifyAccessServerPoint = args[1];
            spotifyApiServerPoint = args[3];
        }
        OauthServer.SPOTIFY_ACCESS_SERVER_POINT = spotifyAccessServerPoint == null
                ? "https://accounts.spotify.com" : spotifyAccessServerPoint;
        EndPoint.SPOTIFY_API_SERVER_POINT = spotifyApiServerPoint == null
                ? "https://api.spotify.com" : spotifyApiServerPoint;
        Menu menu = new Menu();
        menu.runMenu();
    }
}