package carnet.menu;

import carnet.dao.ConsultationDAO;
import carnet.dao.EnfantDAO;
import carnet.model.Consultation;
import carnet.model.Enfant;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu console — Gestion des consultations
 * Fonctions : voir, ajouter, supprimer
 *
 * statutC ENUM : 'planifiee' | 'realisee' | 'annulee'
 */
public class ConsultationMenu {

    private final Scanner         scanner;
    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final EnfantDAO       enfantDAO       = new EnfantDAO();
    private final int             idParent;

    public ConsultationMenu(Scanner scanner, int idParent) {
        this.scanner  = scanner;
        this.idParent = idParent;
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║   GESTION DES CONSULTATIONS  ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Voir les consultations    ║");
            System.out.println("║  2. Ajouter une consultation  ║");
            System.out.println("║  3. Supprimer une consultation║");
            System.out.println("║  0. Retour                    ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> voirConsultations();
                case "2" -> ajouterConsultation();
                case "3" -> supprimerConsultation();
                case "0" -> continuer = false;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── 1. Voir ───────────────────────────────────────────────────────────────

    private void voirConsultations() {
        Enfant enfant = choisirEnfant();
        if (enfant == null) return;

        try {
            List<Consultation> liste = consultationDAO.trouverParCarnet(enfant.getIdCarnetDeSante());
            if (liste.isEmpty()) {
                System.out.println("  Aucune consultation enregistrée.");
                return;
            }
            System.out.println("\n── Consultations de " + enfant.getPrenom() + " ────────────");
            liste.forEach(c -> System.out.println("  " + c));
            System.out.println("────────────────────────────────────────────────");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── 2. Ajouter ────────────────────────────────────────────────────────────

    private void ajouterConsultation() {
        Enfant enfant = choisirEnfant();
        if (enfant == null) return;

        System.out.println("\n── Ajouter une consultation ──");

        LocalDate date = demanderDate("Date (AAAA-MM-JJ) : ");
        if (date == null) return;

        System.out.print("Motif             : ");
        String motif = scanner.nextLine().trim();
        if (motif.isEmpty()) { System.out.println("Le motif est obligatoire."); return; }

        System.out.println("Statut :");
        System.out.println("  1. planifiee (défaut)");
        System.out.println("  2. realisee");
        System.out.println("  3. annulee");
        System.out.print("Votre choix [1] : ");
        String choixStatut = scanner.nextLine().trim();
        String statut = switch (choixStatut) {
            case "2" -> "realisee";
            case "3" -> "annulee";
            default  -> "planifiee";
        };

        Consultation c = new Consultation();
        c.setIdCarnetDeSante(enfant.getIdCarnetDeSante());
        c.setDateCons(date.atStartOfDay()); // DATETIME → minuit par défaut
        c.setMotifC(motif);
        c.setStatutC(statut);
        c.setIdMedecin(0);

        try {
            consultationDAO.ajouter(c);
            System.out.println("✔ Consultation planifiée [" + statut + "].");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── 3. Supprimer ──────────────────────────────────────────────────────────

    private void supprimerConsultation() {
        Enfant enfant = choisirEnfant();
        if (enfant == null) return;

        voirConsultations();

        System.out.print("ID de la consultation à supprimer : ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            consultationDAO.supprimer(id);
            System.out.println("✔ Consultation supprimée.");
        } catch (NumberFormatException e) {
            System.out.println("Nombre invalide.");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private Enfant choisirEnfant() {
        try {
            List<Enfant> enfants = enfantDAO.trouverParParent(idParent);
            if (enfants.isEmpty()) {
                System.out.println("Aucun enfant enregistré.");
                return null;
            }
            System.out.println("\n── Choisir un enfant ──");
            enfants.forEach(e -> System.out.println("  " + e));
            System.out.print("ID de l'enfant : ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            return enfantDAO.trouverParId(id);
        } catch (NumberFormatException e) {
            System.out.println("Nombre invalide.");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
        return null;
    }

    private LocalDate demanderDate(String message) {
        System.out.print(message);
        try {
            return LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("Format invalide. Utilisez AAAA-MM-JJ.");
            return null;
        }
    }
}
