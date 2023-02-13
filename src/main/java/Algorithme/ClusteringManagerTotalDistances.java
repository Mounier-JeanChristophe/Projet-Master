package Algorithme;

import java.util.ArrayList;

public class ClusteringManagerTotalDistances extends ClusteringManager {
    public Double calculCost(ArrayList<ArrayList<Double>> distanceMatrix, ArrayList<ArrayList<Integer>> pools) //Coût du clustering : distance totale du clustering
    {
        return this.calculTotalDistance(distanceMatrix, pools);
    }
}
