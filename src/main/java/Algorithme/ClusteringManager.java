package Algorithme;

import java.util.ArrayList;

public abstract class ClusteringManager {
    public abstract Double calculCost(ArrayList<ArrayList<Double>> distanceMatrix, ArrayList<ArrayList<Integer>> pools); //Calcul du coût du clustering

    public Double calculTotalDistance(ArrayList<ArrayList<Double>> distanceMatrix, ArrayList<ArrayList<Integer>> pools) //Calcul de la distance du clustering
    {
        Double distanceTotale = 0.0;

        for (int k = 0; k < pools.size(); k++) //Pour toutes les poules
        {
            Double clusterDistance = getClusterDistance(distanceMatrix, pools, k);
            distanceTotale += clusterDistance; //On ajoute la distance du cluster à la distance du clustering
        }

        return distanceTotale;
    }

    protected Double getClusterDistance(ArrayList<ArrayList<Double>> distanceMatrix, ArrayList<ArrayList<Integer>> pools, int k) {
        Double clusterDistance = 0.0;
        ArrayList<Integer> teamDistanceCalculated = new ArrayList<>(); //Liste d'équipes où la distance est déjà calculée
        for (int j : pools.get(k)) //Pour toutes les équipes du cluster
        {
            for (int l : pools.get(k)) //Pour toutes les équipes du cluster
            {
                if (l != j && !teamDistanceCalculated.contains(l)) { //Si les équipes ne sont pas les mêmes et que la distance pour l'équipe n'a pas déjà été calculée
                    clusterDistance += distanceMatrix.get(j).get(l); //On ajoute la distance au cluster
                }
            }
            teamDistanceCalculated.add(j); //On ajoute l'équipe aux équipes dont la distance est déjà calculée
        }
        return clusterDistance;
    }
}
