package DatabasePart;

import Model.Trainer;

import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public class DatabaseClient {

    private Connection conn = null;

    /*
     * In this constructor, connect to the mysql database and exit if it doesn't work
     */
    public DatabaseClient() {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /*
     * Make the connection, pass the exception to the caller
     */
    private Connection getConnection() throws SQLException {
        String username = "pokemon";
        String password = "pokebekthebest";
        String dbms = "mysql";
        String portNumber = "3306";
        String serverName = "localhost";
        String dbname = "PokemonDB";

        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);

        conn = DriverManager.getConnection("jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/" + dbname, connectionProps);
        System.out.println("Connected to database");
        return conn;
    }

    public void showAllTrainers() throws SQLException {

        // Execute a query
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM TRAINER");

        int columnCount = rs.getMetaData().getColumnCount();
        System.out.println("TRAINERS:");
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getMetaData().getColumnLabel(i) + ": " + rs.getObject(i) + ", ");
            }
            System.out.println("");
        }

    }

    public Trainer getTrainer(String username) {
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement("SELECT * FROM TRAINER WHERE TRAINER.username=?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
//            rs.getMetaData();
            if (!rs.next())
                return null;
            return new Trainer(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerUser(Trainer trainer) throws SQLException {
        conn.setAutoCommit(false);

        PreparedStatement trainerCreate = conn.prepareStatement(
                "INSERT INTO TRAINER(username, password, FName, LName, Area_name)\n" +
                "VALUES (?, ?, ?, ?, ?)");
        trainerCreate.setString(1, trainer.getUsername());
        trainerCreate.setString(2, trainer.getPassword());
        trainerCreate.setString(3, trainer.getFirstName());
        trainerCreate.setString(4, trainer.getLastName());
        trainerCreate.setString(5, trainer.getArea());

        try {
            trainerCreate.executeUpdate();
            conn.commit();
            System.out.println("REGISTERED " + trainer.getFirstName() + " " + trainer.getLastName() + " as " + trainer.getUsername());
        } catch (SQLException exc) {
            System.err.println("Transaction is being rolled back");
            exc.printStackTrace();
            conn.rollback();
        } finally {
            trainerCreate.close();
            conn.setAutoCommit(true);
        }

    }

    public Trainer Authorize(String username, String password) throws Exception {
        Trainer trainer = getTrainer(username);
        if (trainer == null) {
            return null;
        }
        if (!password.equals(trainer.getPassword()))
            throw new Exception("Wrong password");
        return trainer;
    }
    
    public String encounterPokemon() throws SQLException {

        try {
            // Execute a query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Name FROM POKEMON ORDER BY RAND() LIMIT 1");
            return rs.getString("Name");

        } catch (SQLException e) {
            return null;
        }

    }

    public void spotPokemon(String name, int aggr, int stam, String area) throws SQLException {

        conn.setAutoCommit(false);

        java.util.Date dt = new java.util.Date();
        Timestamp currentTime = new java.sql.Timestamp(dt.getTime());

        PreparedStatement insertWild = conn.prepareStatement("INSERT INTO PKM_WILD(Date_time, Name, Aggressiveness, Stamina, Area_name) VALUES " +
                                                            "(?, ?, ?, ?, ?)");
        insertWild.setTimestamp(1, currentTime);
        insertWild.setString(2, name);
        insertWild.setInt(3, aggr);
        insertWild.setInt(4, stam);
        insertWild.setString(5, area);

        try {
            insertWild.executeUpdate();
            conn.commit();
            System.out.println("SPOTTED POKEMON " + name + " at " + area);
        } catch (SQLException exc) {
            System.err.println("Transaction is being rolled back");
            exc.printStackTrace();
            conn.rollback();
        } finally {
            insertWild.close();
            conn.setAutoCommit(true);
        }
    }

    public void showTrainers(String area) throws SQLException {

        try {
            // Execute a query
            PreparedStatement stmt = conn.prepareStatement("SELECT concat(FName, ' ', LName) as Name FROM TRAINER WHERE Area_name = ?");
            stmt.setString(1, area);
            ResultSet rs = stmt.executeQuery();

            System.out.println("TRAINERS IN " + area + ":");
            while (rs.next()) {
                System.out.println(rs.getObject("Name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void openPokedex(String name) throws SQLException {

        try {
            // Get a Pokemon's name, description and type
            PreparedStatement pokemon = conn.prepareStatement("SELECT Name, Description, Type FROM POKEMON " +
                    "LEFT JOIN POKEMON_TYPE ON POKEMON.Name = POKEMON_TYPE.Pkm_name " +
                    "WHERE Name = ?");
            pokemon.setString(1, name);
            ResultSet rsPokemon = pokemon.executeQuery();

            PreparedStatement abilities = conn.prepareStatement("SELECT a.Name as aName FROM (POKEMON " +
                                                                "JOIN HAS ON POKEMON.Name = HAS.Pokemon_name) " +
                                                                "JOIN ABILITY a ON Ability_name = a.Name " +
                                                                "WHERE POKEMON.Name = ?");
            abilities.setString(1, name);
            ResultSet rsAbilities = abilities.executeQuery();

            System.out.println("POKEDEX DATA FOR " + name + ":");

            rsPokemon.next();
            System.out.println("Name: " + rsPokemon.getString("Name"));
            System.out.println("Description: " + rsPokemon.getString("Description"));

            System.out.print("Type: ");

            if (rsPokemon.getString("Type") != null)
                System.out.print(rsPokemon.getString("Type"));
            else System.out.print("None");
            while (rsPokemon.next()) {
                System.out.print(" / " + rsPokemon.getString("Type"));
            }
            System.out.println();

            System.out.print("Abilities: ");

            if (rsAbilities.next())
                System.out.print(rsAbilities.getString("aName"));
            else System.out.print("None");
            while (rsAbilities.next()) {
                System.out.print(", " + rsAbilities.getString("aName"));
            }
            System.out.println("\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

}
