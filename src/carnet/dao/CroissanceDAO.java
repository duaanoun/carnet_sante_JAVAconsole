package carnet.dao;

import carnet.model.Croissance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — opérations CRUD sur la table Croissance.
 *
 * Responsabilités :
 *   - Gestion du suivi de croissance (taille, poids, IMC)
 *   - Liaison avec le carnet de santé et le médecin
 *
 * Méthodes :
 *   ajouter()           → INSERT
 *   trouverParCarnet()  → SELECT ... WHERE id_carnetDeSante = ? ORDER BY dateMesure DESC
 *   trouverParId()      → SELECT ... WHERE id_croissance = ?
 *   modifier()          → UPDATE
 *   supprimer()         → DELETE
 */
public class CroissanceDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Ajoute une nouvelle mesure de croissance à la base de données.
     *
     * @param c Objet Croissance à insérer
     * @throws SQLException En cas d'erreur BD
     */
    public void ajouter(Croissance c) throws SQLException {
        String sql = """
            INSERT INTO Croissance (dateMesure, taille, poids, id_carnetDeSante, id_medecin)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, java.sql.Date.valueOf(c.getDateMesure()));
            stmt.setFloat(2, c.getTaille());
            stmt.setFloat(3, c.getPoids());
            stmt.setInt(4, c.getIdCarnetDeSante());
            
            // Gestion du médecin (NULL si ID = 0 ou invalide)
            if (c.getIdMedecin() > 0) {
                stmt.setInt(5, c.getIdMedecin());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            // Récupère l'ID auto-généré
            ResultSet cles = stmt.getGeneratedKeys();
            if (cles.next()) {
                c.setIdCroissance(cles.getInt(1));
            }
        }
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    /**
     * Récupère toutes les mesures de croissance d'un carnet de santé.
     * Les résultats sont triés par date décroissante (les plus récentes en premier).
     *
     * @param idCarnetDeSante ID du carnet de santé
     * @return Liste des mesures de croissance
     * @throws SQLException En cas d'erreur BD
     */
    public List<Croissance> trouverParCarnet(int idCarnetDeSante) throws SQLException {
        String sql = "SELECT * FROM Croissance WHERE id_carnetDeSante = ? ORDER BY dateMesure DESC";
        List<Croissance> liste = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCarnetDeSante);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(construire(rs));
            }
        }
        return liste;
    }

    /**
     * Recherche une mesure de croissance par son ID.
     *
     * @param idCroissance ID de la croissance
     * @return Objet Croissance ou null si non trouvé
     * @throws SQLException En cas d'erreur BD
     */
    public Croissance trouverParId(int idCroissance) throws SQLException {
        String sql = "SELECT * FROM Croissance WHERE id_croissance = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCroissance);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construire(rs);
            }
        }
        return null;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Modifie une mesure de croissance existante.
     *
     * @param c Objet Croissance avec les modifications
     * @throws SQLException En cas d'erreur BD
     */
    public void modifier(Croissance c) throws SQLException {
        String sql = """
            UPDATE Croissance
            SET dateMesure = ?, taille = ?, poids = ?, id_carnetDeSante = ?, id_medecin = ?
            WHERE id_croissance = ?
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(c.getDateMesure()));
            stmt.setFloat(2, c.getTaille());
            stmt.setFloat(3, c.getPoids());
            stmt.setInt(4, c.getIdCarnetDeSante());
            
            if (c.getIdMedecin() > 0) {
                stmt.setInt(5, c.getIdMedecin());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(6, c.getIdCroissance());

            stmt.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Supprime une mesure de croissance de la base de données.
     *
     * @param idCroissance ID de la croissance à supprimer
     * @throws SQLException En cas d'erreur BD
     */
    public void supprimer(int idCroissance) throws SQLException {
        String sql = "DELETE FROM Croissance WHERE id_croissance = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCroissance);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

    /**
     * Construit un objet Croissance à partir d'une ligne ResultSet.
     *
     * @param rs Ligne de résultat SQL
     * @return Objet Croissance peuplé
     * @throws SQLException En cas d'erreur accès colonne
     */
    private Croissance construire(ResultSet rs) throws SQLException {
        Croissance c = new Croissance();
        c.setIdCroissance(rs.getInt("id_croissance"));
        c.setDateMesure(rs.getDate("dateMesure").toLocalDate());
        c.setTaille(rs.getFloat("taille"));
        c.setPoids(rs.getFloat("poids"));
        c.setIdCarnetDeSante(rs.getInt("id_carnetDeSante"));
        c.setIdMedecin(rs.getInt("id_medecin"));
        return c;
    }
}
