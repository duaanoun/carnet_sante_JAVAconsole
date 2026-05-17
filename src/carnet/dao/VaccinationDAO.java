package carnet.dao;

import carnet.model.Vaccination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — opérations sur la table Vaccination.
 *
 * Logique métier intégrée :
 *   Si dose == 1 et rappel non renseigné → rappel calculé automatiquement (+6 mois)
 *
 * Le médecin est optionnel (peut être NULL).
 */
public class VaccinationDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

    /**
     * Ajoute une nouvelle vaccination à la base de données.
     * Calcule automatiquement le rappel si dose == 1 et rappel non spécifié.
     *
     * @param v Objet Vaccination à insérer
     * @throws SQLException En cas d'erreur BD
     */
    public void ajouter(Vaccination v) throws SQLException {
        String sql = """
            INSERT INTO Vaccination (NomVaccin, dateVaccin, dose, rappel, id_carnetDeSante, id_medecin)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        // Calcul automatique du rappel : dose 1 → rappel dans 6 mois
        if (v.getRappel() == null && v.getDose() == 1) {
            v.setRappel(v.getDateVaccin().plusMonths(6));
        }

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getNomVaccin());
            stmt.setDate(2, java.sql.Date.valueOf(v.getDateVaccin()));
            stmt.setInt(3, v.getDose());
            stmt.setDate(4, v.getRappel() != null ? java.sql.Date.valueOf(v.getRappel()) : null);
            stmt.setInt(5, v.getIdCarnetDeSante());
            
            // Gestion du médecin (NULL si ID = 0 ou invalide)
            if (v.getIdMedecin() > 0) {
                stmt.setInt(6, v.getIdMedecin());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }

    // ── READ — vaccinations d'un carnet ──────────────────────────────────────

    /**
     * Récupère toutes les vaccinations d'un carnet de santé.
     * Les résultats sont triés par date décroissante (les plus récentes en premier).
     *
     * @param idCarnetDeSante ID du carnet de santé
     * @return Liste des vaccinations
     * @throws SQLException En cas d'erreur BD
     */
    public List<Vaccination> trouverParCarnet(int idCarnetDeSante) throws SQLException {
        String sql = "SELECT * FROM Vaccination WHERE id_carnetDeSante = ? ORDER BY dateVaccin DESC";
        List<Vaccination> liste = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCarnetDeSante);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) liste.add(construire(rs));
        }
        return liste;
    }

    /**
     * Recherche une vaccination par son ID.
     *
     * @param idVaccination ID de la vaccination
     * @return Objet Vaccination ou null si non trouvé
     * @throws SQLException En cas d'erreur BD
     */
    public Vaccination trouverParId(int idVaccination) throws SQLException {
        String sql = "SELECT * FROM Vaccination WHERE id_Vaccination = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVaccination);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return construire(rs);
        }
        return null;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Modifie une vaccination existante.
     *
     * @param v Objet Vaccination avec les modifications
     * @throws SQLException En cas d'erreur BD
     */
    public void modifier(Vaccination v) throws SQLException {
        String sql = """
            UPDATE Vaccination
            SET NomVaccin = ?, dateVaccin = ?, dose = ?, rappel = ?, id_carnetDeSante = ?, id_medecin = ?
            WHERE id_Vaccination = ?
            """;

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getNomVaccin());
            stmt.setDate(2, java.sql.Date.valueOf(v.getDateVaccin()));
            stmt.setInt(3, v.getDose());
            stmt.setDate(4, v.getRappel() != null ? java.sql.Date.valueOf(v.getRappel()) : null);
            stmt.setInt(5, v.getIdCarnetDeSante());
            
            if (v.getIdMedecin() > 0) {
                stmt.setInt(6, v.getIdMedecin());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(7, v.getIdVaccination());

            stmt.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Supprime une vaccination de la base de données.
     *
     * @param idVaccination ID de la vaccination à supprimer
     * @throws SQLException En cas d'erreur BD
     */
    public void supprimer(int idVaccination) throws SQLException {
        String sql = "DELETE FROM Vaccination WHERE id_Vaccination = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVaccination);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

    /**
     * Construit un objet Vaccination à partir d'une ligne ResultSet.
     *
     * @param rs Ligne de résultat SQL
     * @return Objet Vaccination peuplé
     * @throws SQLException En cas d'erreur accès colonne
     */
    private Vaccination construire(ResultSet rs) throws SQLException {
        Vaccination v = new Vaccination();
        v.setIdVaccination(rs.getInt("id_Vaccination"));
        v.setNomVaccin(rs.getString("NomVaccin"));
        v.setDateVaccin(rs.getDate("dateVaccin").toLocalDate());
        v.setDose(rs.getInt("dose"));
        java.sql.Date rappelSQL = rs.getDate("rappel");
        v.setRappel(rappelSQL != null ? rappelSQL.toLocalDate() : null);
        v.setIdCarnetDeSante(rs.getInt("id_carnetDeSante"));
        v.setIdMedecin(rs.getInt("id_medecin"));
        return v;
    }
}
