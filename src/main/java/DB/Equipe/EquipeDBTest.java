package DB.Equipe;

import DB.Ville.Ville;
import DB.Ville.VilleDAO;

import java.util.ArrayList;

public class EquipeDBTest {
    public static void fillDB(){
        EquipeDAO dao=new EquipeDAO();
        Equipe equipe = new Equipe("Cab","Foot","A","18","13440");
        dao.addEquipe(equipe);
        equipe = new Equipe("Sa","Foot","A","18","13670");
        dao.addEquipe(equipe);
        equipe = new Equipe("Av","Foot","A","18","84000");
        dao.addEquipe(equipe);
        equipe = new Equipe("Cau","Foot","A","18","84510");
        dao.addEquipe(equipe);
    }

    public static void main(String[] args) {

        //fillDB();
        EquipeDAO dao=new EquipeDAO();

        ArrayList<Equipe> list=dao.getEquipes();
        for( var value : list ) {
            System.out.println(value.toString());
        }
        System.out.println(dao.getEquipeById(2));

        System.out.println(dao.getEquipeByName("Cab"));


        System.out.println(dao.getEquipeCoordonnees("Cab"));
    }
}
