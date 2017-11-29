import DatabasePart.DatabaseClient;
import Model.Configure;
import Model.Trainer;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
    private Trainer player;
    private String currentMenu;
    private Scanner scanner;
    private DatabaseClient dbc;

    public Game() {
        player = null;
        currentMenu = "-----";
        Configure.loadConfigure("config.txt");
        scanner = new Scanner(System.in);
        dbc = new DatabaseClient();
    }

    void play() throws Exception {
        dbc.showAllTrainers();
        System.out.println("Welcome to $gamename$\n");

        this.loginMenu();
        while (mainMenu());
    }

    private boolean mainMenu() {
        this.currentMenu = "Main menu";
        int response = promptChoice("What would you like to do?",
                "Catch pokemon", "See catched pokemons", "Exit");
        switch (response) {
            case 0:
                catchMenu();
                break;
            case 1:
                galleryMenu();
                break;
            case 2:
                return false;
        }
        return true;

    }

    private void catchMenu() {
        String str = "Let's go outside. \n" +
                "You hear grass trembling...\n";
        throw new UnsupportedOperationException("Not supported yet.");

    }

    private void galleryMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void loginMenu() {
        this.currentMenu = "Login menu";

        this.player = null;
        do {
            int response = promptChoice("What would you like to do?",
                    "login to existing account", "register new account");
            switch (response) {
                case 0:
                    this.player = authorizeMenu();
                    break;
                case 1:
                    this.player = registerMenu();
                    break;
            }
        } while (this.player == null);
    }

    private Trainer authorizeMenu() {
        this.currentMenu = "Authorize menu";
        String login = getAnswer("Enter login: ");
        String password = getAnswer("Enter password: ");

        Trainer trainer = null;
        try {
            trainer = dbc.authorize(login, password);
            if (trainer == null) {
                System.out.println("There is no player with nickname: " + login);
            }
        } catch (Exception e) {
            System.out.println("Password is wrong. Try again");
//            return authorizeMenu();
        }

        if (trainer != null) {
            System.out.println();
            System.out.println("Hello, " + trainer.getUsername());
            return trainer;
        }
        int response = promptChoice("Would you like to try again?", "try again", "cancel");
        System.out.println();
        if (response == 0)
            return this.authorizeMenu();
        else
            return null;
    }

    private Trainer registerMenu() {
        this.currentMenu = "Register menu";
        System.out.println("We need some information about you");
        String login = getAnswer("nickname: ");
        String password = getAnswer("password: ");
        String fname = getAnswer("First name: ");
        String lname = getAnswer("Last name: ");
        String area = null;
        int response = promptChoice("Enter starting area", "SST", "SHSS", "SENG");
        switch (response) {
            case 0:
                area = "SST";
                break;
            case 1:
                area = "SHSS";
                break;
            case 2:
                area = "SENG";
                break;
        }
        Trainer trainer = new Trainer(login, password, fname, lname, area);
        try {
            dbc.registerUser(trainer);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Some error happened. " + e.getErrorCode());
        }
        return trainer;
    }

    private String getAnswer(String str) {
        System.out.print(str);
        return scanner.nextLine();
    }

    private int promptChoice(String title, String... choices) {
        printChoices(title, choices);
        int choice = -1;
        while (!(0 <= choice && choice < choices.length)) { // will loop until there's a valid age
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Enter value between 0 and " + (choices.length - 1) + ". Try again.");
            }
        }
        return choice;
    }

    private void printChoices(String title, String... choices) {
        System.out.println(title);
        for (int i = 0; i < choices.length; i++) {
            System.out.print("(" + i + ") ");
            System.out.println(choices[i]);
        }
    }
}
