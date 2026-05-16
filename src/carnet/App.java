package carnet;

import carnet.dao.DatabaseConnection;
import carnet.menu.ConsultationMenu;
import carnet.menu.EnfantMenu;
import carnet.menu.HistoriqueMenu;
import carnet.menu.VaccinationMenu;

import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════╗
 * ║        CARNET DE SANTÉ — Section Java    ║
 * ║         Application Console JDBC/MySQL   ║
 * ╚══════════════════════════════════════════╝
 *
 * Point d'entrée unique du programme.
 * Architecture : Console (App) → Menu → DAO → DatabaseConnection → MySQL
 *
 * Pour lancer :
 *   javac -cp mysql-connector-j.jar -d out src/carnet/**\/*.java
 *   java  -cp out:mysql-connector-j.jar carnet.App
 */
public class App {

    public static void main(String[] args) {

        // ID du parent connecté — dans un vrai projet, récupéré depuis la session C++
        // (ex. : passé en argument, lu dans un fichier, ou via socket)
        int idParentConnecte = 1;

        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       CARNET DE SANTÉ — Java Console     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║       MENU PRINCIPAL         ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Gestion des enfants      ║");
            System.out.println("║  2. Vaccinations             ║");
            System.out.println("║  3. Consultations            ║");
            System.out.println("║  4. Historique médical       ║");
            System.out.println("║  0. Quitter                  ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> new EnfantMenu(scanner, idParentConnecte).afficher();
                case "2" -> new VaccinationMenu(scanner, idParentConnecte).afficher();
                case "3" -> new ConsultationMenu(scanner, idParentConnecte).afficher();
                case "4" -> new HistoriqueMenu(scanner, idParentConnecte).afficher();
                case "0" -> continuer = false;
                default  -> System.out.println("Choix invalide. Réessayez.");
            }
        }

        // Fermer proprement la connexion MySQL avant de quitter
        DatabaseConnection.fermer();
        scanner.close();
        System.out.println("\nAu revoir !");
    }
}
