# Section 2 — Java Console : Carnet de Santé

## Structure du projet (13 fichiers)

```
src/carnet/
│
├── App.java                        ← Point d'entrée — menu principal
│
├── model/                          ← Classes Java = tables SQL
│   ├── Enfant.java                 ← TABLE Enfant
│   ├── Vaccination.java            ← TABLE Vaccination
│   ├── Consultation.java           ← TABLE Consultation
│   └── CarnetDeSante.java          ← Agrégateur pour l'historique
│
├── dao/                            ← Requêtes SQL avec PreparedStatement
│   ├── DatabaseConnection.java     ← Connexion MySQL unique (Singleton)
│   ├── EnfantDAO.java              ← CRUD Enfant
│   ├── VaccinationDAO.java         ← CRUD Vaccination
│   └── ConsultationDAO.java        ← CRUD Consultation
│
└── menu/                           ← Interface console (remplace FXML)
    ├── EnfantMenu.java             ← Lister / Ajouter / Modifier / Supprimer
    ├── VaccinationMenu.java        ← Voir / Ajouter / Supprimer
    ├── ConsultationMenu.java       ← Voir / Ajouter / Supprimer
    └── HistoriqueMenu.java         ← Historique complet (vaccins + consultations)
```

---

## Architecture : Console → DAO → Database

```
App.java  →  *Menu.java  →  *DAO.java  →  DatabaseConnection  →  MySQL
```

Chaque couche a UN seul rôle :
- **Menu** : saisie utilisateur + affichage résultat
- **DAO**  : requêtes SQL uniquement
- **Model**: données (getters/setters, pas de logique)

---

## Compiler et lancer

```bash
# Compiler (mettre le driver MySQL dans le dossier)
javac -cp mysql-connector-j.jar -d out src/carnet/*.java src/carnet/**/*.java

# Lancer
java -cp out:mysql-connector-j.jar carnet.App
```

---

## Logique métier conservée

| Règle                     | Où c'est codé                         |
|---------------------------|---------------------------------------|
| Rappel vaccin +6 mois     | `VaccinationDAO.ajouter()`            |
| Dose >= 1                 | `VaccinationMenu.ajouterVaccination()`|
| statut ENUM consultation  | `ConsultationMenu` + `ConsultationDAO`|
| Historique complet        | `HistoriqueMenu` + `CarnetDeSante`    |

---

## Phrases clés pour la soutenance

1. **Architecture** : "On a remplacé JavaFX par une interface console — 3 couches claires : Menu → DAO → MySQL."
2. **PreparedStatement** : "Toutes les requêtes utilisent PreparedStatement pour éviter les injections SQL."
3. **Rappel vaccin** : "Si dose == 1, le rappel est calculé automatiquement à 6 mois dans le DAO."
4. **Singleton** : "DatabaseConnection utilise le pattern Singleton — une seule connexion partagée par tous les DAO."
5. **ENUM MySQL** : "Le statut d'une consultation est un ENUM MySQL : planifiee, realisee ou annulee."
