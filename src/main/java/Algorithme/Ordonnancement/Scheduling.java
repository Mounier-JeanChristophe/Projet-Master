package Algorithme.Ordonnancement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scheduling {

    public enum modele {MINZ, CARRE, ABS}
    
    private double dtm;
    private final Map<Integer, Integer> distances;
    private final ArrayList<int[]> ordonnancement;
    private final modele modele;

    public Scheduling(modele modele) {
        this.dtm = 0;
        this.distances = new HashMap<>();
        this.ordonnancement = new ArrayList<>();
        this.modele = modele;
    }

    public void setDtm(double dtm){
        this.dtm = dtm;
    }

    public void addDistance(int id, int distance){
        this.distances.put(id, distance);
    }

    public void addMatch(int i, int j, int k){
        int[] match = {i, j, k};
        this.ordonnancement.add(match);
    }

    public String toString(){

        StringBuilder resultats = new StringBuilder();
        resultats.append("Distance totale moyenne: ").append(dtm).append("\n");

        resultats.append("\nDistances par equipe\n");
        for(int i = 1; i <= distances.size(); i++){
            resultats.append("Equipe ").append(i).append(": ").append(distances.get(i)).append("km\n");
        }

        resultats.append("\nCalendrier\n");
        int jour = 0;
        for(int[] tab : ordonnancement){
            if(jour != tab[2]){
                resultats.append("----- Journee ").append(tab[2]).append("-----\n");
                jour = tab[2];
            }
            resultats.append(tab[0]).append(" contre ").append(tab[1]).append("\n");
        }

        return resultats.toString();
    }

    public void getSchedulingResult(){
        try{
            String algo;
            switch (this.modele){
                case MINZ -> algo = "modele_minz.jl";
                case ABS -> algo = "modele_abs.jl";
                case CARRE -> algo = "modele_carre.jl ";
                default -> throw new IllegalStateException("Unexpected value: " + this.modele);
            }

            String model = "src/main/java/Algorithme/Ordonnancement/Modeles/" + algo;
            String command = "julia " + model;

            // lancement du modele Julia
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // lecture de la sortie
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern p1, p2, p3;
            Matcher m1, m2, m3;
            p1 = Pattern.compile("^dtm");
            p2 = Pattern.compile("^dt");
            p3 = Pattern.compile("^x");

            while ((line = reader.readLine()) != null) {
                m1 = p1.matcher(line);
                m2 = p2.matcher(line);
                m3 = p3.matcher(line);
                //System.out.println(line);

                // cas "dtm"
                if(m1.find()){
                    String[] token = line.split(" ");
                    double dtm = Double.parseDouble(token[1]);
                    //System.out.println(dtm);
                    setDtm(dtm);
                }
                //cas "dt[i]"
                else if(m2.find()){
                    String newLine;
                    newLine = line.replace("[", " ");
                    newLine = newLine.replace("]", " ");
                    String[] token = newLine.split(" ");
                    int id = Integer.parseInt(token[1]);

                    double doubleValue = Double.parseDouble(token[4]);
                    int distance = (int) Math.round(doubleValue);

                    //System.out.println(id+" "+distance);
                    addDistance(id, distance);

                }
                // cas "x[i,j,k]"
                else if(m3.find()){
                    String newLine;
                    newLine = line.replace("[", " ");
                    newLine = newLine.replace("]", " ");
                    newLine = newLine.replace(",", " ");
                    String[] token = newLine.split(" ");
                    int i,j,k;
                    i = Integer.parseInt(token[1]);
                    j = Integer.parseInt(token[2]);
                    k = Integer.parseInt(token[3]);
                    //System.out.println(i+" "+j+" "+k);
                    addMatch(i, j, k);
                }
            }
            int lastIndex = distances.size();
            double lastDistance = distances.get(lastIndex);
            if(lastDistance == 0){
                double newDtm = dtm * lastIndex/(lastIndex-1);
                dtm = Math.round(newDtm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
