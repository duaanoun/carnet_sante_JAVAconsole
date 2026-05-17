package carnet.model;

import java.time.LocalDate;

/**
 * Modèle — correspond à la table SQL : Croissance
 * Colonnes : id_croissance, dateMesure, taille, poids,
 *            id_carnetDeSante, id_medecin
 *
 * Représente le suivi de la croissance d'un enfant (taille, poids, IMC)
 */
public class Croissance {

    private int       idCroissance;
    private LocalDate dateMesure;
    private float     taille;      // en cm
    private float     poids;       // en kg
    private int       idCarnetDeSante;
    private int       idMedecin;

    public Croissance() {}

    public Croissance(int idCroissance, LocalDate dateMesure,
                      float taille, float poids,
                      int idCarnetDeSante, int idMedecin) {
        this.idCroissance    = idCroissance;
        this.dateMesure      = dateMesure;
        this.taille          = taille;
        this.poids           = poids;
        this.idCarnetDeSante = idCarnetDeSante;
        this.idMedecin       = idMedecin;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getIdCroissance()    { return idCroissance; }
    public LocalDate getDateMesure()      { return dateMesure; }
    public float     getTaille()          { return taille; }
    public float     getPoids()           { return poids; }
    public int       getIdCarnetDeSante() { return idCarnetDeSante; }
    public int       getIdMedecin()       { return idMedecin; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setIdCroissance(int id)       { this.idCroissance = id; }
    public void setDateMesure(LocalDate d)    { this.dateMesure = d; }
    public void setTaille(float t)            { this.taille = t; }
    public void setPoids(float p)             { this.poids = p; }
    public void setIdCarnetDeSante(int id)    { this.idCarnetDeSante = id; }
    public void setIdMedecin(int id)          { this.idMedecin = id; }

    // ── Méthodes métier ───────────────────────────────────────────────────────

    /**
     * Calcule l'IMC (Indice de Masse Corporelle)
     * Formule : IMC = poids (kg) / (taille (m))²
     *
     * @return IMC arrondi à 2 décimales
     */
    public float calculerIMC() {
        if (taille <= 0) return 0;
        float tailleEnMetres = taille / 100f;
        return Math.round((poids / (tailleEnMetres * tailleEnMetres)) * 100f) / 100f;
    }

    /**
     * Interprète l'IMC selon les normes OMS pour enfants
     * Note: Cette interprétation varie selon l'âge de l'enfant (simplifié ici)
     *
     * @return Catégorie IMC (Insuffisant, Normal, Surpoids, Obésité)
     */
    public String interpreterIMC() {
        float imc = calculerIMC();
        if (imc < 18.5) return "Insuffisant";
        if (imc < 25) return "Normal";
        if (imc < 30) return "Surpoids";
        return "Obésité";
    }

    @Override
    public String toString() {
        return "[" + idCroissance + "] " + dateMesure
             + " | Taille : " + taille + " cm | Poids : " + poids + " kg"
             + " | IMC : " + calculerIMC() + " (" + interpreterIMC() + ")";
    }
}
