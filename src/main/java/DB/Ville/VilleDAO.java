package DB.Ville;

import DB.Connexion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VilleDAO {
    String dbPath="Database/BDD.db";

    public ArrayList<Ville> getVilles(){
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * FROM Ville;");
        ArrayList<Ville> villeArrayList=new ArrayList<>();
        try {
            while (resultSet.next()) {
                Ville v = new Ville(resultSet.getInt("VilleId"), resultSet.getString("Nom"), resultSet.getString("Departement"), resultSet.getInt("CodePostal"), resultSet.getString("Latitude"), resultSet.getString("Longitude"));
                villeArrayList.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connexion.close();
        return villeArrayList;
    }

    public Ville getVilleByID(int villeId) {
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * from Ville WHERE VilleID=" + villeId + ";");
        Ville ville = null;
        try {
            while (resultSet.next()) {
                ville = new Ville(resultSet.getInt("VilleId"), resultSet.getString("Nom"), resultSet.getString("Departement"), resultSet.getInt("CodePostal"), resultSet.getString("Latitude"), resultSet.getString("Longitude"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connexion.close();
        return ville;
    }

    public Ville getVilleByCodePostal(int codePostal) {
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * from Ville WHERE CodePostal=" + codePostal + ";");
        Ville ville = null;
        try {
            while (resultSet.next()) {
                ville = new Ville(resultSet.getInt("VilleId"), resultSet.getString("Nom"), resultSet.getString("Departement"), resultSet.getInt("CodePostal"), resultSet.getString("Latitude"), resultSet.getString("Longitude"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connexion.close();
        return ville;
    }

    public void addVille(Ville ville) {
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        String query = "";
        query += "INSERT INTO Ville(Nom,Departement,CodePostal,Latitude,Longitude) VALUES (";
        query += "'" + ville.getNom() + "', ";
        query += "'" + ville.getDepartement() + "', ";
        query += "'" + ville.getCodePostal() + "', ";
        query += "'" + ville.getLatitude() + "', ";
        query += "'" + ville.getLongitude() + "'); ";
        connexion.submitQuery(query);
        connexion.close();
    }
}
