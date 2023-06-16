package Algorithme.Clustering;

public class Equipe {
    private Double x; //Coordonnées x de l'équipe
    private Double y; //Coordonnées y de l'équipe
    private int poolNumber;

    public Equipe(Double x_copy, Double y_copy){
        x = x_copy;
        y = y_copy;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void setPoolNumber(int poolNumber) {
        this.poolNumber = poolNumber;
    }

    public int getPoolNumber() {
        return poolNumber;
    }
}
