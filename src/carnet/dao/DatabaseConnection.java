package carnet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connexion MySQL unique partagée par tous les DAO.
 * Pattern Singleton : une seule connexion pour toute l'application.
 */
public class DatabaseConnection {

    // ── À adapter selon votre configuration ─────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/carnetdesante";
    private static final String USER     = "root";
    private static final String PASSWORD = "123456";
    // ────────────────────────────────────────────────────────────────────────

    private static Connection connexion = null;

    private DatabaseConnection() {}

    /** Retourne la connexion active, ou en ouvre une nouvelle. */
    public static Connection getConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            connexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connexion MySQL établie.");
        }
        return connexion;
    }

    /** Ferme proprement la connexion à la fin du programme. */
    public static void fermer() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                System.out.println("[DB] Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erreur fermeture : " + e.getMessage());
        }
    }
}
