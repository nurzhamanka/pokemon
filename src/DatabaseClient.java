import java.sql.*;
import java.util.Properties;

public class DatabaseClient {

    private String userName = "root";
    private String password = "Peridot312";
    private String dbms = "mysql";
    private String serverName = "localhost";
    private String portNumber = "3306";
    private String dbname = "PokemonDB";

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
    public Connection getConnection() throws SQLException {

        conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        if (this.dbms.equals("mysql")) {
            conn = DriverManager.getConnection(
                "jdbc:" + this.dbms + "://" +
                        this.serverName +
                        ":" + this.portNumber + "/" + this.dbname,
                connectionProps);
        }
        System.out.println("Connected to database");
        return conn;
    }

    public void showAllTrainers() throws SQLException {

        try {
            // Execute a query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TRAINER JOIN USER_DATA ON TRAINER_ID=ID");

            int columnCount = rs.getMetaData().getColumnCount();
            System.out.println("TRAINERS:");
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Thread.sleep(200);
                    System.out.print(rs.getMetaData().getColumnLabel(i) + ": " + rs.getObject(i) + ", ");
                }
                System.out.println("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void registerUser(String username, String password, String fName, String lName, String email, String phone, String area) throws SQLException {

        conn.setAutoCommit(false);
        PreparedStatement userCreate = conn.prepareStatement("INSERT INTO USER_DATA(Trainer_ID, Username, Password) "  +
                                                             "VALUES (null, ?, ?)");
        userCreate.setString(1, username);
        userCreate.setString(2, password);

        PreparedStatement trainerCreate = conn.prepareStatement("INSERT INTO TRAINER(ID, FName, LName, Email, Phone, Area_name) " +
                                                                "VALUES ((select Trainer_ID from USER_DATA where Username = ?), ?, ?, ?, ?, ?)");
        trainerCreate.setString(1, username);
        trainerCreate.setString(2, fName);
        trainerCreate.setString(3, lName);
        trainerCreate.setString(4, email);
        trainerCreate.setString(5, phone);
        trainerCreate.setString(6, area);

        try {
            userCreate.executeUpdate();
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
