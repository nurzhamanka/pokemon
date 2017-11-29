import DatabasePart.DatabaseClient;
import Model.Configure;
import Model.Pokemon;
import Model.Trainer;

import java.sql.SQLException;
import java.util.*;

public class Game {
    private Trainer player;
    //    private String currentMenu;
    private Scanner scanner;
    private DatabaseClient dbc;

    public Game() {
        player = null;
//        currentMenu = "-----";
        Configure.loadConfigure("config.txt");
        scanner = new Scanner(System.in);
        dbc = new DatabaseClient();
    }

    void play() throws Exception {
//        dbc.showAllTrainers();
        println("Welcome to $gamename$\n");

        this.loginMenu();
        println("You're in " + this.player.getArea());
        mainMenu();
        println("Bye:)");
    }

    private void loginMenu() {
//        this.currentMenu = "Login menu";

        this.player = null;
        do {
            int response = promptChoice("What would you like to do?",
                    "Login\t\t- enter existing account", "Register\t- create new account");
            switch (response) {
                case 1:
                    this.player = authorizeMenu();
                    break;
                case 2:
                    this.player = registerMenu();
                    break;
            }
        } while (this.player == null);
    }

    private void mainMenu() {
//        this.currentMenu = "Main menu";
        boolean bigLoop = false;
        do {
            int response = promptChoice("What would you like to do?",
                    "Catch\t\t- Catch pokemon", "Pokedex\t\t- See caught pokemons", "Stats\t\t- See some statistics", "Exit");
            switch (response) {
                case 1:
                    println("Let's go outside.");
                    catchMenu();
                    bigLoop = true;
                    break;
                case 2:
                    ownedPokemonMenu();
                    bigLoop = true;
                    break;
                case 3:
                    statsMenu();
                    bigLoop = true;
                    break;
                default:
                    bigLoop = false;
            }
            println("\n");
        } while (bigLoop);

    }

    private void ownedPokemonMenu() {
        try {
            List<Pokemon> pokemons = dbc.getCaughtPokemon(this.player);
            int size = pokemons.size();
            String[] str = new String[size];
            for (int i = 0; i < size; i++) {
                Pokemon poke = pokemons.get(i);
                if (poke.getName().equals(poke.getNickname())) {
                    str[i] = poke.getName();
                } else {
                    str[i] = poke.getNickname() + "\t\t(" + poke.getName() + ")";
                }
            }
            int totalCaughtPokemons = dbc.getCaughtNumber(this.player);
            printChoices("You have caught " + totalCaughtPokemons + " pokemons:", str);

            pause();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statsMenu() {
        println("There is some statistic for you.");
        try {
            Pokemon mostCaught = dbc.getMostCaughtPokemon(this.player);
            Pokemon notCaught = dbc.randomNotCaughtPokemon(this.player);
            Pokemon mostRare = dbc.mostRarePokemon();
            List<String> users = dbc.usersInArea(this.player.getArea());
            users.remove(this.player.getUsername());
            String usersStr = String.join(", ", users);


            println("Most frequently caught pokemon by you: " + mostCaught.getName());
            println("One of pokemons you didn't caught: " + (notCaught == null ? "None" : notCaught.getName()));
            println("And most rare pokemon among all players: " + mostRare.getName());
            println("All players in same Area as you: " + usersStr);
            pause();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Trainer authorizeMenu() {
//        this.currentMenu = "Authorize menu";
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
        int response = promptChoice("Would you like to try again?",
                "Try again\t\t- Enter password again",
                "Cancel\t\t\t- Try another authorization method");
        println();
        if (response == 1)
            return this.authorizeMenu();
        else
            return null;
    }

    private Trainer registerMenu() {
//        this.currentMenu = "Register menu";
        println("We need some information about you");
        String login = getAnswer("nickname: ");
        String password = getAnswer("password: ");
        String fname = getAnswer("First name: ");
        String lname = getAnswer("Last name: ");
        String area = null;
        int response = promptChoice("Enter starting area", "SST", "SHSS", "SENG");
        switch (response) {
            case 1:
                area = "SST";
                break;
            case 2:
                area = "SHSS";
                break;
            case 3:
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

    private void catchMenu() {
        boolean bigLoop = false;
        do {
            String str = "You are in " + this.player.getArea() + ".\n";
            str += "You hear the grass tremble...\n";
            Pokemon pokemon = dbc.generateWildPokemon(this.player.getArea());
            str += "It's a " + pokemon.getName() + "!\n";
            String prompt = "Your actions?";
            println(str);
            boolean smallLoop = false;
            do {
                int response = promptChoice(prompt, "Catch\t\t- Catch him", "Leave\t\t- Search for another pokemon", "Move on\t\t- Move to another area", "Quit");
                switch (response) {
                    case 1:
                        if (tryToCatch(pokemon)) {
                            smallLoop = false;
                            bigLoop = true;
                        } else {
                            smallLoop = true;
                            bigLoop = true;
                        }
                        break;
                    case 2:
                        smallLoop = false;
                        bigLoop = true;
                        break;
                    case 3:
                        moveMenu();
                        smallLoop = false;
                        bigLoop = true;
                        break;
                    default:
                        smallLoop = false;
                        bigLoop = false;
                        break;
                }
            } while (smallLoop);
        } while (bigLoop);
    }

    private boolean tryToCatch(Pokemon pokemon) {
        println("You're trying to catch " + pokemon.getName());
        double st = pokemon.getStamina() / (Configure.maxStamina + .0001);
        double ag = (pokemon.getAggressiveness() + 1.0) / (Configure.maxAggressiveness);
        double successRate = 1 - st * ag;
        double flip = Math.random();

        System.out.print(pokemon.getStamina() + ":" + pokemon.getAggressiveness() + " = ");
        System.out.print(st + "*" + ag + "=");
        System.out.println(successRate + "/" + flip);

        pokemon.decStamina();
        boolean success = flip <= successRate;
        if (!success) {
            println("Pokeball failed you.");
            pause();
            return false;
        }
        println("You successfully caught him");
        String nickname = getAnswer("Would you like to name him? (leave empty if not):\n");
        try {
            dbc.catchWildPokemon(pokemon, this.player, nickname);
        } catch (Exception e) {
//            e.printStackTrace();
            println("Some database error occurred");
            return false;
        }
        pause();
        return true;
    }

    private void moveMenu() {
        try {
            List<String> areas = dbc.listAreas();
            int response = promptChoice("Where you want to go?", areas.toArray(new String[areas.size()]));
            String destination = areas.get(response);
            dbc.moveToArea(this.player, destination);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        println("Press Enter key to continue...");
        try {
            System.in.read();
        } catch (Exception ignored) {
        }
    }

    private String getAnswer(String str) {
        print(str);
        return scanner.nextLine();
    }

    private int promptChoice(String title, String... choices) {
        printChoices(title, choices);
        int choice = -1;
        while (true) { // will loop until there's a valid age
            try {
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);
            } catch (Exception e) {
            }
            if (1 <= choice && choice <= choices.length)
                return choice;
            println("Enter value between 1 and " + (choices.length) + ". Try again.");
        }
    }

    static private void printChoices(String title, String... choices) {
        println(title);
        for (int i = 0; i < choices.length; i++) {
            print("(" + (i + 1) + ") ");
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
