package Algorithme.Graphe;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.GraphParseException;

import java.io.*;
import java.util.List;

public class Main {

    public static BiMap<Integer, String> alphabet;
    public static GraphePoids graphePoids;
    public static Graph graph;
    public static String projectNameGlobal;

    public static void main(String[] args) throws IOException, ClassNotFoundException, GraphParseException {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        /*init();

        boolean end;
        do{
            end = readInput();
        }while(!end);*/
    }


    /* fonction d'initialisation
     * recupere le graphe stocke si le projet existe
     * sinon cree un projet a partir d'un ficher d'entree
     */
    public static void init(String projectName) throws IOException, ClassNotFoundException, GraphParseException {
        projectNameGlobal = projectName;
        initAlphabet();
        //initAlphabetLigue1();
        graph = new MultiGraph("graph");

        /*System.out.print("Charger un projet: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        projectName = reader.readLine();*/

        //si le projet existe on charge les donnees
        if(FileManager.isProjectExist(projectName)){
            //chargement graphePoids
            final FileInputStream fichier = new FileInputStream("project/"+projectName+"/graphePoids.ser");
            ObjectInputStream ois = new ObjectInputStream(fichier);
            graphePoids = (GraphePoids) ois.readObject();
            ois.close();

            //chargement graph (graphstream)
            graph.read("project/"+projectName+"/graph.dgs");
        }
        //sinon on demande un fichier d'entree
        else{
            String input = projectName + ".txt";
            System.out.println("Projet inexistant !");

            graphePoids = new GraphePoids(input, 5);
            graphePoids.loadFile(input);
            makeGraph();
        }
        graph.display();
        updateWeight();
        printOptions();
    }

    /*
     * initialisation de l'alphabet
     * utiliser pour nommer les arcs et sommets
     */
    private static void initAlphabet() {
        alphabet = HashBiMap.create();
        alphabet.put(0, "A");alphabet.put(13, "N");
        alphabet.put(1, "B");alphabet.put(14, "O");
        alphabet.put(2, "C");alphabet.put(15, "P");
        alphabet.put(3, "D");alphabet.put(16, "Q");
        alphabet.put(4, "E");alphabet.put(17, "R");
        alphabet.put(5, "F");alphabet.put(18, "S");
        alphabet.put(6, "G");alphabet.put(19, "T");
        alphabet.put(7, "H");alphabet.put(20, "U");
        alphabet.put(8, "I");alphabet.put(21, "V");
        alphabet.put(9, "J");alphabet.put(22, "W");
        alphabet.put(10, "K");alphabet.put(23, "X");
        alphabet.put(11, "L");alphabet.put(24, "Y");
        alphabet.put(12, "M");alphabet.put(25, "Z");
    }
    private static void initAlphabetLigue1() {
        alphabet = HashBiMap.create();
        alphabet.put(0, "PSG");alphabet.put(13, "SCO");
        alphabet.put(1, "OM");alphabet.put(14, "TROY");
        alphabet.put(2, "ASM");alphabet.put(15, "LOR");
        alphabet.put(3, "REN");alphabet.put(16, "CLM");
        alphabet.put(4, "OCG");alphabet.put(17, "ASSE");
        alphabet.put(5, "RCS");alphabet.put(18, "METZ");
        alphabet.put(6, "LENS");alphabet.put(19, "BRD");
        alphabet.put(7, "OL");alphabet.put(8, "FCN");
        alphabet.put(9, "LOSC");alphabet.put(10, "BRT");
        alphabet.put(11, "RMS");alphabet.put(12, "MHSC");
    }

    /*
     * fonction qui genere un graphe avec graphstream
     * le graphe est genere par rapport au contenu de graphePoids
     * les poids sur les arcs sont en log base 10 afin de pouvoir utiliser
     * l'algorithme de Floyd-Warshall pour la definition d'un classement
     */
    public static void makeGraph(){
        //reset du graphe et definition du CSS utilise pour l'affichage
        graph.clear();
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet","graph {fill-color: black;}");
        String nodeCss = "shape:circle;fill-color: grey; text-size: 15px; text-style: bold; size: 25px; text-alignment: center; text-color: red;";
        String edgeCss = "text-color: blue; fill-color: blue; text-size: 15px; arrow-shape: arrow;" +
                " arrow-size: 20px, 4px; text-background-mode: plain; text-background-color: black; " +
                "text-alignment: along; text-padding: 2px, 2px; text-offset: -4px;";

       //ajout des noeuds
        int size = graphePoids.getSize();
        for(int i=0; i<size; i++){
            graph.addNode(alphabet.get(i));
            Node n = graph.getNode(alphabet.get(i));
            n.setAttribute("ui.style", nodeCss);
            n.setAttribute("ui.label", alphabet.get(i));
        }
        //ajout des arcs
        for(int i=0; i<size; i++){
            for(int j=0; j<i; j++){
                double poids = graphePoids.getMatch(i,j).poids;
                if(poids != -1) {
                    String I = alphabet.get(i);
                    String J = alphabet.get(j);
                    String name = J+I;
                    if(poids == 0){
                        graph.addEdge(name, I, J, false).setAttribute("ui.label", poids);
                        //graph.getEdge(name).setAttribute("weight", 1);
                    }
                    else if(poids > 0){
                        name = alphabet.get(i) + alphabet.get(j);
                        graph.addEdge(name, I, J, true).setAttribute("ui.label", poids);
                        graph.getEdge(name).setAttribute("weight", poids);
                    }
                    else{
                        graph.addEdge(name, J, I, true).setAttribute("ui.label", -poids);
                        graph.getEdge(name).setAttribute("weight", -poids);
                    }
                    graph.getEdge(name).setAttribute("ui.style", edgeCss);
                }
            }
        }
    }

    /*
     * fonction appliquant l'algorithme de Floyd-Warshall
     * sur le graphe
     */
    public static void floydWarshall(){
        APSP apsp = new APSP();
        apsp.init(graph);
        apsp.setWeightAttributeName("weight");
        apsp.setDirected(true);
        apsp.compute();
    }

    /*
     * fonction mettant a jour la matrice triangulaire
     * apres le calcul des plus courts chemins
     */
    public static void updateWeight(){
        floydWarshall();
        int size = graphePoids.getSize();
        for(int i=0; i<size; i++){
            for(int j=0; j<i; j++){
                double poids = graphePoids.getMatch(i,j).poids;
                if(poids != -1){
                    String nI,nJ;
                    nI = alphabet.get(i);
                    nJ = alphabet.get(j);
                    List<Node> path;
                    double newPoids = 0;
                    if(poids > 0){
                        APSPInfo info = (APSPInfo) graph.getNode(nI).getAttribute(APSPInfo.ATTRIBUTE_NAME);
                        path = info.getShortestPathTo(nJ).getNodePath();
                        //System.out.println(nI+" to "+nJ+" -> "+path);
                    }
                    else{
                        APSPInfo info = (APSPInfo) graph.getNode(nJ).getAttribute(APSPInfo.ATTRIBUTE_NAME);
                        path = info.getShortestPathTo(nI).getNodePath();
                        //System.out.println(nJ+" to "+nI+" -> "+path);
                    }
                    int pathSize = path.size()-1;
                    for (int k = 0; k < pathSize; k++) {
                        int I = alphabet.inverse().get(path.get(k).getId());
                        int J = alphabet.inverse().get(path.get(k+1).getId());
                        double poidsAPSP = (I>J) ? graphePoids.getMatch(I,J).poids : -graphePoids.getMatch(I,J).poids;
                        newPoids += poidsAPSP;
                    }
                    if(poids != 0) {
                        graphePoids.getMatch(i, j).prediction = (poids > 0) ? newPoids : -newPoids;
                    }
                    else{
                        graphePoids.getMatch(i, j).prediction = poids;
                    }
                }
            }
        }
    }

    /*
     * fonction de test pour detecter des composantes fortement connexes
     */
    public static void incoherence() {
        int size = graphePoids.getSize();
        double total = 0;
        double cycle = 0;
        for(int j=0; j<size-1; j++) {
            for (int i = j + 1; i < size; i++) {
                double poidsIJ = graphePoids.getMatch(i,j).poids;
                if(poidsIJ != -1) {
                    total++;
                    String nI, nJ;
                    if (poidsIJ > 0) {
                        nI = alphabet.get(i);
                        nJ = alphabet.get(j);
                    } else {
                        nI = alphabet.get(j);
                        nJ = alphabet.get(i);
                    }
                    System.out.print(nI + ">" + nJ + " |--test cycle--> ");
                    APSPInfo info = (APSPInfo) graph.getNode(nJ).getAttribute(APSPInfo.ATTRIBUTE_NAME);
                    Path path = info.getShortestPathTo(nI);
                    System.out.println(path);
                    if (path != null) {
                        cycle++;
                    }
                }
            }
        }
        System.out.println("Pourcentage d'arc dans des composantes fortement connexes: "+(cycle/total)*100+"%" );
    }

    public static boolean readInput() throws IOException {
        System.out.print("cmd:~$ ");
        String input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        input = reader.readLine();

        switch(input){
            //charger un fichier
            case("0"):
                System.out.print("Nom du fichier à charger:");
                input = reader.readLine();
                graphePoids.loadFile(input);
                return false;

            //charger un match
            case("1"):
                System.out.println("Format match: <Equipe I> <Equipe J> <Score I> <Score J>");
                String[] scores = reader.readLine().split(" ");
                if(scores.length == 4) {
                    int i, j, di, dj;
                    i = Integer.parseInt(scores[0]);
                    j = Integer.parseInt(scores[1]);
                    di = Integer.parseInt(scores[2]);
                    dj = Integer.parseInt(scores[3]);
                    graphePoids.loadMatch(i, j, di, dj);
                }
                else{
                    System.out.println("Format incorrect !");
                }
                return false;

            //construire le graphe
            case("2"):
                makeGraph();
                System.out.println("com.example.optimisationcompetitionssportives.Algorithme.Graphe généré");
                return false;

            //afficher le modele (graphePoids)
            case("3"):
                graphePoids.print();
                return false;

            //sauvegarder le projet
            case("4"):
                FileManager.saveData(graphePoids,graph, projectNameGlobal);
                System.out.println("Sauvegarde réussie");
                return false;

            //supprimer le projet
            case("5"):
                FileManager.deleteProject(projectNameGlobal);
                return false;

            //prediction
            case("6"):
                System.out.println("Format: <équipe I> <équipe J>");
                String[] equipes = reader.readLine().split(" ");
                if(equipes.length == 2){
                   int i = Integer.parseInt(equipes[0]);
                   int j = Integer.parseInt(equipes[1]);
                    graphePoids.getPrediction(i,j);
                }
                else{
                    System.out.println("Format incorrect !");
                }
                return false;

            //test
            case("7"):
                Algo algo = new Algo(graphePoids);
                algo.algoEstimation();
                //algo.getClassement();
                graphePoids.getAllPrediction();
                return false;

            //test
            case("8"):
                return true;
        }
        System.out.println("Entrée invalide !");
        return false;
    }

    /*private boolean chargerFichier() throws IOException {
        System.out.print("Nom du fichier à charger:");
        String input = reader.readLine();
        graphePoids.loadFile(input);
        return false;
    }*/

    /*private boolean chargerMatch() throws IOException {
        System.out.println("Format match: <Equipe I> <Equipe J> <Score I> <Score J>");
        String[] scores = reader.readLine().split(" ");
        if(scores.length == 4) {
            int i = Integer.parseInt(scores[0]);
            int j = Integer.parseInt(scores[1]);
            int di = Integer.parseInt(scores[2]);
            int dj = Integer.parseInt(scores[3]);
            graphePoids.loadMatch(i, j, di, dj);
        } else {
            System.out.println("Format incorrect !");
        }
        return false;
    }*/

    public boolean construireGraphe() {
        makeGraph();
        System.out.println("Algorithme.Graphe généré");
        return false;
    }


    //utilisation que pour debug
    public boolean afficherModele() {
        graphePoids.print();
        return false;
    }

    public boolean sauvegarderProjet() {
        FileManager.saveData(graphePoids, graph, projectNameGlobal);
        System.out.println("Sauvegarde réussie");
        return false;
    }

    public boolean supprimerProjet() {
        FileManager.deleteProject(projectNameGlobal);
        return false;
    }

    /*private boolean effectuerPrediction() throws IOException {
        System.out.println("Format: <équipe I> <équipe J>");
        String[] equipes = reader.readLine().split(" ");
        if(equipes.length == 2) {
            int i = Integer.parseInt(equipes[0]);
            int j = Integer.parseInt(equipes[1]);
            graphePoids.getPrediction(i, j);
        } else {
            System.out.println("Format incorrect !");
        }
        return false;
    }*/

    private boolean effectuerTest() {
        Algo algo = new Algo(graphePoids);
        algo.algoEstimation();
        //algo.getClassement();
        graphePoids.getAllPrediction();
        return false;
    }


    public static void printOptions(){
        String[] options = {
                "0- Charger un fichier",
                "1- Charger un match",
                "2- Générer le graphe",
                "3- Afficher le modèle (graphePoids)",
                "4- Sauvegarder le projet",
                "5- Supprimer le projet",
                "6- Prédiction",
                "7- Test",
                "8- Exit",
        };
        System.out.println("Liste des commandes:");
        for(String option : options){
            System.out.println(option);
        }
    }
}


