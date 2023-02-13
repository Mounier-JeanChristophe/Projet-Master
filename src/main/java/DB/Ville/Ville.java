package DB.Ville;

public class Ville {
    int villeId;
    String nom;
    String departement;
    int codePostal;
    String latitude;
    String longitude;

    public Ville(Integer villeId,String nom, String departement, int codePostal,String longitude, String latitude) {
        super();
        this.villeId=villeId;
        this.nom = nom;
        this.departement = departement;
        this.codePostal = codePostal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Ville(String nom, String departement, int codePostal,String longitude,  String latitude) {
        super();
        this.nom = nom;
        this.departement = departement;
        this.codePostal = codePostal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public Double getLatitudeDouble(){return Double.valueOf(latitude);}

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Double getLongitudeDouble(){return Double.valueOf(longitude);}

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public int getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public String getCoordonnees() {
        return this.longitude+","+this.latitude;
    }

    public int getVilleId() {
        return villeId;
    }

    public void setVilleId(int villeId) {
        this.villeId = villeId;
    }

    @Override
    public String toString() {
        return "Ville{" +
                "villeId=" + villeId +
                ", nom='" + nom + '\'' +
                ", departement='" + departement + '\'' +
                ", codePostal=" + codePostal +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
