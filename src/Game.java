import DatabasePart.DatabaseClient;
import Model.Configure;
import Model.Pokemon;
import Model.Trainer;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Random;
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
        println("Welcome to $gamename$\n");

        this.loginMenu();
        println("You're in " + this.player.getArea());
        while (true) {
            if (!mainMenu())
                break;
        }
        println("Bye:)");
    }

    private boolean mainMenu() {
        this.currentMenu = "Main menu";
        int response = promptChoice("What would you like to do?",
                "Catch pokemon", "See catched pokemons", "Exit");
        switch (response) {
            case 0:
                println("Let's go outside.");
                catchMenu();
                break;
            case 1:
                ownedPokemonMenu();
                break;
            case 2:
                return false;
        }
        return true;

    }

    private void catchMenu() {
        String str = "You hear grass trembling...\n";
        Pokemon pokemon = dbc.generateWildPokemon(this.player.getArea());
        str += "It's a " + pokemon.getName() + "\n";
        String prompt = "Your actions?";
        println(str);
        boolean repeat = true;
        do {
            int response = promptChoice(prompt, "Catch him", "Move on", "Quit");
            if (response == 0) {
                if (tryToCatch(pokemon)) {
                    catchMenu();
                    repeat = false;
                }
            } else if (response == 1) {
                catchMenu();
                return;
            } else if (response == 2) {
                return;
            }
        } while (repeat);
    }

    private boolean tryToCatch(Pokemon pokemon) {
        println("You're trying to catch " + pokemon.getName());
        double st = pokemon.getStamina() / (Configure.maxStamina + .0001);
        double ag = (pokemon.getAggressiveness() + 1.0) / (Configure.maxAggressiveness);
        double successRate = 1 - st * ag;
        double flip = Math.random();

        System.out.print(pokemon.getStamina() + ":" + pokemon.getAggressiveness() + " = " );
        System.out.print(st + "*" + ag + "=");
        System.out.println(successRate + "/" + flip);

        pokemon.decStamina();
        boolean success = flip <= successRate;
        if (!success) {
            println("Pokeball failed you.");
            return false;
        }
        println("You successfully catched him");
        String nickname = getAnswer("Would you like to name him? (leave empty if not):\n");
        try {
            dbc.catchWildPokemon(pokemon, this.player, nickname);
        } catch (Exception e) {
//            e.printStackTrace();
            println("Some database error occurred");
            return false;
        }
        return true;
    }

    private void ownedPokemonMenu() {
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
                println("There is no player with nickname: " + login);
            }
        } catch (Exception e) {
            println("Password is wrong. Try again");
//            return authorizeMenu();
        }

        if (trainer != null) {
            println();
            println("Hello, " + trainer.getUsername());
            return trainer;
        }
        int response = promptChoice("Would you like to try again?", "try again", "cancel");
        println();
        if (response == 0)
            return this.authorizeMenu();
        else
            return null;
    }

    private Trainer registerMenu() {
        this.currentMenu = "Register menu";
        println("We need some information about you");
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
            println("Some error happened. " + e.getErrorCode());
        }
        return trainer;
    }

    private String getAnswer(String str) {
        print(str);
        return scanner.nextLine();
    }

    private int promptChoice(String title, String... choices) {
        printChoices(title, choices);
        int choice = -1;
        while (!(0 <= choice && choice < choices.length)) { // will loop until there's a valid age
            try {
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                println("Enter value between 0 and " + (choices.length - 1) + ". Try again.");
            }
        }
        return choice;
    }

    static private void printChoices(String title, String... choices) {
        println(title);
        for (int i = 0; i < choices.length; i++) {
            print("(" + i + ") ");
            println(choices[i]);
        }
    }

    static private void print(String string) {
        System.out.print(string);
    }

    static private void println(String string) {
        print(string + "\n");
    }

    static private void println() {
        println("");
    }
}
