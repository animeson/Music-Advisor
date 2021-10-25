import java.io.IOException;
import java.util.Scanner;

public class Menu {

    private final Scanner input;
    private String userChoice;


    Menu() {
        input = new Scanner(System.in);
    }

    public void runMenu() throws IOException, InterruptedException {
        while (true) {
            System.out.println("""
                    1. new
                    2. featured
                    3. categories
                    4. playlists (name)
                    5. exit
                    """);
            userChoice = input.nextLine();
            EndPoint endPoint = new EndPoint();

            if (!OauthServer.authorized) {
                getAuthorization();
                continue;
            }
            if (userChoice != null && userChoice.contains("playlists")) {
                // 10 is the index where the C_NAME should start
                endPoint.playlistsByCategoryName(userChoice.substring(10));
            } else {
                if (userChoice != null) {
                    switch (userChoice) {
                        case ("new") -> endPoint.newReleases();
                        case ("featured") -> endPoint.featured();
                        case ("categories") -> endPoint.categories(true);
                        case ("exit") -> exitApp();
                    }
                }
            }
        }
    }

    private void getAuthorization() throws IOException {
        if (userChoice.equals("exit")) {
            exitApp();
        }
        else if (userChoice.equals("auth")) {
            OauthServer oauthServer = OauthServer.getOauthServerInstance();
            oauthServer.getCode();
            while (!oauthServer.isAuthorized()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (oauthServer.isAuthorized()) {
                    oauthServer.stopServer();
                    System.out.println("Success!");
                    break;
                }
            }
        } else {
            System.out.println("""
                    Please, provide access for application.
                    use command "auth"
                    """);
        }
    }

    private void exitApp() {
        System.out.println("---GOODBYE!---");
        System.exit(0);
    }
}