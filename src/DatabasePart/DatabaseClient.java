package DatabasePart;

import java.sql.*;
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

        try {
            // Execute a query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TRAINER");

            int columnCount = rs.getMetaData().getColumnCount();
            System.out.println("TRAINERS:");
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Thread.sleep(200);
                    System.out.print(rs.getMetaData().getColumnLabel(i) + ": " + rs.getObject(i) + ", ");
                }
                System.out.println("");
            }

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void registerUser(String username, String password, String fName, String lName, String area) throws SQLException {

        conn.setAutoCommit(false);

        PreparedStatement trainerCreate = conn.prepareStatement("INSERT INTO TRAINER(username, password, FName, LName, Area_name)\n" +
                "VALUES (?, ?, ?, ?, ?)");
        trainerCreate.setString(1, username);
        trainerCreate.setString(2, password);
        trainerCreate.setString(3, fName);
        trainerCreate.setString(4, lName);
        trainerCreate.setString(5, area);

        try {
            trainerCreate.executeUpdate();
            conn.commit();
            System.out.println("REGISTERED " + fName + " " + lName + " as " + username);
        } catch (SQLException exc) {
            System.err.println("Transaction is being rolled back");
            exc.printStackTrace();
            conn.rollback();
        }

    }


}
