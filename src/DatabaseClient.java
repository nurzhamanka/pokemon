//package DatabasePart;

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


}
