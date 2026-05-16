package carnet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle — agrégateur de l'historique médical complet d'un enfant.
 * Utilisé uniquement pour l'affichage de l'historique en console.
 *
 * Correspond à la table SQL : carnetDeSante
 */
public class CarnetDeSante {

    private int                idCarnetDeSante;
    private Enfant             enfant;
    private List<Vaccination>  vaccinations   = new ArrayList<>();
    private List<Consultation> consultations  = new ArrayList<>();

    public CarnetDeSante(int idCarnetDeSante, Enfant enfant) {
        this.idCarnetDeSante = idCarnetDeSante;
        this.enfant          = enfant;
    }

    // Getters
    public int                getIdCarnetDeSante() { return idCarnetDeSante; }
    public Enfant             getEnfant()          { return enfant; }
    public List<Vaccination>  getVaccinations()    { return vaccinations; }
    public List<Consultation> getConsultations()   { return consultations; }

    // Setters
    public void setVaccinations(List<Vaccination> v)   { this.vaccinations = v; }
    public void setConsultations(List<Consultation> c) { this.consultations = c; }
}
