package DatabasePart;

import Model.Configure;
import Model.Pokemon;
import Model.Trainer;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import java.util.concurrent.ThreadLocalRandom;

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

        Properties connectionProps = new Properties();
        connectionProps.put("user", Configure.username);
        connectionProps.put("password", Configure.password);
        String url = "jdbc:" + Configure.dbms + "://" + Configure.serverName + ":" + Configure.portNumber + "/" + Configure.dbname;
        conn = DriverManager.getConnection(url, connectionProps);
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

    public Trainer authorize(String username, String password) throws Exception {
        Trainer trainer = getTrainer(username);
        if (trainer == null) {
            return null;
        }
        if (!password.equals(trainer.getPassword()))
            throw new Exception("Wrong password");
        return trainer;
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
    
    public Pokemon generateWildPokemon(String area) {

        String pkmName;

        try {
            // Execute a query
            PreparedStatement stmt = conn.prepareStatement("SELECT Name FROM PKM_WILD WHERE Area_name = ? ORDER BY RAND() LIMIT 1");
            stmt.setString(1, area);
            ResultSet rs = stmt.executeQuery();
//            if (rs.getFetchSize() == 0)
//                return null;
            rs.next();
            pkmName = rs.getString("Name");

        } catch (SQLException e) {
            e.printStackTrace();
//            System.out.println();
            return null;
        }

        int aggr = ThreadLocalRandom.current().nextInt(1, Configure.maxAggressiveness + 1);
        int stam = ThreadLocalRandom.current().nextInt(1, Configure.maxStamina + 1);

        return new Pokemon(pkmName, aggr, stam, area);
    }

    public void catchWildPokemon(Pokemon pokemon, Trainer trainer, String nickname) throws Exception {

        conn.setAutoCommit(false);

        if (pokemon.getTrainer() != null) {
            throw new Exception("Error taming Pokemon");
        }
        if (nickname == null || nickname.isEmpty()) {
            nickname = pokemon.getName();
        }
        pokemon.setTrainer(trainer);
        pokemon.setNickname(nickname);

        PreparedStatement tamePokemon = conn.prepareStatement("INSERT INTO PKM_OWNED(Name, Trainer_ID, Nickname) " +
                "VALUES (?, ?, ?)");
        tamePokemon.setString(1, pokemon.getName());
        tamePokemon.setInt(2, trainer.getId());
        tamePokemon.setString(3, nickname);

        try {
            tamePokemon.executeUpdate();
            conn.commit();
            // System.out.println("TAMED " + pokemon.getName() + " at " + pokemon.getArea());
        } catch (SQLException e) {
            System.err.println("Transaction is being rolled back");
            e.printStackTrace();
            conn.rollback();
        } finally {
            tamePokemon.close();
            conn.setAutoCommit(true);
        }
    }

    public List<Pokemon> getCatchedPokemon(Trainer trainer) throws SQLException {

        LinkedList<Pokemon> pokemons = new LinkedList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT Name, Nickname FROM PKM_OWNED " +
                                                        "JOIN TRAINER ON PKM_OWNED.Trainer_ID = TRAINER.ID " +
                                                        "WHERE ID = ?");
        stmt.setInt(1, trainer.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String pkmName = rs.getString("Name");
            String pkmNickname = rs.getString("Nickname");
            Pokemon pkm = new Pokemon(pkmName, trainer, pkmNickname);
            pokemons.add(pkm);
        }

        return pokemons;

    }

    public int getCatchedNumber(Trainer trainer) throws SQLException {

        int result = 0;

        // Execute a query
        PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM PKM_OWNED " +
                "JOIN TRAINER ON PKM_OWNED.Trainer_ID = TRAINER.ID " +
                "WHERE ID = ?");
        stmt.setInt(1, trainer.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            result = rs.getInt(1);
        }

        return result;
    }


}
