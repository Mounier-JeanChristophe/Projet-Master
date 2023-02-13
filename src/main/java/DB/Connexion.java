package DB;

import java.sql.*;

public class Connexion {
        protected String DBPath = "/Database/BDD.db";
        protected Connection connection = null;
        protected Statement statement = null;

        public Connexion(String dBPath) {
            DBPath = dBPath;
        }

        public void connect() {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
                statement = connection.createStatement();
//                System.out.println("Connexion a " + DBPath + " avec succès");
            } catch (ClassNotFoundException notFoundException) {
                notFoundException.printStackTrace();
                System.out.println("Erreur de connexion");
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                System.out.println("Erreur de connexion");
            }
        }

        public void close() {
            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public ResultSet query(String requete) {
            ResultSet resultat = null;
            try {
                resultat = statement.executeQuery(requete);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur dans la requete : " + requete);
            }
            return resultat;
        }

        public void submitQuery(String query){
            try {
                statement.executeUpdate(query);
            } catch (SQLException e) {
                System.out.println("Erreur dans la requete : "+query);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
}
