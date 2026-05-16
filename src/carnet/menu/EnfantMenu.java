package carnet.menu;

import carnet.dao.EnfantDAO;
import carnet.model.Enfant;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu console — Gestion des enfants
 * Fonctions : lister, ajouter, modifier, supprimer
 */
public class EnfantMenu {

    private final Scanner    scanner;
    private final EnfantDAO  enfantDAO  = new EnfantDAO();
    private final int        idParent;  // ID du parent connecté (passé depuis le menu principal)

    public EnfantMenu(Scanner scanner, int idParent) {
        this.scanner  = scanner;
        this.idParent = idParent;
    }

    /** Point d'entrée du sous-menu Enfants */
    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      GESTION DES ENFANTS     ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Lister mes enfants        ║");
            System.out.println("║  2. Ajouter un enfant         ║");
            System.out.println("║  3. Modifier un enfant        ║");
            System.out.println("║  4. Supprimer un enfant       ║");
            System.out.println("║  0. Retour au menu principal  ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> listerEnfants();
                case "2" -> ajouterEnfant();
                case "3" -> modifierEnfant();
                case "4" -> supprimerEnfant();
                case "0" -> continuer = false;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── 1. Lister ─────────────────────────────────────────────────────────────

    private void listerEnfants() {
        try {
            List<Enfant> enfants = enfantDAO.trouverParParent(idParent);
            if (enfants.isEmpty()) {
                System.out.println("  Aucun enfant enregistré.");
                return;
            }
            System.out.println("\n── Liste des enfants ──────────────────────────");
            enfants.forEach(e -> System.out.println("  " + e));
            System.out.println("───────────────────────────────────────────────");
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── 2. Ajouter ────────────────────────────────────────────────────────────

    private void ajouterEnfant() {
        System.out.println("\n── Ajouter un enfant ──");

        System.out.print("Nom        : ");
        String nom = scanner.nextLine().trim();
        if (nom.isEmpty()) { System.out.println("Le nom est obligatoire."); return; }

        System.out.print("Prénom     : ");
        String prenom = scanner.nextLine().trim();
        if (prenom.isEmpty()) { System.out.println("Le prénom est obligatoire."); return; }

        LocalDate dateNaissance = demanderDate("Date de naissance (AAAA-MM-JJ) : ");
        if (dateNaissance == null) return;

        System.out.print("Sexe (M/F) : ");
        String sexe = scanner.nextLine().trim().toUpperCase();
        if (!sexe.equals("M") && !sexe.equals("F")) {
            System.out.println("Sexe invalide (M ou F uniquement).");
            return;
        }

        System.out.print("Groupe sanguin (optionnel, Entrée pour ignorer) : ");
        String gs = scanner.nextLine().trim();

        Enfant enfant = new Enfant();
        enfant.setNom(nom);
        enfant.setPrenom(prenom);
        enfant.setDateNaissance(dateNaissance);
        enfant.setSexe(sexe);
        enfant.setGroupeSanguin(gs.isEmpty() ? null : gs);
        enfant.setIdParent(idParent);

        try {
            enfantDAO.ajouter(enfant);
            System.out.println("✔ Enfant ajouté avec succès (id=" + enfant.getIdEnfant() + ").");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    // ── 3. Modifier ───────────────────────────────────────────────────────────

    private void modifierEnfant() {
        listerEnfants();

        System.out.print("\nID de l'enfant à modifier : ");
        int id = lireEntier();
        if (id == -1) return;

        try {
            Enfant enfant = enfantDAO.trouverParId(id);
            if (enfant == null || enfant.getIdParent() != idParent) {
                System.out.println("Enfant introuvable.");
                return;
            }

            System.out.println("Enfant sélectionné : " + enfant);
            System.out.println("(Appuyez sur Entrée pour conserver la valeur actuelle)");

            System.out.print("Nouveau nom [" + enfant.getNom() + "] : ");
            String nom = scanner.nextLine().trim();
            if (!nom.isEmpty()) enfant.setNom(nom);

            System.out.print("Nouveau prénom [" + enfant.getPrenom() + "] : ");
            String prenom = scanner.nextLine().trim();
            if (!prenom.isEmpty()) enfant.setPrenom(prenom);

            System.out.print("Nouvelle date naissance [" + enfant.getDateNaissance() + "] : ");
            String dateStr = scanner.nextLine().trim();
            if (!dateStr.isEmpty()) {
                try { enfant.setDateNaissance(LocalDate.parse(dateStr)); }
                catch (DateTimeParseException ex) { System.out.println("Date invalide, valeur conservée."); }
            }

            System.out.print("Nouveau sexe M/F [" + enfant.getSexe() + "] : ");
            String sexe = scanner.nextLine().trim().toUpperCase();
            if (sexe.equals("M") || sexe.equals("F")) enfant.setSexe(sexe);

            enfantDAO.modifier(enfant);
            System.out.println("✔ Enfant modifié avec succès.");

        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    // ── 4. Supprimer ──────────────────────────────────────────────────────────

    private void supprimerEnfant() {
        listerEnfants();

        System.out.print("\nID de l'enfant à supprimer : ");
        int id = lireEntier();
        if (id == -1) return;

        System.out.print("Confirmer la suppression ? (o/n) : ");
        if (scanner.nextLine().trim().equalsIgnoreCase("o")) {
            try {
                enfantDAO.supprimer(id);
                System.out.println("✔ Enfant supprimé.");
            } catch (SQLException e) {
                System.err.println("Erreur : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression annulée.");
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private LocalDate demanderDate(String message) {
        System.out.print(message);
        try {
            return LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("Format invalide. Utilisez AAAA-MM-JJ.");
            return null;
        }
    }

    private int lireEntier() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Nombre invalide.");
            return -1;
        }
    }
}
