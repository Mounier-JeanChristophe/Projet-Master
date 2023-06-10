package Algorithme.Graphe;

import org.decimal4j.util.DoubleRounder;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class GraphePoids implements Serializable{

    private final List<Poids[]> match;
    List<List<Integer>> composanteList;

    private final int borne;

    /*
     * Constructeur initalisant:
     * -la borne utilisee dans le calcul de Pijk
     * -la list de Poids[]
     */
    public GraphePoids (String filename, int borne) throws IOException {
        String[] data;
        filename = "data/"+filename;
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        reader.close();

        data = line.split(" ");
        int nbEquipe = Integer.parseInt(data[0]);

        this.match = new ArrayList<>();
        this.composanteList = new ArrayList<>();
        this.borne = borne;
        for(int i=0; i<nbEquipe; i++){
            match.add(i, new Poids[i]);
            for(int j=0; j<i; j++){
                match.get(i)[j] = new Poids();
            }
        }
    }

    /*
     * fonction qui renvoie l'indice de force entre i et j pour le match k
     * di est le score de i et dj le score de j
     */
    private double getPijk(int di, int dj){
        int diffScore = di - dj;
        double pijk;
        if(di>=dj){
            pijk = 0.5 + (Math.min(diffScore,borne))/(2.0*borne);
        }
        else{
            diffScore = Math.abs(diffScore);
            pijk = 0.5 - (Math.min(diffScore,borne))/(2.0*borne);
        }
        return pijk;
    }

    /*
     * fonction qui renvoie Pij en fonction de tout les Pijk calcules
     * list contient l'ensemble des Pijk
     */
    private double getPij(List<Double> list){
        double Pij = 0;
        double lambda = 1.0/list.size();
        for(double val : list){
            Pij += lambda * val;
        }
        if(Pij>0.5){
            return Math.min(Pij,0.8);
        }
        return Math.max(Pij,0.2);
    }

    /*
     * fonction qui renvoie la force relative de i contre j en log10
     * Pij est la probabilite que i soit plus fort que j
     */
    private double probaToForceLog(double Pij){return Math.log10(Pij/(1-Pij));}

    /*
     * fonction qui renvoie la force relative de i contre j
     * Pij est la probabilite que i soit plus fort que j
     */
    public double getForce(int i, int j){
        double force = getMatch(i,j).prediction;
        force = Math.pow(10,force);
        return (i>j) ? force : 1/force;
    }

    /*
     * fonction calculant les poids de chaque arc ij
     * les poids correspondent au logarithme des forces relatives de i contre j
     */
    public void calculPoids(){
        for(Poids[] tabPoids : match){
            for(Poids p : tabPoids){
                if(p.scoreList.isEmpty()){
                    p.poids = -1;
                }
                else {
                    double Pij = getPij(p.scoreList);
                    p.poids = DoubleRounder.round(probaToForceLog(Pij),6);
                }
            }
        }
    }

    public double getSumAll(int j){
        int size = match.size();
        double sum = 0;
        for(int i=0; i<size; i++){
            if(i!=j) {
                if ((this.getMatch(i, j).estimation == 0 || this.getMatch(i, j).estimation == -1)) {
                    sum += (i > j) ? -this.getMatch(i, j).poids : this.getMatch(i, j).poids;
                } else {
                    sum += (i > j) ? this.getMatch(i, j).estimation : -this.getMatch(i, j).estimation;
                }
            }
        }
        return sum;
    }

    public double getSumForce(int j){
        int size = match.size();
        double sum = 0;
        for(int i=0; i<size; i++){
            if(i!=j){
                sum += (i > j) ? -this.getMatch(i, j).poids : this.getMatch(i, j).poids;
            }
        }
        return sum;
    }

    public double getSumEstimation(int j){
        int size = match.size();
        double sum = 0;
        for(int i=0; i<size; i++){
            if(i!=j){
                sum += (i > j) ? this.getMatch(i, j).estimation : -this.getMatch(i, j).estimation;
            }
        }
        return sum;
    }

    /*
     * fonction qui ajoute le match dans la structure de donnees
     * i est j sont les equipes qui s'opposent
     * scoreI et scoreJ sont les scores des equipes
     */
    public void loadMatch(int i, int j, int scoreI, int scoreJ){
        if(i<j){
            int k = i;
            int sk = scoreI;
            i = j;
            j = k;
            scoreI = scoreJ;
            scoreJ = sk;
        }
        double score = getPijk(scoreI,scoreJ);
        match.get(i-1)[j-1].scoreList.add(score);
        calculPoids();
    }

    /*
     * fonction qui charge les donnees contenues dans un fichier
     * le nom du fichier est passe en argument <filename>
     */
    public void loadFile(String filename) {

        BufferedReader reader;
        try {
            String[] data;
            reader = new BufferedReader(new FileReader("data/"+filename));
            String line = reader.readLine();

            data = line.split(" ");
            int nbm = Integer.parseInt(data[1]);

            for(int k=0; k<nbm; k++){
                line = reader.readLine();
                data = line.split(" ");
                int i, j, di, dj;
                i = Integer.parseInt(data[0]);
                j = Integer.parseInt(data[1]);
                di = Integer.parseInt(data[2]);
                dj = Integer.parseInt(data[3]);
                loadMatch(i,j,di,dj);
            }
            reader.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        calculPoids();
    }

    public void print(){
        int size = match.size();
        for(int i=1 ;i<size; i++){
            for(int j = 0; j<i; j++) {
                System.out.println("Equipes(" + i + "," + j + ") " + getMatch(i, j).scoreList
                        + " Poids/Prédiction (" + getMatch(i, j).poids + " | "
                        + getMatch(i,j).prediction + ")");
            }
        }
    }

    public void getPrediction(int i, int j){
        String BLUE = "\u001B[34m";
        String RED = "\u001B[31m";
        String RESET = "\u001B[0m";

        double poids = getForce(i,j);
        double winRate = (100*poids) / (poids+1);

        winRate = DoubleRounder.round(winRate,1);
        System.out.println("Probabilité de victoire "
                +BLUE+i+RESET+"/"+RED+j+" "
                +BLUE+winRate+"% "+RED+DoubleRounder.round(100-winRate,1)+"%"+RESET
        );
    }

    public void getAllPrediction(){
        int size = match.size();
        for(int i=0; i<size; i++){
            for(int j=0; j<i; j++){
                getPrediction(i,j);
            }
        }
    }

    public Poids getMatch(int i,int j){
        return (i>j) ? this.match.get(i)[j] : this.match.get(j)[i];
    }

    public int getSize(){
        return this.match.size();
    }
}
