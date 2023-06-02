package Graphe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Poids implements Serializable {

    public List<Double> scoreList;
    public double poids;
    public double estimation;
    public double prediction;

    public Poids(){
        this.scoreList = new ArrayList<>();
        this.poids = 0;
        this.estimation = -1;
        this.prediction = -1;
    }
}
