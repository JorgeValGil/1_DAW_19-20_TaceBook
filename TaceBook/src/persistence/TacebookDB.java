package persistence;

import java.io.IOException;
import java.io.InputStream;
import model.Profile;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase donde se gardan todos os perfis dos usuarios
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class TacebookDB {

    private static Connection connection = null;

    /**
     * Obtén unha única conexión coa base de datos, abríndoa se é necesario
     *
     * @return Conexión coa base de datos aberta
     * @throws PersistenceException Se se produce un erro ao conectar coa BD
     */
    public static Connection getConnection() throws PersistenceException {
        String db = null, user = null, password = null;

        InputStream input = TacebookDB.class.getClassLoader().getResourceAsStream("resources/db.properties");
        if (input == null) {
            System.out.println("Non se pode ler o ficheiro de propiedades");
        } else {
            try {
                Properties prop = new Properties();
                prop.load(input);
                db = prop.getProperty("db");
                user = prop.getProperty("user");
                password = prop.getProperty("password");
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(TacebookDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Obtemos unha conexión coa base de datos
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(db, user, password);
            }
            return connection;
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.CONECTION_ERROR, e.getMessage());
        }
    }

    /**
     * Método que se usa para cerrar a conexión coa base de datos
     */
    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage() + "Erro ao cerrar a conexión coa base de datos");
        }
    }

    /**
     * ArrayList que almacena todos os perfis dos usuarios
     */
    public static ArrayList<Profile> profiles = new ArrayList<>();

    /**
     *
     * @return
     */
    public static ArrayList<Profile> getProfiles() {
        return profiles;
    }

    /**
     *
     * @param profiles
     */
    public static void setProfiles(ArrayList<Profile> profiles) {
        TacebookDB.profiles = profiles;
    }

}
