package carnet.dao;

import carnet.model.Consultation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — opérations sur la table Consultation.
 *
 * statutC ENUM : 'planifiee' | 'realisee' | 'annulee'
 *
 * Gestion des consultations médicales liées à un carnet de santé.
 * Le médecin est optionnel (peut être NULL).
 */
public class ConsultationDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Ajoute une nouvelle consultation à la base de données.
     * Le statut par défaut est 'planifiee' si non spécifié.
     *
     * @param c Objet Consultation à insérer
     * @throws SQLException En cas d'erreur BD
     */
    public void ajouter(Consultation c) throws SQLException {
        String sql = """
            INSERT INTO Consultation (date_Cons, motifC, statutC, id_carnetDeSante, id_medecin)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(c.getDateCons()));
            stmt.setString(2, c.getMotifC());
            stmt.setString(3, c.getStatutC() != null ? c.getStatutC() : "planifiee");
            stmt.setInt(4, c.getIdCarnetDeSante());
            
            // Gestion du médecin (NULL si ID = 0 ou invalide)
            if (c.getIdMedecin() > 0) {
                stmt.setInt(5, c.getIdMedecin());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }

    // ── READ — consultations d'un carnet ─────────────────────────────────────

    /**
     * Récupère toutes les consultations d'un carnet de santé.
     * Les résultats sont triés par date décroissante (les plus récentes en premier).
     *
     * @param idCarnetDeSante ID du carnet de santé
     * @return Liste des consultations
     * @throws SQLException En cas d'erreur BD
     */
    public List<Consultation> trouverParCarnet(int idCarnetDeSante) throws SQLException {
        String sql = "SELECT * FROM Consultation WHERE id_carnetDeSante = ? ORDER BY date_Cons DESC";
        List<Consultation> liste = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCarnetDeSante);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) liste.add(construire(rs));
        }
        return liste;
    }

    /**
     * Recherche une consultation par son ID.
     *
     * @param idConsultation ID de la consultation
     * @return Objet Consultation ou null si non trouvé
     * @throws SQLException En cas d'erreur BD
     */
    public Consultation trouverParId(int idConsultation) throws SQLException {
        String sql = "SELECT * FROM Consultation WHERE id_consultation = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConsultation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return construire(rs);
        }
        return null;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Modifie une consultation existante.
     *
     * @param c Objet Consultation avec les modifications
     * @throws SQLException En cas d'erreur BD
     */
    public void modifier(Consultation c) throws SQLException {
        String sql = """
            UPDATE Consultation
            SET date_Cons = ?, motifC = ?, statutC = ?, id_carnetDeSante = ?, id_medecin = ?
            WHERE id_consultation = ?
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(c.getDateCons()));
            stmt.setString(2, c.getMotifC());
            stmt.setString(3, c.getStatutC() != null ? c.getStatutC() : "planifiee");
            stmt.setInt(4, c.getIdCarnetDeSante());
            
            if (c.getIdMedecin() > 0) {
                stmt.setInt(5, c.getIdMedecin());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(6, c.getIdConsultation());

            stmt.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Supprime une consultation de la base de données.
     *
     * @param idConsultation ID de la consultation à supprimer
     * @throws SQLException En cas d'erreur BD
     */
    public void supprimer(int idConsultation) throws SQLException {
        String sql = "DELETE FROM Consultation WHERE id_consultation = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConsultation);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

    /**
     * Construit un objet Consultation à partir d'une ligne ResultSet.
     *
     * @param rs Ligne de résultat SQL
     * @return Objet Consultation peuplé
     * @throws SQLException En cas d'erreur accès colonne
     */
    private Consultation construire(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setIdConsultation(rs.getInt("id_consultation"));
        c.setDateCons(rs.getTimestamp("date_Cons").toLocalDateTime());
        c.setMotifC(rs.getString("motifC"));
        c.setStatutC(rs.getString("statutC"));
        c.setIdCarnetDeSante(rs.getInt("id_carnetDeSante"));
        c.setIdMedecin(rs.getInt("id_medecin"));
        return c;
    }
}
