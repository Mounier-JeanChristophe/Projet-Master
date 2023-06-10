package Algorithme.Ordonnancement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.*;

public class Main {

    public static void main(String[] args){
        ArrayList<String> adresses = new ArrayList<>();
        adresses.add("Stade municipal, Sablet");
        adresses.add("Chem. de Saint-Hilaire, 84170 Monteux");
        adresses.add("AVENUE DU STADE,  84870 LORIOL DU COMTAT");
        adresses.add("Av. Pierre de Coubertin, 84150 Jonquières");
        adresses.add("Chemin de la Passerelle, 84100 Orange");
        adresses.add("Stade Municipal, 26170 Mollans-sur-Ouvèze");
        adresses.add("151 Chem. de Meneque, 84410 Bédoin");
        adresses.add("Chem. de Saint-Roch, 84210 Saint-Didier");
        adresses.add("33 Imp. du Stade, 84830 Sérignan-du-Comtat");
        //adresses.add("Avenue du Marechal Leclerc, 84500 BOLLENE");

        DataGenerator dataGenerator = new DataGenerator(adresses.size(),4,5,0,9, adresses);
        dataGenerator.generateIntputFile();
        System.out.println("generate input file ok");
        Scheduling scheduling = new Scheduling(Scheduling.modele.MINZ);
        scheduling.getSchedulingResult();
        System.out.println(scheduling);
    }
}
