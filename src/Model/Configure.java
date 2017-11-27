package Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configure {
    static public String username;
    static public String password;
    static public String dbms;
    static public String portNumber;
    static public String serverName;
    static public String dbname;

    static public void loadConfigure(String filename) {
        username = null;
        password = null;
        dbms = null;
        portNumber = null;
        serverName = null;
        dbname = null;
        try {
            Properties properties = new Properties();
            InputStream in;
            in = new FileInputStream(filename);
            properties.load(in);
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            dbms = properties.getProperty("dbms");
            portNumber = properties.getProperty("portNumber");
            serverName = properties.getProperty("serverName");
            dbname = properties.getProperty("dbname");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
