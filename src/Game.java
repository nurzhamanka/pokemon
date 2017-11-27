import DatabasePart.DatabaseClient;
import Model.Trainer;

import java.util.Scanner;

public class Game {
    private Trainer player;
    private String currentMenu;
    private Scanner scanner;
    private DatabaseClient dbc;

    public Game() {
        player = null;
        currentMenu = "-----";
        scanner = new Scanner(System.in);
        dbc = new DatabaseClient();
    }

    public void play() throws Exception {
        dbc.showAllTrainers();
        System.out.println("Welcome to $gamename$\n");

        this.Authorize();
    }

    public boolean isAuthorized() {
        return player != null;
    }

    public void Authorize() throws Exception {
        if (isAuthorized()) {
            throw new Exception("Player is already authorized");
        }
        player = this.AuthorizeMenu();
    }

    public Trainer AuthorizeMenu() {
        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print(  "Enter password: ");
        String password = scanner.nextLine();

        Trainer trainer = null;
        try {
            trainer = dbc.Authorize(login, password);
        } catch (Exception e) {
            System.out.println("Password is wrong. Try again");
            return AuthorizeMenu();
        }
        if (trainer == null) {
            System.out.println("There is no player with nickname:" + login + "\n");
            System.out.println("Would you like to try again or register? Y(es) to try again, N(o) to register");
            String responce = scanner.nextLine();
            if (responce.equals("Yes") || responce.equals("Y"))
                return AuthorizeMenu();
            else
                return this.RegisterMenu();
        }
        System.out.println("Hello, " + trainer.getUsername());
        return trainer;
    }

    public Trainer RegisterMenu() {
        return null;
    }
}
