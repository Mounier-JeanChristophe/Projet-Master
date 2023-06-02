package Ordonnancement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchedulingResult {

    private double dtm;
    private Map<Integer, Integer> distances;
    private ArrayList<int[]> ordonnancement;

    public SchedulingResult() {
        this.dtm = 0;
        this.distances = new HashMap<>();
        this.ordonnancement = new ArrayList<>();
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
}
