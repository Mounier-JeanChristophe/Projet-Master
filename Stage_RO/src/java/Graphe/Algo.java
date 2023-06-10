package Algorithme.Graphe;

import org.decimal4j.util.DoubleRounder;

import java.util.*;

public class Algo {

    private GraphePoids gp;

    public Algo(GraphePoids graphePoids){
        this.gp = graphePoids;
    }

    /*******************************************************
    Algorithme cassant les cycles : calcul estimation
    ******************************************************/
    public void algoEstimation(){
        resetEstimation();
        int size = gp.getSize();

        // 1ere etape -> estimation a 0 pour les arcs nuls et circuits de meme poids
        for(int i=0; i<size; i++){
            for(int j=0; j<i; j++){
                double poids = gp.getMatch(i,j).poids;
                double estimation = gp.getMatch(i,j).estimation;

                if(poids == 0){
                    gp.getMatch(i,j).estimation = 0;
                }
                else if(poids != -1 && estimation == -1){
                    List<Integer> cycle = (poids>0) ? parcoursLargeur(j,i,poids) : parcoursLargeur(i,j,-poids);
                    if(cycle != null){
                        int circuitSize = cycle.size();
                        for(int k=0; k<circuitSize; k++){
                            int i2 = cycle.get(k);
                            int j2 = cycle.get((k+1)%circuitSize);
                            gp.getMatch(i2,j2).estimation = 0;
                        }
                    }
                }
            }
        }

        // 2nd etape -> calcul estimation pour tout arc de poids > 0 et estimation != 0
        for(int i=0; i<size; i++) {
            for (int j = 0; j < i; j++) {
                double poids = gp.getMatch(i,j).poids;
                double estimation = gp.getMatch(i,j).estimation;

                if (poids != -1 && estimation == -1) {
                    List<Integer> cycle = (poids > 0) ? parcoursLargeur(j, i, 1) : parcoursLargeur(i, j, 1);
                    if (cycle != null) {
                        calculEstimationCircuit(cycle);
                    }
                }
            }
        }
    }

    private void calculEstimationCircuit(List<Integer> cycle){
        int size = cycle.size();
        for(int i=0; i<size; i++){
            int j = (i+1)%size;
            calculEstimation(cycle,i,j);
        }
    }

    private void calculEstimation(List<Integer> cycle, int i, int j){
        int valI,valJ,valA,valB,indA,indB,size;
        double sum,poids,max;
        size = cycle.size();
        indA = (i+1)%size;
        indB = (j+1)%size;
        valI = cycle.get(i);
        valJ = cycle.get(j);
        sum = 0;
        max = 0;

        while(indA != i){
            valA = cycle.get(indA);
            valB = cycle.get(indB);
            indA = (indA+1)%size;
            indB = (indB+1)%size;
            poids = (valA > valB) ? -gp.getMatch(valA,valB).poids : gp.getMatch(valA,valB).poids;

            //somme des forces des arcs du chemin de j a i
            sum += poids;

            //max des forces des arcs du chemin de j a i
            if(poids > max){
                max = poids;
            }
        }

        //double Pji = sum / (size - 1);
        double Pji = max;
        double Pij = (valI > valJ) ? -gp.getMatch(valI,valJ).poids : gp.getMatch(valI,valJ).poids;
        gp.getMatch(valI,valJ).estimation = Pij-Pji;
    }
    
    private List<Integer> parcoursLargeur(int start, int stop, double poidsIJ){
        int size = gp.getSize();
        Queue<Integer> queue = new LinkedList<>();
        Integer[] pred = new Integer[size];
        
        for(int i=0; i<size; i++){
            pred[i] = null;
        }
        pred[start] = -1;
        queue.add(start);
        while (!queue.isEmpty()){
            int i = queue.peek();
            for(int j=0; j<size; j++){
                double poids = -1;
                if(i>j){
                    poids = gp.getMatch(i,j).poids;
                }
                else if(i<j){
                    poids = -gp.getMatch(i,j).poids;
                }
                if((poids == poidsIJ || (poidsIJ == 1 && poids > 0)) && pred[j] == null && i != j){
                    pred[j] = i;
                    queue.add(j);
                    if(j==stop){
                        List<Integer> chemin = new ArrayList<>();
                        int k = j;
                        while(k != start){
                            chemin.add(k);
                            k = pred[k];
                        }
                        chemin.add(start);
                        return chemin;
                    }
                }
            }
            queue.remove();
        }
        return null;
    }

    private void resetEstimation(){
        int size = gp.getSize();
        for(int i=1; i<size; i++){
            for(int j=0; j<i; j++){
                gp.getMatch(i,j).estimation = -1;
            }
        }
    }

    /*******************************************************
     Fonction de classement
     ******************************************************/

    public void  getClassement(){
        int size = gp.getSize();
        Map<Integer,Double> classement = new HashMap<>();

        double[] ranking = pageRankBasedAlgorithm();//Page Rank
        for(int i=0; i<size; i++){
            classement.put(i,ranking[i]);
        }

        Comparator<Map.Entry<Integer, Double>> comparator = (o1, o2) -> {
            Double d1 = o1.getValue();
            Double d2 = o2.getValue();
            return d2.compareTo(d1);
        };

        Set<Map.Entry<Integer, Double>> entries = classement.entrySet();
        List<Map.Entry<Integer, Double>> listOfEntries = new ArrayList<>(entries);
        listOfEntries.sort(comparator);
        LinkedHashMap<Integer, Double> sortedByValue = new LinkedHashMap<>(listOfEntries.size());
        for(Map.Entry<Integer, Double> entry : listOfEntries){
            sortedByValue.put(entry.getKey(), entry.getValue());
        }
        Set<Map.Entry<Integer, Double>> data = sortedByValue.entrySet();
        data.forEach(System.out::println);
    }


    /*******************************************************
     Algorithme PageRank
     ******************************************************/

    public double[] pageRank(){
        int size = gp.getSize();
        double df = 0.85;
        double[] ranking = new double[size];

        for(int i=0; i<size; i++){
            ranking[i] = 0;
        }
        System.out.println(Arrays.toString(ranking));
        for(int loop=0; loop<130; loop++) {
            for (int i = 0; i < size; i++) {
                double rankI = (1 - df);
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        double poids = (i > j) ? gp.getMatch(i, j).poids : -gp.getMatch(i, j).poids;
                        if (poids != -1 && poids < 0) {
                            rankI += df*(ranking[j] / getNbSortant(j));
                        }
                    }
                }
                ranking[i] = rankI;
            }
            System.out.println(Arrays.toString(ranking));
        }
        return ranking;
    }

    public int getNbSortant(int i){
        int size = gp.getSize();
        int nb = 0;
        for(int j=0; j<size; j++){
            if(i != j) {
                double poids = gp.getMatch(i, j).poids;
                if (poids != -1) {
                    if ((i > j && poids > 0) || (i < j && -poids > 0)) {
                        nb++;
                    }
                }
            }
        }
        return nb;
    }

    /*******************************************************
     Algorithme PageRank Ameliore
     ******************************************************/

    public double[] getExtDeg(){
        int size = gp.getSize();
        double[] degExt = new double[size];

        for(int i=0; i<size; i++){
            double deg = 0;
            for(int j=0; j<size; j++){
                if(i != j) {
                    double poids = (i > j) ? gp.getMatch(i, j).poids : -gp.getMatch(i, j).poids;
                    if (poids > 0) {
                        deg += poids;
                    }
                }
            }
            degExt[i] = deg;
        }
        return degExt;
    }

    public double[] pageRank2(){
        int size = gp.getSize();
        double[] vect = new double[size];
        double[][] A = new double[size][size];
        double[] degExt = getExtDeg();

        //initialisation du vecteur a 1
        for(int i=0; i<size; i++){
            vect[i] = 1;
        }

        //creation de la matrice stochastique A
        for(int i=0; i<size; i++){
            A[i][i] = 0;
            for(int j=0; j<i; j++){
                double poids = gp.getMatch(i,j).poids;
                if(poids > 0){
                    A[j][i] = poids/degExt[i];
                }
                else if(poids < 0 && poids != -1){
                    A[i][j] = -poids/degExt[j];
                }
                else{
                    A[i][j] = A[j][i] = 0;
                }
            }
        }

        for(int loop = 0; loop<150; loop++) {
            //calcul vecteur
            for (int i = 0; i < size; i++) {
                double sum = 0;
                for (int j = 0; j < size; j++) {
                    sum += A[i][j] * vect[j];
                }
                vect[i] = sum;
            }
            System.out.println(Arrays.toString(vect));
        }
        return vect;
    }

    /*******************************************************
     Algorithme base sur PageRank
     ******************************************************/

    public double[] pageRankBasedAlgorithm(){
        int size = gp.getSize();
        double[] ranking = new double[size];

        for(int i=0; i<size; i++){
            ranking[i] = getScoreI(i);
        }
        return ranking;
    }

    private double getScoreI(int i){
        int size = gp.getSize();
        double phiPlus,phiMoins;
        phiPlus = phiMoins = 0;
        int deg = 0;

        for(int j=0; j<size; j++){
            if(i!=j) {
                double poids = gp.getMatch(i, j).poids;
                if(poids != -1){
                    poids = (i>j) ? poids : -poids;
                    double moyJ = getMoyenne(j);
                    if(poids>0){
                        phiPlus += Math.max(0,poids+moyJ)/(2* Math.log10(4));
                    }
                    else{
                        phiMoins += Math.min(0,poids+moyJ)/(2* Math.log10(4));
                    }
                    deg++;
                }
            }
        }
        return (deg!=0) ? DoubleRounder.round(phiPlus+phiMoins,10)/deg : 0;
    }

    private double getMoyenne(int i){
        int size = gp.getSize();
        double sum = 0;
        double effectif = 0;

        for(int j=0; j<size; j++){
            if(i!=j) {
                double poids = gp.getMatch(i, j).poids;
                if(poids != -1){
                    sum += (i>j) ? poids : -poids;
                    effectif++;
                }
            }
        }
        return (effectif != 0) ? sum/effectif : 0;
    }

    /*******************************************************
     Algorithme calculant les composantes fortement connexes
     ******************************************************/

    /*
     * fonction qui calcule l'ensemble des composantes
     * fortement connexes du graphe
     */
    public void getComposante(GraphePoids gp, int lim){
        int size = gp.getSize();
        gp.composanteList.clear();

        for(int i=0; i<size; i++) {
            if(!isInComposante(gp,i)) {
                List<Integer> desc = descendant(gp,i,lim);
                List<Integer> asc = ascendant(gp,i,lim+1);
                List<Integer> composante = new ArrayList<>();
                for (int node : desc) {
                    if (asc.contains(node)) {
                        composante.add(node);
                    }
                }
                gp.composanteList.add(composante);
            }
        }
        System.out.println(gp.composanteList);
    }

    /*
     * fonction qui retourne la liste de tout les noeuds
     * atteignable depuis n en suivant le sens des arcs
     */
    public List<Integer> descendant(GraphePoids gp, int n, int lim){
        int size = gp.getSize();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> desc = new ArrayList<>();
        Boolean[] visite = new Boolean[size];
        Integer[] nbArc = new Integer[size];

        for(int i=0; i<size; i++){
            visite[i] = false;
        }
        visite[n] = true;
        nbArc[n] = 0;
        desc.add(n);
        queue.add(n);

        while (!queue.isEmpty()){
            int v = queue.peek();
            for(int j=0; j<size; j++){
                if(v>j){
                    double poids = gp.getMatch(v,j).poids;
                    if(poids >=  0 && !visite[j]){
                        visite[j] = true;
                        queue.add(j);
                        nbArc[j] = nbArc[v]+1;
                        if(nbArc[j] < lim && !isInComposante(gp, j)) {
                            desc.add(j);
                        }
                    }
                }
                else if(v<j){
                    double poids = gp.getMatch(j,v).poids;
                    if(poids != -1 && poids <= 0 && !visite[j]){
                        visite[j] = true;
                        queue.add(j);
                        nbArc[j] = nbArc[v]+1;
                        if(nbArc[j] <= lim && !isInComposante(gp, j)) {
                            desc.add(j);
                        }
                    }
                }
            }
            queue.remove();
        }
        return desc;
    }

    /*
     * fonction qui retourne la liste de tout les noeuds
     * atteignable depuis n en suivant le sens inverse des arcs
     */
    public List<Integer> ascendant(GraphePoids gp, int n, int lim){
        int size = gp.getSize();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> asc = new ArrayList<>();
        Boolean[] visite = new Boolean[size];
        Integer[] nbArc = new Integer[size];

        for(int i=0; i<size; i++){
            visite[i] = false;
        }
        visite[n] = true;
        nbArc[n] = 0;
        asc.add(n);
        queue.add(n);

        while (!queue.isEmpty()){

            int v = queue.peek();
            for(int j=0; j<size; j++){
                if(v>j){
                    double poids = gp.getMatch(v,j).poids;
                    if(poids != -1 && poids < 0 && !visite[j]){
                        visite[j] = true;
                        queue.add(j);
                        nbArc[j] = nbArc[v]+1;
                        if(nbArc[j] <= lim && !isInComposante(gp,j)) {
                            asc.add(j);
                        }
                    }
                }
                else if(v<j){
                    double poids = gp.getMatch(j,v).poids;
                    if(poids > 0 && !visite[j]){
                        visite[j] = true;
                        queue.add(j);
                        nbArc[j] = nbArc[v]+1;
                        if(nbArc[j] <= lim && !isInComposante(gp,j)) {
                            asc.add(j);
                        }
                    }
                }

            }
            queue.remove();
        }
        return asc;
    }

    public boolean isInComposante(GraphePoids gp, int i){
        for(List<Integer> list : gp.composanteList){
            if(list.contains(i)){
                return true;
            }
        }
        return false;
    }
}
