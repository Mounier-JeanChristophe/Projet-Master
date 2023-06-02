package Ordonnancement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.*;

public class Main {

    public static void main(String[] args){
        DataGenerator dataGenerator = new DataGenerator(10,4,5,0,9,new ArrayList<>());
        //dataGenerator.generateIntputFile();
        getSchedulingResult();

    }

    public static SchedulingResult getSchedulingResult(){
        try{
            // String command = "julia "+args; version finale
            // algo: modele_abs.jl modele_carre.jl modele_minz.jl
            String model = "src/main/java/Ordonnancement/Modeles/modele_minz.jl";
            String command = "julia " + model;

            // lancement du modele Julia
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern p1, p2, p3;
            Matcher m1, m2, m3;
            p1 = Pattern.compile("^dtm");
            p2 = Pattern.compile("^dt");
            p3 = Pattern.compile("^x");

            SchedulingResult res = new SchedulingResult();
            while ((line = reader.readLine()) != null) {
                m1 = p1.matcher(line);
                m2 = p2.matcher(line);
                m3 = p3.matcher(line);
                System.out.println(line);

                // cas "dtm"
                if(m1.find()){
                    String[] token = line.split(" ");
                    double dtm = Double.parseDouble(token[1]);
                    //System.out.println(dtm);
                    res.setDtm(dtm);
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
                    res.addDistance(id, distance);

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
                    res.addMatch(i, j, k);
                }
            }
            return  res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
