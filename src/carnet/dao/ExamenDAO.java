package carnet.dao;

import carnet.model.Examen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — opérations CRUD sur la table Examen.
 *
 * Responsabilités :
 *   - Gestion des examens médicaux (analyses, radiographies, etc.)
 *   - Liaison avec le carnet de santé et le médecin
 *
 * Méthodes :
 *   ajouter()           → INSERT
 *   trouverParCarnet()  → SELECT ... WHERE id_carnetDeSante = ?
 *   trouverParId()      → SELECT ... WHERE id_examen = ?
 *   modifier()          → UPDATE
 *   supprimer()         → DELETE
 */
public class ExamenDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Ajoute un nouvel examen à la base de données.
     *
     * @param e Objet Examen à insérer
     * @throws SQLException En cas d'erreur BD
     */
    public void ajouter(Examen e) throws SQLException {
        String sql = """
            INSERT INTO Examen (dateExamen, details, resultat, id_carnetDeSante, id_medecin)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, java.sql.Date.valueOf(e.getDateExamen()));
            stmt.setString(2, e.getDetails());
            stmt.setString(3, e.getResultat());
            stmt.setInt(4, e.getIdCarnetDeSante());
            
            // Gestion du médecin (NULL si ID = 0 ou invalide)
            if (e.getIdMedecin() > 0) {
                stmt.setInt(5, e.getIdMedecin());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            // Récupère l'ID auto-généré
            ResultSet cles = stmt.getGeneratedKeys();
            if (cles.next()) {
                e.setIdExamen(cles.getInt(1));
            }
        }
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    /**
     * Récupère tous les examens d'un carnet de santé.
     *
     * @param idCarnetDeSante ID du carnet de santé
     * @return Liste des examens, triés par date décroissante
     * @throws SQLException En cas d'erreur BD
     */
    public List<Examen> trouverParCarnet(int idCarnetDeSante) throws SQLException {
        String sql = "SELECT * FROM Examen WHERE id_carnetDeSante = ? ORDER BY dateExamen DESC";
        List<Examen> liste = new ArrayList<>();

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
     * Recherche un examen par son ID.
     *
     * @param idExamen ID de l'examen
     * @return Objet Examen ou null si non trouvé
     * @throws SQLException En cas d'erreur BD
     */
    public Examen trouverParId(int idExamen) throws SQLException {
        String sql = "SELECT * FROM Examen WHERE id_examen = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construire(rs);
            }
        }
        return null;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Modifie les données d'un examen existant.
     *
     * @param e Objet Examen avec les modifications
     * @throws SQLException En cas d'erreur BD
     */
    public void modifier(Examen e) throws SQLException {
        String sql = """
            UPDATE Examen
            SET dateExamen = ?, details = ?, resultat = ?, id_carnetDeSante = ?, id_medecin = ?
            WHERE id_examen = ?
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(e.getDateExamen()));
            stmt.setString(2, e.getDetails());
            stmt.setString(3, e.getResultat());
            stmt.setInt(4, e.getIdCarnetDeSante());
            
            if (e.getIdMedecin() > 0) {
                stmt.setInt(5, e.getIdMedecin());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(6, e.getIdExamen());

            stmt.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Supprime un examen de la base de données.
     *
     * @param idExamen ID de l'examen à supprimer
     * @throws SQLException En cas d'erreur BD
     */
    public void supprimer(int idExamen) throws SQLException {
        String sql = "DELETE FROM Examen WHERE id_examen = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

    /**
     * Construit un objet Examen à partir d'une ligne ResultSet.
     *
     * @param rs Ligne de résultat SQL
     * @return Objet Examen peuplé
     * @throws SQLException En cas d'erreur accès colonne
     */
    private Examen construire(ResultSet rs) throws SQLException {
        Examen e = new Examen();
        e.setIdExamen(rs.getInt("id_examen"));
        e.setDateExamen(rs.getDate("dateExamen").toLocalDate());
        e.setDetails(rs.getString("details"));
        e.setResultat(rs.getString("resultat"));
        e.setIdCarnetDeSante(rs.getInt("id_carnetDeSante"));
        e.setIdMedecin(rs.getInt("id_medecin"));
        return e;
    }
}
