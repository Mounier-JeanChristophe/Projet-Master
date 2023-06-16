package Controllers;

import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.ArrayList;

/**
 * Classe servent a la resolution du probleme d assignation des equipes aux poules via un modele lineaire utilisant CPLEX
 */
public class LinearModel {

    private ArrayList<ArrayList<Integer>> pools=new ArrayList<>();
    private double distanceTotale=-1;
    private Integer timeLimit=0;

    /**
     * Retourne les poules
     * @return
     */
    public ArrayList<ArrayList<Integer>> getPools(){
        return pools;
    }

    /***
     * Constructeur qui definit un temps limite d execution
     * @param timeLimit
     */
    public LinearModel(int timeLimit) {
        this.timeLimit=timeLimit;
    }

    /**
     * Constructeur de la classe
     */
    public LinearModel() {
    }

    /**
     * Retourne la somme des distances entre les villes de chaque poule
     * @return
     */
    public double getCost() {
        return distanceTotale;
    }



    /**
     * Lance la resolution du modele lineaire qui cherche la solution du probleme de clustering.
     *
     * Merci a Hernan Caceres pour ses tuto sur cplex en java : https://www.youtube.com/channel/UCTQ0lzvmqnkQKnJRMt1A67g
     * @param dist: matrice des distances entre les villes
     * @param ne: nombre d equipes par poule
     * @return les poules avec les indices des villes assignees a chaque poule si une solution a ete trouve
     */
    public ArrayList<ArrayList<Integer>> launchModel(ArrayList<ArrayList<Double>> dist, int ne){
        System.out.println("Start");
        try {


            // données en entrée
            int nv=dist.size();   // nombre de villes
            int np=nv/ne;   // nombre de poules

            // affichage de la matrice des distances entre les villes
            LinearModel.printDistance(dist,"Matrices des distances:");

            // creation du modele
            IloCplex cplex = new IloCplex();

            if (timeLimit!=0) {
                cplex.setParam(IloCplex.Param.TimeLimit, this.timeLimit);
            }
            // variables du modele
            IloNumVar[][] p = new IloNumVar[np][];
            for (int i=0; i<np; i++) {
                p[i] = cplex.boolVarArray(nv);
            }

            IloNumVar[][] z = new IloNumVar[nv][];
            for (int i=0; i<nv; i++) {
                z[i] = cplex.boolVarArray(nv);
            }


            // fonction objective
            IloLinearNumExpr objective = cplex.linearNumExpr();
            for (int i=0; i<nv; i++) {
                for (int j=i+1; j<nv;j++) {
                    objective.addTerm(z[i][j], dist.get(i).get(j)); //not working
                }
            }
            cplex.addMinimize(objective);

            // contraintes
            // chaque poule a "ne" equipe
            for (int i=0; i<np; i++) {
                cplex.addEq(cplex.sum(p[i]), ne);
            }
            // chaque equipe est affectée a une poule
            for (int i=0;i<nv;i++){
                IloLinearNumExpr expr=cplex.linearNumExpr();
                for (int j=0;j<np;j++){
                    expr.addTerm(1, p[j][i]);
                }
                cplex.addEq(expr,1);
            }

            // z_{ij} >= x_{ik} + x_{jk} - 1
            for (int k=0; k<np; k++) {
                for (int i = 0; i < nv; i++) {
                    for (int j = i + 1; j < nv; j++) {
                        IloLinearNumExpr expr=cplex.linearNumExpr();
                        //expr.setConstant(-1);
                        expr.addTerm(-1,p[k][i]);
                        expr.addTerm(-1,p[k][j]);
                        expr.addTerm(1,z[i][j]);
                        //System.out.println(expr);
                        cplex.addGe(expr,-1);

                    }
                }
            }


            // Pour tout i < j < k
            //
            // y_{jk} >= y_{ij} + y_{ik} - 1
            // y_{ij} >= y_{ik} + y_{jk} - 1
            // y_{ik} >= y_{ij} + y_{jk} - 1
            for (int i=0; i<nv;i++) {
                for (int j = i+1; j < nv; j++) {
                    for (int k = j + 1; k < nv; k++) {
                        // y_{jk} >= y_{ij} + y_{ik} - 1
                        IloLinearNumExpr c1=cplex.linearNumExpr();
                        //expr.setConstant(-1);
                        c1.addTerm(1,z[j][k]);
                        c1.addTerm(-1,z[i][j]);
                        c1.addTerm(-1,z[i][k]);
                        //System.out.println(expr);
                        cplex.addGe(c1,-1);

                        // y_{ij} >= y_{ik} + y_{jk} - 1
                        IloLinearNumExpr c2=cplex.linearNumExpr();
                        //expr.setConstant(-1);
                        c2.addTerm(1,z[i][j]);
                        c2.addTerm(-1,z[i][k]);
                        c2.addTerm(-1,z[j][k]);
                        //System.out.println(expr);
                        cplex.addGe(c2,-1);

                        // y_{ik} >= y_{ij} + y_{jk} - 1
                        IloLinearNumExpr c3=cplex.linearNumExpr();
                        //expr.setConstant(-1);
                        c3.addTerm(1,z[i][k]);
                        c3.addTerm(-1,z[i][j]);
                        c3.addTerm(-1,z[j][k]);
                        //System.out.println(expr);
                        cplex.addGe(c3,-1);

                    }
                }
            }


            cplex.exportModel("clustering.lp");

            // resolution
            if (cplex.solve()) {
                System.out.println("obj = "+Math.round(cplex.getObjValue()));
                distanceTotale = cplex.getObjValue();
                //System.out.println("Poules en brut");
                for (int i=0;i<p.length;i++) {
                    ArrayList<Integer> pool=new ArrayList<>();
                    for (int j=0;j<p[i].length;j++){
                        if ((int) cplex.getValue(p[i][j])==1){
                            pool.add(j);
                        }
                        //System.out.print(cplex.getValue(p[i][j]));
                        //System.out.print("; ");
                    }
                    pools.add(pool);
                    // System.out.println();
                }
//                System.out.println();
//                for (int i=0;i<nv;i++){
//                    for (int j=i+1;j<nv;j++){
//                        System.out.print(cplex.getValue(z[i][j]));
//                        System.out.print("; ");
//                    }
//                    System.out.println();
//                }
                //printModel(cplex,pools,dist);
                return pools;
            }
            else {
                System.out.println("Model not solved");
            }
            cplex.end();
        }
        catch (IloException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Affiche le resultat du modele
     * @param cplex
     * @param pools
     * @param dist
     * @throws IloException
     */
    public static void printModel(IloCplex cplex, ArrayList<ArrayList<Integer>> pools,ArrayList<ArrayList<Integer>> dist) throws IloException {
        LinearModel.print2DList(pools ,"Poules");

        System.out.println("Fct objective");
        String calcul="";
        String calcul2="";
        for (int i=0;i<pools.size();i++){
            for (int j=0;j<pools.get(i).size();j++){
                for (int z=j+1; z<pools.get(i).size();z++) {
                    calcul+=dist.get(pools.get(i).get(j)).get(pools.get(i).get(z)).toString()+"+";
                    calcul2+="dist("+pools.get(i).get(j)+","+pools.get(i).get(z)+")+";

                }
            }
        }

        calcul=calcul.substring(0,calcul.length()-1);
        calcul+="="+Math.round((cplex.getObjValue()));
        System.out.println(calcul);
        calcul2=calcul2.substring(0,calcul2.length()-1);
        calcul2+="="+Math.round((cplex.getObjValue()));
        System.out.println(calcul2);
    }

    /**
     * Affiche un tableau en deux dimensions
     * @param arr
     * @param arrName
     */
    public static void print2DArray(double[][] arr,String arrName){
        System.out.println(arrName);
        for(int i=0;i<arr.length;i++){
            for(int j=0;j<arr.length;j++){
                System.out.print(arr[i][j]);
                System.out.print("; ");
            }
            System.out.println();
        }
    }

    /**
     * Affiche une arraylist en deux dimensions
     * @param list
     * @param listName
     */
    public static void printDistance(ArrayList<ArrayList<Double>> list, String listName){
        System.out.println(listName);
        for(int i=0;i<list.size();i++){
            System.out.print(i+" | ");
            for(int j=0;j<list.get(i).size();j++){
                System.out.print(list.get(i).get(j));
                System.out.print("; ");
            }
            System.out.println();
        }
    }
    
    public static void print2DList(ArrayList<ArrayList<Integer>> list, String listName){
        System.out.println(listName);
        for(int i=0;i<list.size();i++){
            System.out.print(i+" | ");
            for(int j=0;j<list.get(i).size();j++){
                System.out.print(list.get(i).get(j));
                System.out.print("; ");
            }
            System.out.println();
        }
    }
    /**
     * Permet de tester le modele sur un probleme simple
     * @param args
     */
    public static void main(String[] args){

            /*
            double[] v1={0,2,50,20};
            double[] v2={2,0,24,20};
            double[] v3={50,24,0,18};
            double[] v4={20,20,18,0};
            double[][] dist={v1,v2,v3,v4};
            */
        ArrayList<Double> v1=new ArrayList<>();
        v1.add(0.0);
        v1.add(20.0);
        v1.add(2.0);
        v1.add(20.0);
        v1.add(10.0);
        v1.add(15.0);
        ArrayList<Double> v2=new ArrayList<>();
        v2.add(20.0);
        v2.add(0.0);
        v2.add(20.0);
        v2.add(2.0);
        v2.add(15.0);
        v2.add(10.0);
        ArrayList<Double> v3=new ArrayList<>();
        v3.add(2.0);
        v3.add(20.0);
        v3.add(0.0);
        v3.add(20.0);
        v3.add(10.0);
        v3.add(15.0);
        ArrayList<Double> v4=new ArrayList<>();
        v4.add(20.0);
        v4.add(2.0);
        v4.add(20.0);
        v4.add(0.0);
        v4.add(15.0);
        v4.add(10.0);
        ArrayList<Double> v5=new ArrayList<>();
        v5.add(10.0);
        v5.add(15.0);
        v5.add(10.0);
        v5.add(15.0);
        v5.add(0.0);
        v5.add(10.0);
        ArrayList<Double> v6=new ArrayList<>();
        v6.add(15.0);
        v6.add(10.0);
        v6.add(15.0);
        v6.add(10.0);
        v6.add(10.0);
        v6.add(0.0);
        ArrayList<ArrayList<Double>> dist=new ArrayList<>();   // matrice des distances entre les villes
        dist.add(v1);
        dist.add(v2);
        dist.add(v3);
        dist.add(v4);
        dist.add(v5);
        dist.add(v6);

        int ne=3;   // nombre de poules
        LinearModel linearModel=new LinearModel();
        linearModel.launchModel(dist,ne);

    }
}
