package com.example.optimisationcompetitionssportives;

/**
 * Classe equipe, contient les informations liees a une equipe
 */
public class Equipe {
    String poule;
    String nom;
    String region;
    String departement;
    String coordonnees;
    String niveau;
    String age;
    String sport;
    String codePostal;

    /**
     * @param nom
     * @param region
     * @param departement
     * @param coordonnees
     * @param niveau
     * @param age
     * @param sport
     */
    public Equipe(String nom, String region, String departement, String coordonnees, String niveau, String age, String sport) {
        this.poule="Pas de poule";
        this.nom = nom;
        this.region = region;
        this.departement = departement;
        this.coordonnees = coordonnees;
        this.niveau = niveau;
        this.age = age;
        this.sport = sport;
    }

    public Equipe(String nom,int codePostal,String niveau, String age, String sport) {
        this.poule="Pas de poule";
        this.nom = nom;
        this.codePostal = String.valueOf(codePostal);
        this.niveau = niveau;
        this.age = age;
        this.sport = sport;
    }
    @Override
    public String toString() {
        return "Equipe{" +
                "nom='" + nom + '\'' +
                ", region='" + region + '\'' +
                ", departement='" + departement + '\'' +
                ", coordonnees='" + coordonnees + '\'' +
                ", niveau='" + niveau + '\'' +
                ", age='" + age + '\'' +
                ", sport='" + sport + '\'' +
                '}';
    }

    /**
     * Equipe vers ligne de CSV
     * @param delim
     * @return
     */
    public String toCSV(String delim){
        return poule+delim+nom+delim+codePostal;
    }

    public String getPoule() {
        return poule;
    }

    public void setPoule(String poule) {
        this.poule = poule;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getNom() {
        return nom;
    }

    public String getRegion() {
        return region;
    }

    public String getDepartement() {
        return departement;
    }

    public String getCoordonnees() {
        return coordonnees;
    }

    public String getNiveau() {
        return niveau;
    }

    public String getAge() {
        return age;
    }

    public String getSport() {
        return sport;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public void setCoordonnees(String coordonnees) {
        this.coordonnees = coordonnees;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
