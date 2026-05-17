package carnet.dao;

import carnet.model.Medecin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — opérations CRUD sur la table Medecin.
 *
 * Responsabilités :
 *   - Authentification médecin (login/password)
 *   - Gestion CRUD des médecins (admin)
 *   - Récupération des données médecin
 *
 * Méthodes :
 *   authentifier()      → SELECT ... WHERE email = ? (vérification password)
 *   trouverParEmail()   → SELECT ... WHERE email = ?
 *   trouverParId()      → SELECT ... WHERE id_medecin = ?
 *   ajouter()           → INSERT
 *   modifier()          → UPDATE
 *   supprimer()         → DELETE
 *   trouverTous()       → SELECT * (pour admin)
 */
public class MedecinDAO {

    // ── AUTHENTIFICATION ─────────────────────────────────────────────────────

    /**
     * Authentifie un médecin par email et mot de passe.
     * Utilise une comparaison simple (en production, utiliser bcrypt ou argon2).
     *
     * @param email    Email du médecin
     * @param password Mot de passe en clair (à hasher en production)
     * @return Objet Medecin si authentification réussie, null sinon
     * @throws SQLException En cas d'erreur BD
     */
    public Medecin authentifier(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Medecin WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);  // En production : comparer avec hash
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construire(rs);
            }
        }
        return null;
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    /**
     * Recherche un médecin par son email.
     *
     * @param email Email du médecin
     * @return Objet Medecin ou null si non trouvé
     * @throws SQLException En cas d'erreur BD
     */
    public Medecin trouverParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Medecin WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construire(rs);
            }
        }
        return null;
    }

    /**
     * Recherche un médecin par son ID.
     *
     * @param idMedecin ID du médecin
     * @return Objet Medecin ou null si non trouvé
     * @throws SQLException En cas d'erreur BD
     */
    public Medecin trouverParId(int idMedecin) throws SQLException {
        String sql = "SELECT * FROM Medecin WHERE id_medecin = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMedecin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construire(rs);
            }
        }
        return null;
    }

    /**
     * Récupère tous les médecins (pour interface admin).
     *
     * @return Liste de tous les médecins
     * @throws SQLException En cas d'erreur BD
     */
    public List<Medecin> trouverTous() throws SQLException {
        String sql = "SELECT * FROM Medecin ORDER BY nom, prenom";
        List<Medecin> liste = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(construire(rs));
            }
        }
        return liste;
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Ajoute un nouveau médecin à la base de données.
     *
     * @param m Objet Medecin à insérer
     * @throws SQLException En cas d'erreur BD
     */
    public void ajouter(Medecin m) throws SQLException {
        String sql = """
            INSERT INTO Medecin (nom, prenom, specialite, telephone, cabinet, email, password)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getPrenom());
            stmt.setString(3, m.getSpecialite());
            stmt.setString(4, m.getTelephone());
            stmt.setString(5, m.getCabinet());
            stmt.setString(6, m.getEmail());
            stmt.setString(7, m.getPassword());

            stmt.executeUpdate();

            // Récupère l'ID auto-généré
            ResultSet cles = stmt.getGeneratedKeys();
            if (cles.next()) {
                m.setIdMedecin(cles.getInt(1));
            }
        }
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Modifie les données d'un médecin existant.
     *
     * @param m Objet Medecin avec les modifications
     * @throws SQLException En cas d'erreur BD
     */
    public void modifier(Medecin m) throws SQLException {
        String sql = """
            UPDATE Medecin
            SET nom = ?, prenom = ?, specialite = ?, telephone = ?, cabinet = ?, email = ?, password = ?
            WHERE id_medecin = ?
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getPrenom());
            stmt.setString(3, m.getSpecialite());
            stmt.setString(4, m.getTelephone());
            stmt.setString(5, m.getCabinet());
            stmt.setString(6, m.getEmail());
            stmt.setString(7, m.getPassword());
            stmt.setInt(8, m.getIdMedecin());

            stmt.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Supprime un médecin de la base de données.
     *
     * @param idMedecin ID du médecin à supprimer
     * @throws SQLException En cas d'erreur BD
     */
    public void supprimer(int idMedecin) throws SQLException {
        String sql = "DELETE FROM Medecin WHERE id_medecin = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMedecin);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

    /**
     * Construit un objet Medecin à partir d'une ligne ResultSet.
     *
     * @param rs Ligne de résultat SQL
     * @return Objet Medecin peuplé
     * @throws SQLException En cas d'erreur accès colonne
     */
    private Medecin construire(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();
        m.setIdMedecin(rs.getInt("id_medecin"));
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setSpecialite(rs.getString("specialite"));
        m.setTelephone(rs.getString("telephone"));
        m.setCabinet(rs.getString("cabinet"));
        m.setEmail(rs.getString("email"));
        m.setPassword(rs.getString("password"));
        return m;
    }
}
