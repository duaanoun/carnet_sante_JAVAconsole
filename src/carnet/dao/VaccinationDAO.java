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
 */
public class VaccinationDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

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
            stmt.setDate(2, Date.valueOf(v.getDateVaccin()));
            stmt.setInt(3, v.getDose());
            stmt.setDate(4, v.getRappel() != null ? Date.valueOf(v.getRappel()) : null);
            stmt.setInt(5, v.getIdCarnetDeSante());
            stmt.setInt(6, v.getIdMedecin());

            stmt.executeUpdate();
        }
    }

    // ── READ — vaccinations d'un carnet ──────────────────────────────────────

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

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void supprimer(int idVaccination) throws SQLException {
        String sql = "DELETE FROM Vaccination WHERE id_Vaccination = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVaccination);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

    private Vaccination construire(ResultSet rs) throws SQLException {
        Vaccination v = new Vaccination();
        v.setIdVaccination(rs.getInt("id_Vaccination"));
        v.setNomVaccin(rs.getString("NomVaccin"));
        v.setDateVaccin(rs.getDate("dateVaccin").toLocalDate());
        v.setDose(rs.getInt("dose"));
        Date rappelSQL = rs.getDate("rappel");
        v.setRappel(rappelSQL != null ? rappelSQL.toLocalDate() : null);
        v.setIdCarnetDeSante(rs.getInt("id_carnetDeSante"));
        v.setIdMedecin(rs.getInt("id_medecin"));
        return v;
    }
}
