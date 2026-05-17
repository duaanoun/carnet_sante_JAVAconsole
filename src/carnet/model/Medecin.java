package carnet.model;

/**
 * Modèle — correspond à la table SQL : Medecin
 * Colonnes : id_medecin, nom, prenom, specialite, telephone, cabinet, email, password
 *
 * Représente un médecin connecté au système pour gérer les consultations,
 * vaccinations et examens des enfants.
 */
public class Medecin {

    private int    idMedecin;
    private String nom;
    private String prenom;
    private String specialite;  // Pédiatre, Cardiologue, etc.
    private String telephone;
    private String cabinet;     // Adresse/Localité du cabinet
    private String email;
    private String password;    // Hash MD5 ou autre (sécurité)

    public Medecin() {}

    public Medecin(int idMedecin, String nom, String prenom,
                   String specialite, String telephone, String cabinet,
                   String email, String password) {
        this.idMedecin  = idMedecin;
        this.nom        = nom;
        this.prenom     = prenom;
        this.specialite = specialite;
        this.telephone  = telephone;
        this.cabinet    = cabinet;
        this.email      = email;
        this.password   = password;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getIdMedecin()  { return idMedecin; }
    public String getNom()        { return nom; }
    public String getPrenom()     { return prenom; }
    public String getSpecialite() { return specialite; }
    public String getTelephone()  { return telephone; }
    public String getCabinet()    { return cabinet; }
    public String getEmail()      { return email; }
    public String getPassword()   { return password; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setIdMedecin(int id)     { this.idMedecin = id; }
    public void setNom(String nom)       { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setSpecialite(String s)  { this.specialite = s; }
    public void setTelephone(String tel) { this.telephone = tel; }
    public void setCabinet(String cab)   { this.cabinet = cab; }
    public void setEmail(String email)   { this.email = email; }
    public void setPassword(String pwd)  { this.password = pwd; }

    @Override
    public String toString() {
        return "[" + idMedecin + "] Dr. " + prenom + " " + nom
             + " | " + specialite + " | " + telephone
             + " | " + cabinet;
    }
}
