package Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Trainer {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String area;

    public Trainer(int id, String username, String password, String firstName, String lastName, String area) {
        this.setId(id);
        this.setUsername(username);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setArea(area);
    }

    public Trainer(String username, String password, String firstName, String lastName, String area) {
        this(-1, username, password, firstName, lastName, area);
    }

    public Trainer() {
        this(-1, null, null, null, null, null);
    }

    public Trainer(ResultSet rs) throws SQLException {
        id = rs.getInt("ID");
        username = rs.getString("username");
        password = rs.getString("password");
        firstName = rs.getString("FName");
        lastName = rs.getString("LName");
        area = rs.getString("Area_name");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
