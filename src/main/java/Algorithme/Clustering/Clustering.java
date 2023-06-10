package Algorithme.Clustering;

import java.util.ArrayList;
import java.util.Random;

public class Clustering {
    private ArrayList<ArrayList<Integer>> pools = new ArrayList<ArrayList<Integer>>(); //Correspond aux poules (clustering)
    private ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<>(); //Correspond aux poules (clustering)
    private int clusterSize; //taille d'un cluster
    private ArrayList<Equipe> teams;
    private ClusteringManager clusteringManager; //Utilisée pour calculer le coût du clustering (varie en fonction de la classe de clusteringManager)
    private Double cost;

    public Clustering(ArrayList<Equipe> teamsCopy,ArrayList<ArrayList<Double>> distanceMatrix, int clusterSize_Copy, ClusteringManager clusteringManagerCopy, String initialisation) {
        teams = teamsCopy;
        clusterSize = clusterSize_Copy;
        this.clusteringManager = clusteringManagerCopy;
        this.distanceMatrix=distanceMatrix;
        //this.distanceMatrixInitialisation();
        this.hierarchicalClusteringInitialisation(initialisation);
        this.cost = clusteringManager.calculCost(this.distanceMatrix, pools);
    }

    public Clustering(ArrayList<Equipe> teamsCopy, int clusterSize_Copy, ClusteringManager clusteringManagerCopy, String initialisation) {
        teams = teamsCopy;
        clusterSize = clusterSize_Copy;
        this.clusteringManager = clusteringManagerCopy;
        //this.distanceMatrix=distanceMatrix;
        this.distanceMatrixInitialisation();
        this.hierarchicalClusteringInitialisation(initialisation);
        this.cost = clusteringManager.calculCost(this.distanceMatrix, pools);
    }

    private void distanceMatrixInitialisation() {
        for (int i = 0; i < teams.size(); i++) {
            distanceMatrix.add(new ArrayList<Double>()); //Une ligne par ville dans le clustering
        }

        for (int i = 0; i < teams.size(); i++) //Pour toutes les équipes
        {
            for (int j = i; j < teams.size(); j++) //Pour toutes les équipes pas encore gérées
            {
                Double distanceX = teams.get(i).getX() - teams.get(j).getX(); //Distance en abscisses
                Double distanceY = teams.get(i).getY() - teams.get(j).getY(); //Distance en ordonnées
                Double distanceTotale = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY)); //Distance de la ville i à la ville j (Pythagore)
                distanceMatrix.get(i).add(distanceTotale); //Ajout de la distance à la matrice à la ligne i
                if (i != j) {
                    distanceMatrix.get(j).add(distanceTotale); //Ajout de la distance à la matrice à la ligne j
                }
            }
        }
    }

    private void hierarchicalClusteringInitialisation(String initialisation) {
        int teamListSize = teams.size();
        ArrayList<Integer> chosenTeams = new ArrayList<Integer>(); //ArrayList d'equipes ayant été rajoutées au clustering

        for (int i = 0; i < teamListSize; i++) //On initialise l'ArrayList
        {
            chosenTeams.add(0);
        }

        int k = 0; //numéro du cluster

        if (initialisation.equals("optimizedFurthestTeams"))
            optimizedFurthestTeams(teamListSize, chosenTeams, k);
        else if (initialisation.equals("optimizedRandom"))
            optimizedRandom(teamListSize, chosenTeams, k);
        else
            fullRandom(teamListSize, chosenTeams, k);
    }

    public ArrayList<ArrayList<Integer>> resolutionGloutonne() {
        /*if (this.clusteringManager.getClass() == ClusteringManagerTotalDistances.class)
            System.out.println("Algorithme glouton de type minimisation de la distance totale");
        else if (this.clusteringManager.getClass() == ClusteringManagerMinMaxCluster.class)
            System.out.println("Algorithme glouton de type MinMax");
        System.out.println("Poules au départ : " + pools);*/

        double maxDistance = -1; //Détérioration maximale permise (pour un MinMax)
        if (this.clusteringManager.getClass() == ClusteringManagerMinMaxCluster.class)
        {
            int percentageWorsening = ((ClusteringManagerMinMaxCluster) clusteringManager).getPercentageWorsening();
            if (percentageWorsening >= 0)
            {
                Double totalDistance = clusteringManager.calculTotalDistance(distanceMatrix, pools);
                maxDistance = totalDistance + (totalDistance * percentageWorsening / 100);
            }
        }

        boolean baseChange = true; //S'il y a eu au moins un changement dans le clustering
        while (baseChange) {
            baseChange = false;
            for (int k = 0; k < pools.size(); k++) //Pour tous les clusters
            {
                boolean continueWhile = true; //On continue le while avec cette variable
                boolean change = false; //On sait qu'il y a un changement à faire avec celle-ci

                ArrayList<ArrayList<Integer>> poolsChanging = new ArrayList<ArrayList<Integer>>(); //Variable utilisée pour comparer les intervertions avec le clustering originel
                ArrayList<ArrayList<Integer>> poolsTemp = new ArrayList<ArrayList<Integer>>(); //Variable pour copier les futurs changements temporairement
                for (int k2 = 0; k2 < pools.size(); k2++) //Initialisation des clusterings permettant la résolution de l'algorithme
                {
                    poolsChanging.add(new ArrayList<>(pools.get(k2)));
                    poolsTemp.add(new ArrayList<>(pools.get(k2)));
                }

                while (continueWhile) //Tant qu'il y a un changement
                {
                    continueWhile = false;
                    //System.out.println("cluster : " + k);

                    if (change) //S'il y a un changement à faire
                    {
                        for (int k2 = 0; k2 < pools.size(); k2++) //on modifie le clustering
                        {
                            pools.set(k2, new ArrayList<>(poolsTemp.get(k2)));
                            for (int team : pools.get(k2)) {
                                teams.get(team).setPoolNumber(k2);
                            }
                            poolsChanging.set(k2, new ArrayList<>(poolsTemp.get(k2)));
                        }
                    }
                    change = false;

                    Double minCost = clusteringManager.calculCost(distanceMatrix, pools); //Cout du clustering
                    //System.out.println("Cout : " + minCost);

                    for (int j : pools.get(k)) //Pour toutes les équipes du cluster
                    {
                        for (int l = 0; l < teams.size(); l++) //Pour toutes les équipes du clustering
                        {
                            if (!pools.get(k).contains(l)) //Si l'équipe ne fait pas parti du cluster
                            {
                                int clusterOfL = teams.get(l).getPoolNumber(); //Récupération du numéro de poule de l'équipe

                                //Intervertion des équipes dans les différentes poules
                                poolsChanging.get(k).add(l);
                                poolsChanging.get(k).remove(Integer.valueOf(j));
                                poolsChanging.get(clusterOfL).add(j);
                                poolsChanging.get(clusterOfL).remove(Integer.valueOf(l));

                                //Calcul du coût avec cette nouvelle formation du clustering
                                Double newCost = clusteringManager.calculCost(distanceMatrix, poolsChanging);

                                if (newCost < minCost) //Si le coût est plus petit après intervertion
                                {
                                    if (this.clusteringManager.getClass() != ClusteringManagerMinMaxCluster.class || (maxDistance < 0 || clusteringManager.calculTotalDistance(distanceMatrix, poolsChanging) < maxDistance)) //Si la détérioration est permise (pour un MinMax)
                                    {
                                        minCost = newCost;
                                        continueWhile = true;
                                        change = true;
                                        baseChange = true;

                                        for (int k2 = 0; k2 < pools.size(); k2++) //On enregistre les modifications dans une variable
                                        {
                                            poolsTemp.set(k2, new ArrayList<>(poolsChanging.get(k2)));
                                        }
                                    }
                                }

                                for (int k2 = 0; k2 < pools.size(); k2++) //On remet le clustering interverti à l'équivalent du clustering de base
                                {
                                    poolsChanging.set(k2, new ArrayList<>(pools.get(k2)));
                                }
                            }
                        }
                    }
                }
            }
        }

        this.cost = clusteringManager.calculCost(distanceMatrix, pools);
        return pools;
    }

    private void fullRandom(int teamListSize, ArrayList<Integer> chosenTeams, int k) {
        //Pour un clustering de base mal initialisé
/*        int i = 0;
        int j = -1;

        while (i < teams.size())
        {
            if (i%10 == 0)
            {
                j++;
                pools.add(new ArrayList<Integer>());
            }
            pools.get(j).add(i);
            i++;
        }*/

        ArrayList<Integer> teamsToAdd = new ArrayList<Integer>();
        for (int i = 0; i < teams.size(); i++)
        {
            teamsToAdd.add(i);
        }
        Random random = new Random();
        int i = 0;
        int j = -1;
        while (!teamsToAdd.isEmpty())
        {
            int teamToAdd = teamsToAdd.get(random.nextInt(teamsToAdd.size()));
            if (i%clusterSize == 0)
            {
                j++;
                pools.add(new ArrayList<Integer>());
            }
            pools.get(j).add(teamToAdd);
            i++;
            teamsToAdd.remove(Integer.valueOf(teamToAdd));
        }
    }

    private void optimizedFurthestTeams(int teamListSize, ArrayList<Integer> chosenTeams, int k) {
        //Pour un clustering de base initialisé en fonction des équipes les plus éloignées des autres

        for (int i = 0; i < teamListSize /clusterSize; i++) { //Pour toutes les équipes
            int teamToAdd = -1;
            int dist = 0;
            for (int i2 = 0; i2 < teamListSize; i2++) {
                if (chosenTeams.get(i2) == 0) { //Si elle n'est pas encore dans un cluster
                    int tempDist = 0;
                    for (int i3 = 0; i3 < teamListSize; i3++) {
                        tempDist += distanceMatrix.get(i2).get(i3);
                    }
                    if (tempDist > dist) {
                        teamToAdd = i2;
                    }
                }
            }

            pools.add(new ArrayList<Integer>()); //On ajoute un nouveau cluster
            pools.get(k).add(teamToAdd); //On ajoute l'équipe au nouveau cluster
            chosenTeams.set(teamToAdd, 1); //On met l'équipe comme choisie dans un nouveau cluster

            for (int teamAdded = 1; teamAdded < clusterSize; teamAdded++) { //Pour le nombre d'équipes que l'on veut dans un cluster
                int distance = 999999999;
                teamToAdd = -1;

                for (int j = 0; j < teamListSize; j++) { //Pour toutes les équipes
                    if (chosenTeams.get(j) == 0) { //Si elle n'est pas encore dans un cluster
                        int tempDistance = 0; //Variable pour calculer sa distance
                        for (Integer teamFromPool : pools.get(k)) { //On calcule la distance entre l'équipe regardée et toutes les équipes de la poule
                            tempDistance += distanceMatrix.get(j).get(teamFromPool);
                        }
                        if (tempDistance < distance) { //Si la distance vers toutes les autres équipes est plus petite que la plus petite des distances trouvées jusque-là
                            distance = tempDistance; //La nouvelle plus petite distance est celle de cette équipe
                            teamToAdd = j; //Cette équipe est la nouvelle équipe à ajouter
                        }
                    }
                }
                pools.get(k).add(teamToAdd); //On rajoute cette équipe à la poule
                teams.get(teamToAdd).setPoolNumber(k);
                chosenTeams.set(teamToAdd, 1); //L'équipe est mise comme déjà dans une poule
            }
            k++;
        }
    }

    private void optimizedRandom(int teamListSize, ArrayList<Integer> chosenTeams, int k) {
        for (int i = 0; i < teamListSize; i++) { //Pour toutes les équipes
            if (chosenTeams.get(i) == 0) { //Si elle n'est pas encore dans un cluster
                pools.add(new ArrayList<Integer>()); //On ajouter un nouveau cluster
                pools.get(k).add(i); //On ajoute l'équipe au nouveau cluster
                chosenTeams.set(i, 1); //On met l'équipe comme choisie dans un nouveau cluster

                for (int teamAdded = 1; teamAdded < clusterSize; teamAdded++) { //Pour le nombre d'équipes que l'on veut dans un cluster
                    int distance = 999999999;
                    int teamToAdd = -1;

                    for (int j = i + 1; j < teamListSize; j++) { //Pour toutes les équipes
                        if (chosenTeams.get(j) == 0) { //Si elle n'est pas encore dans un cluster
                            int tempDistance = 0; //Variable pour calculer sa distance
                            for (Integer teamFromPool : pools.get(k)) { //On calcule la distance entre l'équipe regardée et toutes les équipes de la poule
                                tempDistance += distanceMatrix.get(j).get(teamFromPool);
                            }
                            if (tempDistance < distance) { //Si la distance vers toutes les autres équipes est plus petite que la plus petite des distances trouvées jusque-là
                                distance = tempDistance; //La nouvelle plus petite distance est celle de cette équipe
                                teamToAdd = j; //Cette équipe est la nouvelle équipe à ajouter
                            }
                        }
                    }
                    pools.get(k).add(teamToAdd); //On rajoute cette équipe à la poule
                    teams.get(teamToAdd).setPoolNumber(k);
                    chosenTeams.set(teamToAdd, 1); //L'équipe est mise comme déjà dans une poule
                }
                k++;
            }
        }
    }

    public void setClusteringManager(ClusteringManager clusteringManager) {
        this.clusteringManager = clusteringManager;
    }

    public ArrayList<ArrayList<Integer>> getPools() {
        return pools;
    }

    public ArrayList<ArrayList<Double>> getDistanceMatrix() {
        return distanceMatrix;
    }

    public Double getCost() {
        return clusteringManager.calculCost(distanceMatrix, pools);
    }

    public Double getTotalDistance(){
        return clusteringManager.calculTotalDistance(distanceMatrix, pools);
    }
}
