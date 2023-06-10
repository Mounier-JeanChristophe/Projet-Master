package Algorithme.Ordonnancement;

import API.DistanceGoogleAPI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;

public class DataGenerator {

    private final int nbEquipe;
    private final int borne_inf;
    private final int borne_sup;
    private final int borne_inf_cons;
    private final int borne_sup_cons;
    private final ArrayList<String> adresses;

    public DataGenerator(int nbEquipe, int borne_inf, int borne_sup, int borne_inf_cons, int borne_sup_cons, ArrayList<String> adresses) {
        this.nbEquipe = nbEquipe;
        this.borne_inf = borne_inf;
        this.borne_sup = borne_sup;
        this.borne_inf_cons = borne_inf_cons;
        this.borne_sup_cons = borne_sup_cons;
        this.adresses = adresses;
    }

    public void generateIntputFile(){
        String filename = "src/main/java/Algorithme/Ordonnancement/Modeles/param.txt";
        int nbEquipeTotal = (nbEquipe%2 == 0) ? nbEquipe : nbEquipe+1;
        int nbRound = nbEquipeTotal - 1;
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));

            writer.write("n = " + nbEquipeTotal);
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

        String filename = "src/main/java/Algorithme/Ordonnancement/Modeles/param.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write("d = [ \n");

            DistanceGoogleAPI api = new DistanceGoogleAPI(this.adresses);
            int[][] matriceDistances = api.getDistances();

            int nbEquipeTotal = (nbEquipe%2 == 0) ? nbEquipe : nbEquipe+1;
            for(int i = 0; i < nbEquipeTotal; i++){
                for(int j = 0; j < nbEquipeTotal; j++){
                    String distance = String.valueOf(matriceDistances[i][j]);
                    writer.append(distance).append(" ");
                }
                writer.append("\n");
            }
            writer.append("]");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
