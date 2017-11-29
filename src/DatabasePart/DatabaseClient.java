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

    private Trainer getTrainer(String username) {
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement("SELECT * FROM TRAINER WHERE TRAINER.username=?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
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

        executor(trainerCreate, true);
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

    public Pokemon generateWildPokemon(String area) {
        String pkmName;

        try {
            // Execute a query
            PreparedStatement stmt = conn.prepareStatement("SELECT Name FROM PKM_WILD WHERE Area_name = ? ORDER BY RAND() LIMIT 1");
            stmt.setString(1, area);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            pkmName = rs.getString("Name");

        } catch (SQLException e) {
            e.printStackTrace();
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

        executor(tamePokemon, true);
    }

    public List<Pokemon> getCaughtPokemon(Trainer trainer) throws SQLException {
        LinkedList<Pokemon> pokemons = new LinkedList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT Name, Nickname\n" +
                "FROM PKM_OWNED, TRAINER\n" +
                "WHERE PKM_OWNED.Trainer_ID = TRAINER.ID AND TRAINER.ID = ?");
        stmt.setInt(1, trainer.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String pkmName = rs.getString("Name");
            String pkmNickname = rs.getString("Nickname");
            Pokemon pkm = new Pokemon(pkmName, trainer, pkmNickname);
            pokemons.add(pkm);
        }

        stmt.close();
        return pokemons;

    }

    public int getCaughtNumber(Trainer trainer) throws SQLException {
        int result = 0;

        PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM PKM_OWNED " +
                "JOIN TRAINER ON PKM_OWNED.Trainer_ID = TRAINER.ID " +
                "WHERE TRAINER.ID = ?");
        stmt.setInt(1, trainer.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            result = rs.getInt(1);
        }

        return result;
    }

    public List<String> listAreas() throws SQLException {
        LinkedList<String> areas = new LinkedList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT Name FROM AREA");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String areaName = rs.getString("Name");
            areas.add(areaName);
        }

        stmt.close();
        return areas;
    }

    public void moveToArea(Trainer trainer, String area) throws SQLException {
        conn.setAutoCommit(false);

        PreparedStatement stmt = conn.prepareStatement("UPDATE TRAINER SET Area_name = ? WHERE ID = ?");
        stmt.setString(1, area);
        stmt.setInt(2, trainer.getId());

        if (executor(stmt, true)) {
            trainer.setArea(area);
        }

    }

    // performs updates or queries
    private boolean executor(PreparedStatement stmt, boolean update) throws SQLException {
        boolean flag;

        try {
            if (update)
                stmt.executeUpdate();
            else
                stmt.executeQuery();
            conn.commit();
            flag = true;
        } catch (SQLException e) {
            System.err.println("Transaction is being rolled back");
            e.printStackTrace();
            conn.rollback();
            flag = false;
        }
        stmt.close();
        conn.setAutoCommit(true);
        return flag;
    }

    ///by particular trainer
    public Pokemon getMostCaughtPokemon(Trainer trainer) throws SQLException {
        // HANDLE TRAINERS WHO HAVE NOT CAUGHT ANY POKEMON
        Pokemon pkm = null;

        PreparedStatement stmt = conn.prepareStatement("SELECT p.Name AS Name, p.Nickname AS Nickname, count(*) AS number\n" +
                "FROM TRAINER t JOIN PKM_OWNED p ON t.ID = p.Trainer_ID\n" +
                "WHERE t.ID = ?\n" +
                "GROUP BY p.Name\n" +
                "ORDER BY number DESC\n" +
                "LIMIT 1");
        stmt.setInt(1, trainer.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
            pkm = new Pokemon(rs.getString("Name"), trainer, rs.getString("Nickname"));

        return pkm;
    }

    ///by particular trainer
    public Pokemon randomNotCaughtPokemon(Trainer trainer) throws SQLException {
        Pokemon pkm = null;

        PreparedStatement stmt = conn.prepareStatement("SELECT p.Name AS Name\n" +
                "FROM PKM_WILD AS p\n" +
                "WHERE p.Name NOT IN (\n" +
                "  SELECT o.Name AS 'Name'\n" +
                "    FROM PKM_OWNED o JOIN TRAINER t ON o.Trainer_ID = t.ID\n" +
                "    WHERE t.ID = ?\n" +
                ")\n" +
                "# ORDER BY RAND()\n" +
                "# LIMIT 1");
        stmt.setInt(1, trainer.getId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next())
            pkm = new Pokemon(rs.getString("Name"), null, null);

        return pkm;
    }

    ///In general, across all trainers
    public Pokemon mostRarePokemon() throws SQLException {

        Pokemon pkm = null;

        PreparedStatement stmt = conn.prepareStatement("SELECT p.Name AS Name, count(*) AS number " +
                "FROM TRAINER t JOIN PKM_OWNED p ON t.ID = p.Trainer_ID " +
                "GROUP BY p.Name " +
                "ORDER BY number ASC " +
                "LIMIT 1");

        ResultSet rs = stmt.executeQuery();

        while (rs.next())
            pkm = new Pokemon(rs.getString("Name"), null, null);

        return pkm;
    }

    public List<String> usersInArea(String area) throws SQLException {
        LinkedList<String> users = new LinkedList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT username FROM TRAINER WHERE Area_name = ?");
        stmt.setString(1, area);

        try {
            // Execute a query
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stmt.close();
        return users;
    }
}
