package carnet.dao;

import carnet.model.Consultation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — opérations sur la table Consultation.
 *
 * statutC ENUM : 'planifiee' | 'realisee' | 'annulee'
 */
public class ConsultationDAO {

    // ── CREATE ───────────────────────────────────────────────────────────────

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
            stmt.setInt(5, c.getIdMedecin());

            stmt.executeUpdate();
        }
    }

    // ── READ — consultations d'un carnet ─────────────────────────────────────

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

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void supprimer(int idConsultation) throws SQLException {
        String sql = "DELETE FROM Consultation WHERE id_consultation = ?";

        try (Connection conn = DatabaseConnection.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConsultation);
            stmt.executeUpdate();
        }
    }

    // ── Méthode privée ────────────────────────────────────────────────────────

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
