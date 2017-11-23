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
            dbc.registerUser("pinnacle", "paswud345", "Lera", "Rudikova", "lera@foo.com", "+77077681275", "SHSS");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
