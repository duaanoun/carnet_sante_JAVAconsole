package carnet.menu;

import carnet.dao.ConsultationDAO;
import carnet.dao.EnfantDAO;
import carnet.dao.VaccinationDAO;
import carnet.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu console — Historique médical complet d'un enfant
 *
 * Charge le CarnetDeSante (vaccinations + consultations)
 * et l'affiche de façon structurée en console.
 */
public class HistoriqueMenu {

    private final Scanner         scanner;
    private final EnfantDAO       enfantDAO       = new EnfantDAO();
    private final VaccinationDAO  vaccinationDAO  = new VaccinationDAO();
    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final int             idParent;

    public HistoriqueMenu(Scanner scanner, int idParent) {
        this.scanner  = scanner;
        this.idParent = idParent;
    }

    public void afficher() {
        try {
            List<Enfant> enfants = enfantDAO.trouverParParent(idParent);
            if (enfants.isEmpty()) {
                System.out.println("Aucun enfant enregistré.");
                return;
            }

            System.out.println("\n── Choisir un enfant pour voir l'historique ──");
            enfants.forEach(e -> System.out.println("  " + e));
            System.out.print("ID de l'enfant : ");

            int id;
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide.");
                return;
            }

            Enfant enfant = enfantDAO.trouverParId(id);
            if (enfant == null) { System.out.println("Enfant introuvable."); return; }

            // Construire le carnet de santé complet
            CarnetDeSante carnet = new CarnetDeSante(enfant.getIdCarnetDeSante(), enfant);
            carnet.setVaccinations(vaccinationDAO.trouverParCarnet(enfant.getIdCarnetDeSante()));
            carnet.setConsultations(consultationDAO.trouverParCarnet(enfant.getIdCarnetDeSante()));

            afficherCarnet(carnet);

        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── Affichage structuré du carnet ─────────────────────────────────────────

    private void afficherCarnet(CarnetDeSante carnet) {
        Enfant e = carnet.getEnfant();

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║             CARNET DE SANTÉ                          ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║  Enfant  : %-41s║%n", e.getPrenom() + " " + e.getNom());
        System.out.printf("║  Né(e)   : %-41s║%n", e.getDateNaissance());
        System.out.printf("║  Sexe    : %-41s║%n", e.getSexe());
        System.out.printf("║  Groupe  : %-41s║%n",
            e.getGroupeSanguin() != null ? e.getGroupeSanguin() : "—");
        System.out.println("╠══════════════════════════════════════════════════════╣");

        // Vaccinations
        System.out.println("║  VACCINATIONS                                        ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        if (carnet.getVaccinations().isEmpty()) {
            System.out.println("║  Aucune vaccination enregistrée.                     ║");
        } else {
            for (Vaccination v : carnet.getVaccinations()) {
                String rappel = v.getRappel() != null ? " → rappel : " + v.getRappel() : "";
                System.out.printf("║  💉 %-48s║%n",
                    v.getNomVaccin() + " | dose " + v.getDose() + " | " + v.getDateVaccin() + rappel);
            }
        }
        System.out.println("╠══════════════════════════════════════════════════════╣");

        // Consultations
        System.out.println("║  CONSULTATIONS                                       ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        if (carnet.getConsultations().isEmpty()) {
            System.out.println("║  Aucune consultation enregistrée.                    ║");
        } else {
            for (Consultation c : carnet.getConsultations()) {
                System.out.printf("║  🩺 %-48s║%n",
                    c.getDateCons().toLocalDate() + " | " + c.getMotifC()
                    + " [" + c.getStatutC() + "]");
            }
        }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
