package Algorithme;

import java.util.ArrayList;

public class ClusteringManagerMinMaxCluster extends ClusteringManager {
    private int percentageWorsening = -1; //pourcentage de la distance totale que l'on se permet de détériorer. Si plus petit que 0 alors, pas de limite de détérioration

    public ClusteringManagerMinMaxCluster(int percentageWorseningCopy)
    {
        this.percentageWorsening = percentageWorseningCopy;
    }

    public Double calculCost(ArrayList<ArrayList<Double>> distanceMatrix, ArrayList<ArrayList<Integer>> pools) //Coût du clustering : distance du pire cluster
    {
        Double maxDistanceCluster = null;

        for (int k = 0; k < pools.size(); k++) //Pour toutes les poules
        {
            Double clusterDistance = this.getClusterDistance(distanceMatrix, pools, k);

            if (maxDistanceCluster == null || maxDistanceCluster < clusterDistance) //Si distance parcourue dans le cluster est plus grande que celle des précédents
            {
                maxDistanceCluster = clusterDistance; //On stocke la distance parcourue dans le cluster
            }
        }

        return maxDistanceCluster; //Distance maximale d'un cluster
    }

    public int getPercentageWorsening() {
        return percentageWorsening;
    }
}
