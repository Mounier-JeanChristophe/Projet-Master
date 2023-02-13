package Algorithme;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        //Initialisation des équipes
        ArrayList<Equipe> equipeListe = new ArrayList<Equipe>();
        File file = new File(System.getProperty("user.dir") + "/src/main/java/Algorithme/Donnees/Donnees_1.txt");
        Scanner obj = new Scanner(file);

        while (obj.hasNextLine()){
            String[] coordinates = obj.nextLine().split(" ");
            Equipe equipe = new Equipe(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])); //coordonnées x et y
            equipeListe.add(equipe);
        }

        int clusterSize = 10; //nombre d'équipes dans une poule

        Clustering clustering = new Clustering(equipeListe, clusterSize, new ClusteringManagerTotalDistances(), "optimizedFurthestTeams"); //Initialisation du clustering

        clustering.resolutionGloutonne(); //Resolution du clustering avec algorithme glouton
        System.out.println("Coût : " + clustering.getCost()); //Affichage de la distance totale parcourue
        System.out.println("Poules : " + clustering.getPools());

        clustering.setClusteringManager(new ClusteringManagerMinMaxCluster(10000));
        System.out.println("Pire poule après minimisation de la distance moyenne du clustering");
        System.out.println("Coût : " + clustering.getCost()); //Affichage de la distance totale parcourue
        //System.out.println("Poules : " + clustering.getPools());
        clustering.setClusteringManager(new ClusteringManagerTotalDistances());
        System.out.println("Coût total : " + clustering.getCost()); //Affichage de la distance totale parcourue




        clustering = new Clustering(equipeListe, clusterSize, new ClusteringManagerMinMaxCluster(10000), "optimizedFurthestTeams"); //Initialisation du clustering

        clustering.resolutionGloutonne(); //Resolution du clustering avec algorithme glouton
        System.out.println("Pire poule après minimisation du Max");
        System.out.println("Coût : " + clustering.getCost()); //Affichage de la distance totale parcourue
        //System.out.println("Poules : " + clustering.getPools());
        clustering.setClusteringManager(new ClusteringManagerTotalDistances());
        System.out.println("Coût total : " + clustering.getCost()); //Affichage de la distance totale parcourue


    }
}
