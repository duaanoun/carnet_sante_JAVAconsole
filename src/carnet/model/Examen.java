package carnet.model;

import java.time.LocalDate;

/**
 * Modèle — correspond à la table SQL : Examen
 * Colonnes : id_examen, dateExamen, details, resultat,
 *            id_carnetDeSante, id_medecin
 *
 * Représente un examen médical (analyses, radiographies, etc.)
 * effectué sur un enfant.
 */
public class Examen {

    private int       idExamen;
    private LocalDate dateExamen;
    private String    details;      // Type d'examen (sanguin, radiographie, etc.)
    private String    resultat;     // Résultat de l'examen
    private int       idCarnetDeSante;
    private int       idMedecin;

    public Examen() {}

    public Examen(int idExamen, LocalDate dateExamen,
                  String details, String resultat,
                  int idCarnetDeSante, int idMedecin) {
        this.idExamen        = idExamen;
        this.dateExamen      = dateExamen;
        this.details         = details;
        this.resultat        = resultat;
        this.idCarnetDeSante = idCarnetDeSante;
        this.idMedecin       = idMedecin;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getIdExamen()        { return idExamen; }
    public LocalDate getDateExamen()      { return dateExamen; }
    public String    getDetails()         { return details; }
    public String    getResultat()        { return resultat; }
    public int       getIdCarnetDeSante() { return idCarnetDeSante; }
    public int       getIdMedecin()       { return idMedecin; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setIdExamen(int id)           { this.idExamen = id; }
    public void setDateExamen(LocalDate d)    { this.dateExamen = d; }
    public void setDetails(String details)    { this.details = details; }
    public void setResultat(String resultat)  { this.resultat = resultat; }
    public void setIdCarnetDeSante(int id)    { this.idCarnetDeSante = id; }
    public void setIdMedecin(int id)          { this.idMedecin = id; }

    @Override
    public String toString() {
        return "[" + idExamen + "] " + dateExamen
             + " | " + details
             + " | Résultat : " + resultat;
    }
}
