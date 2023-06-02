package Ordonnancement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;

public class DataGenerator {

    private int nbEquipe;
    private int borne_inf;
    private int borne_sup;
    private int borne_inf_cons;
    private int borne_sup_cons;
    private ArrayList<String> adresse;

    public DataGenerator(int nbEquipe, int borne_inf, int borne_sup, int borne_inf_cons, int borne_sup_cons, ArrayList<String> adresse) {
        this.nbEquipe = nbEquipe;
        this.borne_inf = borne_inf;
        this.borne_sup = borne_sup;
        this.borne_inf_cons = borne_inf_cons;
        this.borne_sup_cons = borne_sup_cons;
        this.adresse = adresse;
    }

    public void generateIntputFile(){
        String filename = "src/main/java/Ordonnancement/Modeles/param_test.txt";
        int nbRound = nbEquipe - 1;
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

            writer.write("n = " + nbEquipe);
            writer.append("\nr = ").append(String.valueOf(nbRound));
            writer.append("\nL_CONS = ").append(String.valueOf(borne_inf_cons));
            writer.append("\nU_CONS = ").append(String.valueOf(borne_sup_cons));
            writer.append("\nL = ").append(String.valueOf(borne_inf));
            writer.append("\nU = ").append(String.valueOf(borne_sup));
            writer.append("\n");
            writer.close();

            calculateDistances();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void calculateDistances() {

    }
}
