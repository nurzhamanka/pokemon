import java.sql.SQLException;

public class PokemonMain {

    public static void main(String[] args) {

        DatabaseClient dbc = new DatabaseClient();

        try {
            dbc.showAllTrainers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            dbc.registerUser("master", "pikachu12", "Ash", "Ketchum", "ash@foo.com", "+77077231275");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
