package DB.Equipe;

import DB.Connexion;
import DB.Ville.Ville;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EquipeDAO {
    String dbPath="Database/BDD.db";

    public ArrayList<Equipe> getEquipeResult(ResultSet resultSet){
        ArrayList<Equipe> equipeArrayList=new ArrayList<>();
        try {
            while (resultSet.next()) {
                Equipe e=new Equipe(resultSet.getInt("EquipeId"),resultSet.getString("Nom"),resultSet.getString("Sport"),resultSet.getString("Niveau"),resultSet.getString("Age"),resultSet.getString("Ville"));
                equipeArrayList.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipeArrayList;
    }

    public ArrayList<Equipe> getEquipes(){
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * FROM Equipe;");
        ArrayList<Equipe> equipeArrayList=this.getEquipeResult(resultSet);
        connexion.close();
        return equipeArrayList;
    }

    public Equipe getEquipeById(int equipeId) {
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * from Equipe WHERE EquipeId=" + equipeId + ";");
        Equipe equipe=this.getEquipeResult(resultSet).get(0);
        connexion.close();
        return equipe;
    }

    public Equipe getEquipeByName(String nom) {
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * from Equipe WHERE Nom='" + nom + "';");
        Equipe equipe=this.getEquipeResult(resultSet).get(0);
        connexion.close();
        return equipe;
    }



    public String getEquipeCoordonnees(String nom){
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * from Equipe WHERE Nom='" + nom + "';");
        Equipe equipe=this.getEquipeResult(resultSet).get(0);
        resultSet = connexion.query("SELECT * from Ville WHERE VilleId=" + equipe.getVille() + ";");
        Ville ville = null;
        try {
            while (resultSet.next()) {
                ville = new Ville(resultSet.getInt("VilleId"), resultSet.getString("Nom"), resultSet.getString("Departement"), resultSet.getInt("CodePostal"), resultSet.getString("Latitude"), resultSet.getString("Longitude"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connexion.close();
        return ville.getCoordonnees();
    }

    public void addEquipe(Equipe equipe) {
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        String query = "";
        query += "INSERT INTO Equipe(Nom,Sport,Niveau,Age,Ville) VALUES (";
        query += "'" + equipe.getNom() + "', ";
        query += "'" + equipe.getSport() + "', ";
        query += "'" + equipe.getNiveau() + "', ";
        query += "'" + equipe.getAge() + "', ";
        query += "'" + equipe.getVille() + "'); ";
        connexion.submitQuery(query);
        connexion.close();
    }

    public void deleteEquipe(Equipe equipe){
        Connexion connexion = new Connexion(this.dbPath);
        connexion.connect();
        String query = "";
        query += "DELETE FROM Equipe WHERE equipeId = ";
        query += equipe.getEquipeId();
        connexion.submitQuery(query);
        connexion.close();
    }
}
