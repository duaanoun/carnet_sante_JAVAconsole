package carnet.menu;

import carnet.dao.EnfantDAO;
import carnet.dao.VaccinationDAO;
import carnet.model.Enfant;
import carnet.model.Vaccination;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu console — Gestion des vaccinations
 * Fonctions : lister, ajouter, supprimer
 *
 * Logique intégrée : rappel automatique +6 mois si dose == 1
 */
public class VaccinationMenu {

    private final Scanner        scanner;
    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();
    private final EnfantDAO      enfantDAO      = new EnfantDAO();
    private final int            idParent;

    public VaccinationMenu(Scanner scanner, int idParent) {
        this.scanner  = scanner;
        this.idParent = idParent;
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║    GESTION DES VACCINATIONS  ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Voir les vaccinations     ║");
            System.out.println("║  2. Ajouter une vaccination   ║");
            System.out.println("║  3. Supprimer une vaccination ║");
            System.out.println("║  0. Retour                    ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> voirVaccinations();
                case "2" -> ajouterVaccination();
                case "3" -> supprimerVaccination();
                case "0" -> continuer = false;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── 1. Voir ───────────────────────────────────────────────────────────────

    private void voirVaccinations() {
        Enfant enfant = choisirEnfant();
        if (enfant == null) return;

        try {
            List<Vaccination> liste = vaccinationDAO.trouverParCarnet(enfant.getIdCarnetDeSante());
            if (liste.isEmpty()) {
                System.out.println("  Aucune vaccination enregistrée.");
                return;
            }
            System.out.println("\n── Vaccinations de " + enfant.getPrenom() + " ──────────────");
            liste.forEach(v -> System.out.println("  " + v));
            System.out.println("──────────────────────────────────────────────────");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── 2. Ajouter ────────────────────────────────────────────────────────────

    private void ajouterVaccination() {
        Enfant enfant = choisirEnfant();
        if (enfant == null) return;

        System.out.println("\n── Ajouter une vaccination ──");

        System.out.print("Nom du vaccin    : ");
        String nom = scanner.nextLine().trim();
        if (nom.isEmpty()) { System.out.println("Le nom est obligatoire."); return; }

        LocalDate dateVaccin = demanderDate("Date (AAAA-MM-JJ)    : ");
        if (dateVaccin == null) return;

        System.out.print("Numéro de dose   : ");
        int dose;
        try {
            dose = Integer.parseInt(scanner.nextLine().trim());
            if (dose < 1) { System.out.println("La dose doit être >= 1."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Nombre invalide.");
            return;
        }

        Vaccination v = new Vaccination();
        v.setIdCarnetDeSante(enfant.getIdCarnetDeSante());
        v.setNomVaccin(nom);
        v.setDateVaccin(dateVaccin);
        v.setDose(dose);
        v.setRappel(null); // Calculé automatiquement dans le DAO si dose == 1
        v.setIdMedecin(0);

        try {
            vaccinationDAO.ajouter(v);
            String msg = "✔ Vaccination enregistrée.";
            if (dose == 1) msg += " Rappel prévu le : " + dateVaccin.plusMonths(6);
            System.out.println(msg);
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── 3. Supprimer ──────────────────────────────────────────────────────────

    private void supprimerVaccination() {
        Enfant enfant = choisirEnfant();
        if (enfant == null) return;

        voirVaccinations();

        System.out.print("ID de la vaccination à supprimer : ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            vaccinationDAO.supprimer(id);
            System.out.println("✔ Vaccination supprimée.");
        } catch (NumberFormatException e) {
            System.out.println("Nombre invalide.");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    /** Affiche la liste des enfants et demande d'en sélectionner un par ID */
    private Enfant choisirEnfant() {
        try {
            List<Enfant> enfants = enfantDAO.trouverParParent(idParent);
            if (enfants.isEmpty()) {
                System.out.println("Aucun enfant enregistré. Ajoutez d'abord un enfant.");
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
