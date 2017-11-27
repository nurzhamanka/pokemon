import java.util.Scanner;

public class GameManager {

    private Scanner in = new Scanner(System.in);


    public GameManager() {
    }

    public void printChoices(String... choices) {

        int i = 0;

        for (String c : choices) {
            System.out.print(i + ": ");
            System.out.println(c);
        }
    }
}
