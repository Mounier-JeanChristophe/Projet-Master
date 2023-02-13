package DB.Equipe;

import DB.Ville.Ville;
import DB.Ville.VilleDAO;
import javafx.scene.control.CheckBox;

import static java.lang.Integer.parseInt;

public class Equipe {
    Integer equipeId;
    String nom;
    String sport;
    String niveau;
    String age;
    String ville;
    Integer codePostal;
    CheckBox selected;


    public Equipe(Integer equipeId, String nom, String sport, String niveau, String age, String ville) {
        this.equipeId=equipeId;
        this.nom = nom;
        this.sport = sport;
        this.niveau = niveau;
        this.age = age;
        this.ville = ville;
    }

    public Equipe(String nom, String sport, String niveau, String age, Integer codePostal, Integer equipeId, CheckBox selected) {
        this.nom = nom;
        this.sport = sport;
        this.niveau = niveau;
        this.age = age;
        VilleDAO dao=new VilleDAO();
        this.ville=dao.getVilleByCodePostal(codePostal).getNom();
        this.equipeId = equipeId;
        this.selected = selected;
        this.codePostal = codePostal;
    }

    public Equipe(String nom, String sport, String niveau, String age, String ville) {
        this.nom = nom;
        this.sport = sport;
        this.niveau = niveau;
        this.age = age;
        this.ville=ville;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "equipeId=" + equipeId +
                ", nom='" + nom + '\'' +
                ", sport='" + sport + '\'' +
                ", niveau='" + niveau + '\'' +
                ", age='" + age + '\'' +
                ", ville=" + ville +
                '}';
    }

    public Ville getVilleOfEquipe(){
        VilleDAO villeDAO = new VilleDAO();
        Ville v = villeDAO.getVilleByCodePostal(this.getCodePostal());
        return v;
    }

    public Integer getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Integer equipeId) {
        this.equipeId = equipeId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public CheckBox getSelected() {
        return selected;
    }

    public void setSelected(CheckBox selected) {
        this.selected = selected;
    }

    public Integer getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(Integer codePostal) {
        this.codePostal = codePostal;
    }
}
